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
    public static final Registry<Codec<? extends BiomeData>> BIOME_CODECS = BuiltInRegistryManager.createRegistry(
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
        return BuiltInRegistryManager.register(registry, location, keyDispatchDataCodec.codec());
    }

    @ApiStatus.Internal
    public static void initialize() {
        onBootstrap(BIOME_CODECS);
    }

    private static Codec<? extends BiomeData> onBootstrap(Registry<Codec<? extends BiomeData>> registry) {
        final var biomeData = WoverBiome.C.id("vanilla_data");
        if (registry.containsKey(biomeData)) {
            return registry.get(biomeData);
        }

        return register(registry, biomeData, BiomeData.KEY_CODEC);
    }
}
