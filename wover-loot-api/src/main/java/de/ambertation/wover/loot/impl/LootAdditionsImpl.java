package de.ambertation.wover.loot.impl;

import de.ambertation.wover.entrypoint.LibWoverLoot;
import de.ambertation.wover.loot.api.LootAddition;
import de.ambertation.wover.loot.api.LootAdditionFile;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.LootTable;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;

import java.io.Reader;
import java.util.*;
import java.util.stream.Stream;

/**
 * Loads {@code data/<namespace>/wover/loot_addition/<name>.json} from the datapacks and turns it into the very
 * same additions {@link de.ambertation.wover.loot.api.LootTableAppenders} produces in Java.
 *
 * <h2>Why this is not a datapack registry</h2>
 * A datapack registry entry is replaced wholesale, which is precisely the problem loot additions exist to work
 * around - two packs contributing to the same table have to <em>merge</em>, not overwrite. So this is a plain
 * resource load, with the merge semantics of a vanilla tag file: every pack's file for one id is read in pack
 * order, {@code replace: true} discards what accumulated, and the default appends. See
 * {@link LootAdditionFile} for the format and the full merge rules.
 *
 * <h2>Where it runs</h2>
 * Both halves live on {@code ReloadableServerRegistries}:
 * <ul>
 *     <li>{@code reload(layers, pendingTags, resourceManager, executor)} is the only place the
 *     {@link ResourceManager} of the reload in flight is reachable, so its {@code HEAD} parks it in
 *     {@link #PENDING}, keyed by the {@code layers} instance;</li>
 *     <li>{@code createAndValidateFullContext(layers, lookupWithUpdatedTags, newRegistries)} - which vanilla
 *     reaches asynchronously, on a different thread, with the <em>same</em> {@code layers} instance - takes it
 *     back out and decodes. Decoding can only happen here, not at {@code reload} HEAD: a
 *     {@code minecraft:loot_table} entry nested into an addition resolves against a registry that does not
 *     exist yet when {@code reload} starts. That is why the datapack path is not a pure decode.</li>
 * </ul>
 * The decoded result is kept in {@link #CURRENT} and re-applied, unchanged, by every
 * {@link LootTableAppendersImpl#applyAll} pass - which is what makes re-application idempotent for the
 * datapack path in exactly the same way it is for the Java path.
 */
public final class LootAdditionsImpl {
    private LootAdditionsImpl() {
    }

    /** The datapack directory additions are read from, relative to {@code data/<namespace>/}. */
    public static final String DIRECTORY = "wover/loot_addition";

    private static final FileToIdConverter CONVERTER = FileToIdConverter.json(DIRECTORY);

    /**
     * The {@link ResourceManager} of a reload that has started but not yet reached
     * {@code createAndValidateFullContext}. Keyed by the {@code LayeredRegistryAccess} both methods receive -
     * {@code LayeredRegistryAccess} overrides neither {@code equals} nor {@code hashCode}, so this is an
     * identity map, and it is weak so a reload that somehow never completes leaks nothing.
     */
    private static final Map<Object, ResourceManager> PENDING =
            Collections.synchronizedMap(new WeakHashMap<>());

    /** The additions of the most recent reload, per target table, already in application order. */
    private static volatile Map<ResourceKey<LootTable>, List<Entry>> CURRENT = Map.of();

    /**
     * One addition, together with the file it came from (for log output) and its position in the global,
     * deterministic order (file id first, then position within the file).
     */
    record Entry(Identifier file, int order, LootAddition addition) {
        static final Comparator<Entry> ORDER = Comparator.comparingInt(Entry::order);
    }

    /**
     * Parks the {@link ResourceManager} of a reload that just started; called from
     * {@code ReloadableServerRegistries.reload}'s {@code HEAD}.
     *
     * @param layers          the registry layers of this reload, used as the correlation key
     * @param resourceManager the resource manager of this reload
     */
    public static void beginReload(Object layers, ResourceManager resourceManager) {
        if (layers == null || resourceManager == null) return;
        PENDING.put(layers, resourceManager);
    }

    /**
     * Reads and decodes every {@code wover/loot_addition} file of the reload that {@code layers} belongs to.
     * Called from {@code ReloadableServerRegistries.createAndValidateFullContext}'s {@code HEAD}, right before
     * {@link LootTableAppendersImpl#applyAll}.
     *
     * @param layers     the registry layers of this reload, as handed to {@code beginReload}
     * @param registries the static registries, with the tags of this reload already applied
     * @param loaded     the reloadable registries that were just parsed from the datapacks
     */
    public static void parse(Object layers, HolderLookup.Provider registries, HolderLookup.Provider loaded) {
        final ResourceManager resourceManager = layers == null ? null : PENDING.remove(layers);
        if (resourceManager == null) {
            // Only reachable if the reload() hook did not run - a mixin problem, not a datapack problem. Drop
            // the previous reload's additions rather than silently re-applying stale ones.
            if (!CURRENT.isEmpty()) {
                LibWoverLoot.C.LOG.warn(
                        "No ResourceManager was captured for this reload; dropping all datapack loot additions.");
            }
            CURRENT = Map.of();
            return;
        }
        CURRENT = load(resourceManager, registries, loaded);
    }

    /**
     * @return the additions of the most recent reload, keyed by the table they extend
     */
    static Map<ResourceKey<LootTable>, List<Entry>> current() {
        return CURRENT;
    }

    /**
     * Reads, condition-filters, merges and decodes all addition files.
     *
     * @param resourceManager the resource manager of this reload
     * @param registries      the static registries, with the tags of this reload already applied
     * @param loaded          the reloadable registries that were just parsed from the datapacks
     * @return the additions, keyed by the table they extend, each list in application order
     */
    private static Map<ResourceKey<LootTable>, List<Entry>> load(
            ResourceManager resourceManager,
            HolderLookup.Provider registries,
            HolderLookup.Provider loaded
    ) {
        // The lookup an addition is decoded against has to span *both* halves: the static registries (items,
        // blocks, enchantments, ...) and the reloadable ones that were just parsed (loot_table). This is the
        // same concatenation vanilla builds two statements later in createAndValidateFullContext, so a nested
        // loot table reference resolves exactly as it would inside a real loot table JSON.
        final HolderLookup.Provider full = HolderLookup.Provider.create(
                Stream.concat(registries.listRegistries(), loaded.listRegistries())
        );
        final RegistryOps.RegistryInfoLookup infoLookup = new RegistryOps.RegistryInfoLookup() {
            @Override
            public <T> Optional<RegistryOps.RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> key) {
                return full.lookup(key).map(RegistryOps.RegistryInfo::fromRegistryLookup);
            }
        };
        final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, infoLookup);

        final Optional<? extends HolderLookup.RegistryLookup<LootTable>> lootTables =
                loaded.lookup(Registries.LOOT_TABLE);

        final Map<Identifier, List<Resource>> stacks = CONVERTER.listMatchingResourceStacks(resourceManager);
        // Sorted, so that the order two different files apply in is stable across runs and machines.
        final List<Identifier> fileIds = new ArrayList<>(stacks.keySet());
        fileIds.sort(Comparator.comparing(Identifier::toString));

        final Map<ResourceKey<LootTable>, List<Entry>> result = new HashMap<>();
        final List<String> missingRequired = new ArrayList<>(0);
        int order = 0;
        int fileCount = 0;
        int additionCount = 0;

        for (Identifier fileId : fileIds) {
            final Identifier name = CONVERTER.fileToId(fileId);
            final List<LootAdditionFile> stack = new ArrayList<>();

            // Pack order, lowest priority first - exactly how TagLoader walks a tag's resource stack.
            for (Resource resource : stacks.get(fileId)) {
                final LootAdditionFile file = decode(name, resource, ops);
                if (file == null) continue;
                if (!conditionsMet(name, resource, file.conditions(), infoLookup)) continue;
                stack.add(file);
            }
            if (stack.isEmpty()) continue;
            fileCount++;
            final List<LootAddition> merged = merge(stack);

            for (LootAddition addition : merged) {
                additionCount++;
                for (ResourceKey<LootTable> target : addition.targets()) {
                    final boolean exists = lootTables.map(r -> r.get(target).isPresent()).orElse(false);
                    if (!exists) {
                        // Same shape as a vanilla tag entry: `required: false` means "skip if absent", which
                        // is what an addition aimed at another mod's table wants. A missing *required* target
                        // is loud but never fatal - one broken pack must not make the world unloadable.
                        if (addition.required()) {
                            missingRequired.add(name + " -> " + target.identifier());
                        } else {
                            LibWoverLoot.C.LOG.debug(
                                    "{}: optional target {} does not exist, skipping.",
                                    name, target.identifier()
                            );
                        }
                        continue;
                    }
                    result.computeIfAbsent(target, k -> new ArrayList<>())
                          .add(new Entry(name, order, addition));
                }
                order++;
            }
        }

        if (!missingRequired.isEmpty()) {
            LibWoverLoot.C.LOG.error(
                    missingRequired.size() + " datapack loot addition(s) target a loot table that does not"
                            + " exist and were skipped. Add \"required\": false to make this silent: "
                            + missingRequired
            );
        }

        for (List<Entry> entries : result.values()) {
            entries.sort(Entry.ORDER);
        }

        if (fileCount > 0) {
            LibWoverLoot.C.LOG.info(
                    "Loaded {} datapack loot addition(s) from {} file(s) in data/*/{}, targeting {} loot table(s)",
                    additionCount, fileCount, DIRECTORY, result.size()
            );
        }
        return Map.copyOf(result);
    }

    private static LootAdditionFile decode(Identifier name, Resource resource, RegistryOps<JsonElement> ops) {
        try (Reader reader = resource.openAsReader()) {
            final JsonElement json = JsonParser.parseReader(reader);
            return LootAdditionFile.CODEC
                    .parse(ops, json)
                    .getOrThrow(msg -> new IllegalStateException(msg));
        } catch (Exception ex) {
            LibWoverLoot.C.LOG.error(
                    "Could not read the loot addition '" + name + "' from pack '"
                            + resource.sourcePackId() + "', skipping it.", ex
            );
            return null;
        }
    }

    /**
     * Evaluates the file's {@code fabric:load_conditions}. A file whose conditions are not met is treated as
     * if it were not present at all - it contributes nothing, and its {@code replace} does not fire either.
     */
    private static boolean conditionsMet(
            Identifier name,
            Resource resource,
            List<ResourceCondition> conditions,
            RegistryOps.RegistryInfoLookup infoLookup
    ) {
        for (ResourceCondition condition : conditions) {
            final boolean met;
            try {
                met = condition.test(infoLookup);
            } catch (Exception ex) {
                LibWoverLoot.C.LOG.error(
                        "Resource condition " + condition.getType().id() + " of the loot addition '" + name
                                + "' (pack '" + resource.sourcePackId() + "') threw, skipping the file.", ex
                );
                return false;
            }
            if (!met) {
                LibWoverLoot.C.LOG.debug(
                        "{} (pack '{}') is disabled by the resource condition {}.",
                        name, resource.sourcePackId(), condition.getType().id()
                );
                return false;
            }
        }
        return true;
    }

    /**
     * The merge step of {@link #load}, factored out so it can be exercised directly: applies the vanilla-tag
     * semantics to the files one id was found in, in pack order.
     *
     * @param stack the files for one id, lowest-priority pack first; files whose conditions are unmet must
     *              already have been removed
     * @return the additions that id contributes
     */
    public static List<LootAddition> merge(List<LootAdditionFile> stack) {
        final List<LootAddition> merged = new ArrayList<>();
        for (LootAdditionFile file : stack) {
            if (file.replace()) merged.clear();
            merged.addAll(file.additions());
        }
        return List.copyOf(merged);
    }
}
