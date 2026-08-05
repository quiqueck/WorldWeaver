package de.ambertation.wover.loot.impl;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverLoot;
import de.ambertation.wover.loot.api.LootTableAppender;
import de.ambertation.wover.loot.mixin.LootTableAccessor;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import com.google.common.base.Stopwatch;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Backing store for {@link de.ambertation.wover.loot.api.LootTableAppenders}.
 *
 * <h2>Why this cannot accumulate state</h2>
 * The reloadable loot table registry is rebuilt from scratch on every {@code /reload} and every world load:
 * loading runs again and produces brand-new {@link LootTable} instances parsed from the JSON. Appending to a
 * fresh instance is therefore naturally idempotent - the previous pass's additions were left on objects that
 * are now garbage.
 * <p>
 * That alone would be enough, but it silently depends on "the instance is always fresh", which is exactly the
 * kind of assumption that breaks. So {@link #ORIGINAL_POOLS} / {@link #ORIGINAL_ENTRIES} / {@link #ORIGINAL_CONDITIONS}
 * remember, per <em>instance</em>, the state that instance had when this class first saw it, and every pass rebuilds from
 * that snapshot instead of from the current state. Re-running a pass over an instance that was already
 * extended therefore produces exactly the same result as the first pass, rather than doubling the additions.
 * All three maps are weak and keyed by identity ({@link LootTable} and {@link LootPool} do not override
 * {@code equals}), so they hold nothing alive and vanish with the registry they describe.
 */
public final class LootTableAppendersImpl {
    private LootTableAppendersImpl() {
    }

    /**
     * A single registration. {@code modId} and {@code index} exist purely to make the application order
     * deterministic and independent of Fabric's entrypoint order: sorted by mod id first, then by the order
     * the mod registered its appenders in.
     */
    private record Registration(String modId, int index, LootTableAppender appender) {
        static final Comparator<Registration> ORDER =
                Comparator.comparing(Registration::modId).thenComparingInt(Registration::index);
    }

    private static final Map<ResourceKey<LootTable>, List<Registration>> APPENDERS = new ConcurrentHashMap<>();
    private static final AtomicInteger REGISTRATION_COUNTER = new AtomicInteger();

    /** The pools a {@link LootTable} instance had before we touched it. See the class javadoc. */
    static final Map<LootTable, List<LootPool>> ORIGINAL_POOLS =
            Collections.synchronizedMap(new WeakHashMap<>());

    /** The entries a {@link LootPool} instance had before we touched it. See the class javadoc. */
    static final Map<LootPool, List<LootPoolEntryContainer>> ORIGINAL_ENTRIES =
            Collections.synchronizedMap(new WeakHashMap<>());

    /** The conditions a {@link LootPool} instance had before we touched it. See the class javadoc. */
    static final Map<LootPool, List<LootItemCondition>> ORIGINAL_CONDITIONS =
            Collections.synchronizedMap(new WeakHashMap<>());

    @SafeVarargs
    public static void register(ModCore mod, LootTableAppender appender, ResourceKey<LootTable>... tables) {
        Objects.requireNonNull(mod, "mod");
        Objects.requireNonNull(appender, "appender");
        if (tables == null || tables.length == 0) return;

        final int index = REGISTRATION_COUNTER.getAndIncrement();
        for (ResourceKey<LootTable> table : tables) {
            Objects.requireNonNull(table, "table");
            APPENDERS
                    .computeIfAbsent(table, k -> Collections.synchronizedList(new ArrayList<>()))
                    .add(new Registration(mod.modId, index, appender));
        }
    }

    /**
     * Applies every registered appender to the loot tables of a freshly loaded reloadable registry.
     *
     * @param registries the static registries, with the tags of this reload already applied
     * @param loaded     the reloadable registries that were just parsed from the datapacks
     */
    public static void applyAll(HolderLookup.Provider registries, HolderLookup.Provider loaded) {
        final Map<ResourceKey<LootTable>, List<LootAdditionsImpl.Entry>> datapack = LootAdditionsImpl.current();
        if (APPENDERS.isEmpty() && datapack.isEmpty()) return;

        final Optional<? extends HolderLookup.RegistryLookup<LootTable>> maybeRegistry =
                loaded.lookup(Registries.LOOT_TABLE);
        if (maybeRegistry.isEmpty()) {
            LibWoverLoot.C.LOG.warn("No loot table registry was loaded, skipping {} loot table additions.",
                    APPENDERS.size());
            return;
        }
        final HolderLookup.RegistryLookup<LootTable> registry = maybeRegistry.get();
        final Stopwatch stopwatch = Stopwatch.createStarted();

        int changedTables = 0;
        int addedPools = 0;
        final List<ResourceKey<LootTable>> unknown = new ArrayList<>(0);

        // Deterministic outer order as well, so the log output is stable and reproducible.
        final Set<ResourceKey<LootTable>> touched = new HashSet<>(APPENDERS.keySet());
        touched.addAll(datapack.keySet());
        final List<ResourceKey<LootTable>> keys = new ArrayList<>(touched);
        keys.sort(Comparator.comparing(k -> k.identifier().toString()));

        for (ResourceKey<LootTable> key : keys) {
            final Optional<Holder.Reference<LootTable>> holder = registry.get(key);
            if (holder.isEmpty()) {
                unknown.add(key);
                continue;
            }
            final LootTable table = holder.get().value();
            if (table == LootTable.EMPTY) {
                // A shared singleton - extending it would leak into every table that resolved to it.
                LibWoverLoot.C.LOG.warn("Loot table {} resolved to LootTable.EMPTY, not extending it.", key.identifier());
                continue;
            }

            final List<Registration> registrations = new ArrayList<>();
            final List<Registration> source = APPENDERS.get(key);
            if (source != null) {
                synchronized (source) {
                    registrations.addAll(source);
                }
                registrations.sort(Registration.ORDER);
            }
            final List<LootAdditionsImpl.Entry> additions = datapack.getOrDefault(key, List.of());

            final List<LootPool> original = ORIGINAL_POOLS.computeIfAbsent(
                    table,
                    t -> List.copyOf(((LootTableAccessor) (Object) t).wover_getPools())
            );

            final MutableLootTableImpl view = new MutableLootTableImpl(key, table, registries, registry, original);
            for (Registration registration : registrations) {
                try {
                    registration.appender().appendTo(view);
                } catch (Exception ex) {
                    // One broken mod must not make the world unloadable; the table simply keeps whatever the
                    // other mods (and the datapacks) contributed.
                    LibWoverLoot.C.LOG.error(
                            "Mod '" + registration.modId() + "' failed to append to loot table "
                                    + key.identifier(), ex
                    );
                }
            }

            // Datapack additions run after the Java appenders, so a pack always has the last word: an
            // `add_condition` written in a datapack also gates the pools a mod's Java appender just added,
            // and not the other way round. Within the datapack half the order is file id, then position in
            // the file (see LootAdditionsImpl).
            for (LootAdditionsImpl.Entry entry : additions) {
                try {
                    entry.addition().applyTo(view);
                } catch (Exception ex) {
                    // Same containment as above: a malformed pool index in one pack's file must not take the
                    // whole table - or the world - down with it.
                    LibWoverLoot.C.LOG.error(
                            "The datapack loot addition '" + entry.file() + "' failed to append to loot table "
                                    + key.identifier(), ex
                    );
                }
            }

            if (view.commit()) {
                changedTables++;
                addedPools += view.addedPoolCount();

                // Debug-level, but the wover Logger promotes debug to info in a dev environment - which makes
                // the "did a /reload stack the additions?" question directly observable in the log: these
                // numbers are absolute (pools *now*, not pools added), so they must be identical on every
                // reload and every restart.
                LibWoverLoot.C.LOG.debug(
                        "{}: {} -> {} pools, +{} entries in existing pools, {} pool(s) restricted (from {})",
                        key.identifier(),
                        view.originalPoolCount(),
                        ((LootTableAccessor) (Object) table).wover_getPools().size(),
                        view.addedEntryCount(),
                        view.restrictedPoolCount(),
                        Stream.concat(
                                registrations.stream().map(Registration::modId),
                                additions.stream().map(e -> e.file().toString())
                        ).distinct().toList()
                );
            }
        }

        if (!unknown.isEmpty()) {
            LibWoverLoot.C.LOG.warn(
                    "{} loot table(s) do not exist, nothing was added to them: {}",
                    unknown.size(),
                    unknown.stream().map(k -> k.identifier().toString()).sorted().toList()
            );
        }

        LibWoverLoot.C.LOG.info(
                "Extended {} loot table(s) with {} additional pool(s) in {}ms",
                changedTables, addedPools, stopwatch.stop().elapsed().toMillis()
        );
    }
}
