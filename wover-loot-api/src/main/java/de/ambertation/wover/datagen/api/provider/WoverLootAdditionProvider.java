package de.ambertation.wover.datagen.api.provider;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverDataProvider;
import de.ambertation.wover.loot.api.LootAdditionFile;
import de.ambertation.wover.loot.impl.LootAdditionsImpl;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for datagen providers that emit loot <em>additions</em> - the data form of
 * {@link de.ambertation.wover.loot.api.LootTableAppenders} - to
 * {@code data/<namespace>/wover/loot_addition/<name>.json}.
 *
 * <p>Use this instead of registering a Java appender whenever the addition is static, which is almost always
 * the case: the result is inspectable, overridable by pack authors, and can be gated on another mod being
 * present without any code. The Java API remains for additions that are genuinely dynamic.
 *
 * <p>The builder handed to {@link #bootstrap} offers the same verbs {@link de.ambertation.wover.loot.api.MutableLootTable}
 * does, so converting an existing appender is close to mechanical:
 * <pre>{@code
 * public class MyLootAdditions extends WoverLootAdditionProvider {
 *     public MyLootAdditions(ModCore modCore) {
 *         super(modCore);
 *     }
 *
 *     protected void bootstrap(HolderLookup.Provider lookup, BiConsumer<Identifier, LootAdditionFile.Builder> consumer) {
 *         final LootAdditionFile.Builder file = LootAdditionFile.builder();
 *         file.forTables(BuiltInLootTables.RUINED_PORTAL, BuiltInLootTables.NETHER_BRIDGE)
 *             .addPool(LootPool.lootPool()
 *                              .setRolls(UniformGenerator.between(0, 4))
 *                              .add(LootItem.lootTableItem(MyBlocks.SHINY.asItem()).setWeight(1))
 *                              .add(EmptyLootItem.emptyItem().setWeight(9)));
 *         consumer.accept(modCore.id("portal_chests"), file);
 *     }
 * }
 * }</pre>
 *
 * <p>Register it on a datagen pack like any other provider, e.g.
 * {@code globalPack.addProvider(MyLootAdditions::new)}.
 */
public abstract class WoverLootAdditionProvider implements WoverDataProvider<DataProvider> {
    /**
     * The title of the provider. Mainly used for logging.
     */
    public final String title;

    /**
     * The ModCore instance of the Mod that is providing this instance.
     */
    protected final ModCore modCore;

    /**
     * Creates a new provider, using the mod's namespace as the provider title.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance
     */
    public WoverLootAdditionProvider(ModCore modCore) {
        this(modCore, modCore.namespace + " Loot Additions");
    }

    /**
     * Creates a new provider with a custom title.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance
     * @param title   The title of the provider. Mainly used for logging
     */
    public WoverLootAdditionProvider(ModCore modCore, String title) {
        this.modCore = modCore;
        this.title = title;
    }

    /**
     * Registers this provider's addition files by calling the given consumer once per file.
     *
     * <p>The {@link Identifier} is the file's id: it decides both the path the file is written to
     * ({@code data/<namespace>/wover/loot_addition/<path>.json}) <em>and</em> which files from other packs it
     * merges with, exactly like a tag id does. Two packs that deliberately want to extend the same set of
     * additions use the same id; everything else should use ids of its own.
     *
     * @param lookup   The registry lookup, usable to reference enchantments and other registry content
     * @param consumer Consumer to register an addition file under an id; throws if called twice for the same id
     */
    protected abstract void bootstrap(
            @NotNull HolderLookup.Provider lookup,
            @NotNull BiConsumer<Identifier, LootAdditionFile.Builder> consumer
    );

    /**
     * The datagen counterpart of {@link de.ambertation.wover.loot.api.MutableLootTable#lootTable(ResourceKey)}:
     * a {@link Holder} for another loot table, suitable for
     * {@link net.minecraft.world.level.storage.loot.entries.NestedLootTable#lootTableReference(Holder)}, that
     * serializes as the plain {@code "value": "<namespace>:<path>"} reference the runtime decoder expects.
     * <p>
     * It cannot simply be looked up. The loot table registry is reloadable, and Fabric's data generator builds
     * every reloadable registry <em>empty</em> (see {@code FabricDataGenHelper#addEmptyRegistries}), so
     * {@code lookup.lookupOrThrow(Registries.LOOT_TABLE).getOrThrow(key)} throws for every key - including the
     * mod's own tables emitted by a {@code WoverLootTableProvider} in the very same run. A
     * {@link Holder#direct(Object)} is no use either: {@code RegistryFileCodec} would take its "inline the element"
     * branch and try to serialize a whole loot table we do not have.
     * <p>
     * What the codec needs is a holder that unwraps to its {@link ResourceKey} and passes {@code canSerializeIn}
     * against the loot table lookup the {@link RegistryOps} carries. That is exactly what
     * {@link Holder.Reference#createStandAlone(HolderOwner, ResourceKey)} produces - the same kind of unbound
     * forward reference vanilla's own {@code RegistrySetBuilder} hands out while a registry is still being built -
     * provided its owner is the one that lookup checks identity against.
     * <p>
     * Hence {@link #serializationOwner(HolderLookup.RegistryLookup)}: in datagen the lookup is vanilla's
     * {@code EmptyTagLookupWrapper}, a {@link HolderLookup.RegistryLookup.Delegate} whose {@code canSerialize}
     * forwards to its {@link HolderLookup.RegistryLookup.Delegate#parent() parent}, so a reference owned by the
     * wrapper itself would be rejected. Unwrapping to the terminal parent makes the identity check succeed. The
     * reference is never dereferenced here - encoding only ever reads its key.
     * <p>
     * The reference is not validated at datagen time. If no loot table is registered under {@code key} at runtime,
     * the addition's {@code required} flag does not help - that one is about the addition's <em>target</em> - and
     * vanilla's own loot table validation reports the dangling reference instead.
     *
     * @param lookup the registry lookup handed to {@link #bootstrap}
     * @param key    the loot table to reference
     * @return a holder that encodes as {@code key}'s id
     */
    protected static Holder<LootTable> lootTable(
            @NotNull HolderLookup.Provider lookup,
            @NotNull ResourceKey<LootTable> key
    ) {
        return Holder.Reference.createStandAlone(
                serializationOwner(lookup.lookupOrThrow(Registries.LOOT_TABLE)),
                key
        );
    }

    /**
     * Unwraps a registry lookup to the {@link HolderOwner} its {@code canSerialize} ultimately compares against.
     * <p>
     * {@link HolderLookup.RegistryLookup.Delegate#canSerialize(HolderOwner)} forwards to its parent, and
     * {@link HolderOwner#canSerialize(HolderOwner)} is an identity check, so a holder owned by a delegate is not
     * serializable through that delegate - only one owned by the terminal parent is.
     *
     * @param lookup the lookup to unwrap
     * @param <T>    the registry's element type
     * @return the owner at the end of the delegate chain, or {@code lookup} itself if it is not a delegate
     */
    private static <T> HolderOwner<T> serializationOwner(HolderLookup.RegistryLookup<T> lookup) {
        HolderLookup.RegistryLookup<T> owner = lookup;
        while (owner instanceof HolderLookup.RegistryLookup.Delegate<T> delegate) {
            owner = delegate.parent();
        }
        return owner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataProvider getProvider(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new Provider(output, registriesFuture);
    }

    private class Provider implements DataProvider {
        private final PackOutput.PathProvider pathProvider;
        private final CompletableFuture<HolderLookup.Provider> registryLookup;

        private Provider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
            this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, LootAdditionsImpl.DIRECTORY);
            this.registryLookup = registryLookup;
        }

        @Override
        public @NotNull CompletableFuture<?> run(@NotNull CachedOutput writer) {
            // Insertion-ordered, so the files are written in the order they were declared; the emitted JSON
            // itself is sorted by DataProvider.saveStable, so the output is byte-stable either way.
            final Map<Identifier, LootAdditionFile.Builder> files = new LinkedHashMap<>();

            return registryLookup.thenCompose(lookup -> {
                bootstrap(lookup, (id, builder) -> {
                    if (files.putIfAbsent(id, builder) != null) {
                        throw new IllegalStateException("Duplicate loot addition file for " + id);
                    }
                });

                final RegistryOps<JsonElement> ops = lookup.createSerializationContext(JsonOps.INSTANCE);
                return CompletableFuture.allOf(
                        files.entrySet()
                             .stream()
                             .map(entry -> DataProvider.saveStable(
                                     writer,
                                     LootAdditionFile.CODEC
                                             .encodeStart(ops, entry.getValue().build())
                                             .getOrThrow(msg -> new IllegalStateException(
                                                     entry.getKey() + ": " + msg)),
                                     pathProvider.json(entry.getKey())
                             ))
                             .toArray(CompletableFuture[]::new)
                );
            });
        }

        @Override
        public @NotNull String getName() {
            return title;
        }
    }
}
