package org.betterx.wover.biome.impl.data;

import org.betterx.wover.biome.api.data.BiomeCodecRegistry;
import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.core.api.registry.BuiltInRegistryManager;
import org.betterx.wover.entrypoint.WoverBiome;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.KeyDispatchDataCodec;

import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;

public class BiomeCodecRegistryImpl {
    public static final Registry<Codec<? extends BiomeData>> BIOME_CODECS = BuiltInRegistryManager.register(
            BiomeCodecRegistry.BIOME_CODEC_REGISTRY,
            BiomeCodecRegistryImpl::onBootstrap
    );
    
    public static final Codec<BiomeData> CODEC = BIOME_CODECS
            .byNameCodec()
            .dispatch(b -> b.codec().codec(), Function.identity());

    public static Codec<? extends BiomeData> register(
            Registry<Codec<? extends BiomeData>> registry,
            ResourceLocation location,
            KeyDispatchDataCodec<? extends BiomeData> keyDispatchDataCodec
    ) {
        return Registry.register(registry, location, keyDispatchDataCodec.codec());
    }

    @ApiStatus.Internal
    public static void initialize() {
        onBootstrap(BIOME_CODECS);
    }

    private static Codec<? extends BiomeData> onBootstrap(Registry<Codec<? extends BiomeData>> registry) {
        final var biomeData = WoverBiome.C.id("data");
        if (registry.containsKey(biomeData)) {
            return registry.get(biomeData);
        }
//        register(registry, WoverBiome.C.id("not"), Not.CODEC);
//        register(registry, WoverBiome.C.id("and"), And.CODEC);
//        register(registry, WoverBiome.C.id("or"), Or.CODEC);
//
//        register(registry, WoverBiome.C.id("is_biome"), IsBiome.CODEC);
//        register(registry, WoverBiome.C.id("has_tag"), HasTag.CODEC);
//        register(registry, WoverBiome.C.id("in_dimension"), InDimension.CODEC);
//        register(registry, WoverBiome.C.id("is_namespace"), IsNamespace.CODEC);
//        register(registry, WoverBiome.C.id("spawns"), Spawns.CODEC);
//        register(registry, WoverBiome.C.id("has_structure"), HasStructure.CODEC);
//        register(registry, WoverBiome.C.id("has_placed_feature"), HasPlacedFeature.CODEC);
//        register(registry, WoverBiome.C.id("has_configured_feature"), HasConfiguredFeature.CODEC);
//
        return register(registry, biomeData, BiomeData.KEY_CODEC);
    }
}
