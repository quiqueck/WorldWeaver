package de.ambertation.wover.datagen.api.provider;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverDataProvider;

import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootTable;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for datagen providers that generate {@link LootTable}s for a single loot context type (e.g. block
 * drops, entity drops, chest loot), written to {@code data/<namespace>/loot_table/<path>.json}.
 *
 * <p>This is modeled after Fabric's {@code SimpleLootTableProvider}, but exposes a
 * {@link HolderLookup.Provider} to {@link #boostrap(HolderLookup.Provider, BiConsumer)} so subclasses can look up
 * enchantments and other registry-backed content while building loot tables.
 *
 * <p>Subclass this, implement {@link #boostrap(HolderLookup.Provider, BiConsumer)} to register loot table
 * builders, and add the provider to a {@code PackBuilder} from a {@code WoverDataGenEntryPoint}.
 */
public abstract class WoverLootTableProvider implements WoverDataProvider<DataProvider> {
    /**
     * The title of the provider. Mainly used for logging.
     */
    public final String title;

    /**
     * The ModCore instance of the Mod that is providing this instance.
     */
    protected final ModCore modCore;

    /**
     * The loot context type (parameter set) every loot table produced by this provider is validated against.
     */
    protected final ContextKeySet lootContextType;

    /**
     * Creates a new loot table provider, using the mod's namespace as the provider title.
     *
     * @param modCore         The ModCore instance of the Mod that is providing this instance
     * @param lootContextType The loot context type every loot table produced by this provider is validated against
     */
    public WoverLootTableProvider(
            ModCore modCore,
            ContextKeySet lootContextType
    ) {
        this(modCore, modCore.namespace, lootContextType);
    }

    /**
     * Creates a new loot table provider with a custom title.
     *
     * @param modCore         The ModCore instance of the Mod that is providing this instance
     * @param title           The title of the provider. Mainly used for logging
     * @param lootContextType The loot context type every loot table produced by this provider is validated against
     */
    public WoverLootTableProvider(
            ModCore modCore,
            String title,
            ContextKeySet lootContextType
    ) {
        this.modCore = modCore;
        this.title = title;
        this.lootContextType = lootContextType;
    }

    /**
     * Registers this provider's loot tables by calling the given consumer with a resource key and builder for
     * each table. Called once per datagen run, with a registry lookup that becomes available once all registries
     * have finished bootstrapping.
     *
     * @param lookup     The registry lookup, usable to reference enchantments and other registry content
     * @param biConsumer Consumer to register a loot table builder under a resource key; throws if called twice
     *                   for the same key
     */
    protected abstract void boostrap(
            @NotNull HolderLookup.Provider lookup,
            @NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer
    );

    /**
     * {@inheritDoc}
     */
    @Override
    public DataProvider getProvider(
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new LootTableProvider(output, registriesFuture);
    }

    //Based on Fabrics SimpleLootTableProvider. The generate method in that class does not provide access
    //to a HolderLookup.Provider, which is required to get enchantments from the registry.
    private class LootTableProvider implements DataProvider {
        protected final FabricPackOutput output;
        private final CompletableFuture<HolderLookup.Provider> registryLookup;

        public LootTableProvider(
                FabricPackOutput output,
                CompletableFuture<HolderLookup.Provider> registryLookup
        ) {
            this.output = output;
            this.registryLookup = registryLookup;
        }

        @Override
        public @NotNull CompletableFuture<?> run(@NotNull CachedOutput writer) {
            final HashMap<Identifier, LootTable> builders = new HashMap<>();

            return registryLookup.thenCompose(lookup -> {
                boostrap(
                        lookup, (registryKey, builder) -> {
                            if (builders.containsKey(registryKey.identifier()))
                                throw new IllegalStateException("Duplicate loot table for " + registryKey.identifier());

                            builders.put(registryKey.identifier(), builder.setParamSet(lootContextType).build());
                        }
                );

                final RegistryOps<JsonElement> ops = lookup.createSerializationContext(JsonOps.INSTANCE);
                return CompletableFuture.allOf(
                        builders
                                .entrySet()
                                .stream()
                                .map(entry -> DataProvider
                                        .saveStable(
                                                writer,
                                                LootTable.DIRECT_CODEC
                                                        .encodeStart(ops, entry.getValue())
                                                        .getOrThrow(msg -> new IllegalStateException(
                                                                entry.getKey() + ": " + msg)),
                                                output.createRegistryElementsPathProvider(Registries.LOOT_TABLE)
                                                      .json(entry.getKey())
                                        )
                                )
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