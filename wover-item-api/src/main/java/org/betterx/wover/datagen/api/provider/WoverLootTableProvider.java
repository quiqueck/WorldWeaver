package org.betterx.wover.datagen.api.provider;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverDataProvider;

import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootTable;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;

public abstract class WoverLootTableProvider implements WoverDataProvider<DataProvider> {
    /**
     * The title of the provider. Mainly used for logging.
     */
    public final String title;

    /**
     * The ModCore instance of the Mod that is providing this instance.
     */
    protected final ModCore modCore;

    protected final ContextKeySet lootContextType;

    public WoverLootTableProvider(
            ModCore modCore,
            ContextKeySet lootContextType
    ) {
        this(modCore, modCore.namespace, lootContextType);
    }

    public WoverLootTableProvider(
            ModCore modCore,
            String title,
            ContextKeySet lootContextType
    ) {
        this.modCore = modCore;
        this.title = title;
        this.lootContextType = lootContextType;
    }

    protected abstract void boostrap(
            @NotNull HolderLookup.Provider lookup,
            @NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer
    );

    @Override
    public DataProvider getProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new LootTableProvider(output, registriesFuture);
    }

    //Based on Fabrics SimpleLootTableProvider. The generate method in that class does not provide access
    //to a HolderLookup.Provider, which is required to get enchantments from the registry.
    private class LootTableProvider implements DataProvider {
        protected final FabricDataOutput output;
        private final CompletableFuture<HolderLookup.Provider> registryLookup;

        public LootTableProvider(
                FabricDataOutput output,
                CompletableFuture<HolderLookup.Provider> registryLookup
        ) {
            this.output = output;
            this.registryLookup = registryLookup;
        }

        @Override
        public @NotNull CompletableFuture<?> run(@NotNull CachedOutput writer) {
            final HashMap<ResourceLocation, LootTable> builders = new HashMap<>();

            return registryLookup.thenCompose(lookup -> {
                boostrap(
                        lookup, (registryKey, builder) -> {
                            if (builders.containsKey(registryKey.location()))
                                throw new IllegalStateException("Duplicate loot table for " + registryKey.location());

                            builders.put(registryKey.location(), builder.setParamSet(lootContextType).build());
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