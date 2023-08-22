package org.betterx.wover.biome.impl.data;

import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeDataRegistry;
import org.betterx.wover.common.registry.api.CustomRegistryData;
import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.entrypoint.WoverBiome;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.events.impl.EventImpl;
import org.betterx.wover.state.api.WorldState;

import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;

public class BiomeDataRegistryImpl {
    public static final EventImpl<OnBootstrapRegistry<BiomeData>> BOOTSTRAP_BIOME_DATA_REGISTRY
            = new EventImpl<>("BOOTSTRAP_BIOME_DATA_REGISTRY");

    private static void onBootstrap(BootstapContext<BiomeData> ctx) {
        BOOTSTRAP_BIOME_DATA_REGISTRY.emit(c -> c.bootstrap(ctx));
    }

    private static final CustomRegistryData.DataKey<Map<ResourceKey<BiomeData>, BiomeData>> TEMP_BIOME_DATA
            = new CustomRegistryData.DataKey<>(WoverBiome.C.id("temp_biome_data"));

    public static BiomeData getFromRegistryOrTemp(ResourceKey<Biome> key) {
        return getFromRegistryOrTemp(key, BiomeData::of);
    }

    public static BiomeData getFromRegistryOrTemp(
            ResourceKey<Biome> key,
            Function<ResourceKey<Biome>, BiomeData> factory
    ) {
        if (WorldState.allStageRegistryAccess() == null) return null;
        final Registry<BiomeData> registry = WorldState.allStageRegistryAccess()
                                                       .registryOrThrow(BiomeDataRegistry.BIOME_DATA_REGISTRY);
        return getFromRegistryOrTemp(registry, key, factory);
    }

    public static BiomeData getFromRegistryOrTemp(
            Registry<BiomeData> registry,
            ResourceKey<Biome> key
    ) {
        return getFromRegistryOrTemp(registry, key, BiomeData::of);
    }

    public static BiomeData getFromRegistryOrTemp(
            Registry<BiomeData> registry,
            ResourceKey<Biome> key,
            Function<ResourceKey<Biome>, BiomeData> factory
    ) {
        final ResourceKey<BiomeData> dataKey = createKey(key.location());
        if (registry != null) {
            final Optional<BiomeData> oData = registry.getOptional(dataKey);

            if (oData.isPresent()) {
                return oData.get();
            }
        }

        if (registry instanceof CustomRegistryData tempRegistryData) {
            Map<ResourceKey<BiomeData>, BiomeData> customData = tempRegistryData.wover_computeDataIfAbsent(
                    TEMP_BIOME_DATA,
                    k -> new HashMap<>()
            );

            return customData.computeIfAbsent(dataKey, k -> BiomeData.of(key));
        }

        return factory.apply(key);
    }

    @ApiStatus.Internal
    public static void initialize() {
        DatapackRegistryBuilder.register(
                BiomeDataRegistry.BIOME_DATA_REGISTRY,
                BiomeCodecRegistryImpl.CODEC,
                BiomeDataRegistryImpl::onBootstrap
        );
    }

    public static ResourceKey<BiomeData> createKey(
            ResourceLocation ruleID
    ) {
        return ResourceKey.create(
                BiomeDataRegistry.BIOME_DATA_REGISTRY,
                ruleID
        );
    }

}
