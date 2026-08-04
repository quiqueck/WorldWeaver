package de.ambertation.wover.biome.impl.data;

import de.ambertation.wover.biome.api.data.BiomeCodecRegistry;
import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.core.api.registry.BuiltInRegistryManager;
import de.ambertation.wover.entrypoint.LibWoverBiome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.KeyDispatchDataCodec;

import java.util.function.Function;
import org.jetbrains.annotations.ApiStatus;

public class BiomeCodecRegistryImpl {
    public static final Registry<MapCodec<? extends BiomeData>> BIOME_CODECS = BuiltInRegistryManager.createRegistry(
            BiomeCodecRegistry.BIOME_CODEC_REGISTRY,
            BiomeCodecRegistryImpl::onBootstrap
    );

    public static final Codec<BiomeData> CODEC = BIOME_CODECS
            .byNameCodec()
            .dispatch(b -> b.codec().codec(), Function.identity());

    public static MapCodec<? extends BiomeData> register(
            Registry<MapCodec<? extends BiomeData>> registry,
            ResourceLocation location,
            KeyDispatchDataCodec<? extends BiomeData> keyDispatchDataCodec
    ) {
        return BuiltInRegistryManager.register(registry, location, keyDispatchDataCodec.codec());
    }

    @ApiStatus.Internal
    public static void initialize() {
        onBootstrap(BIOME_CODECS);
    }

    private static MapCodec<? extends BiomeData> onBootstrap(Registry<MapCodec<? extends BiomeData>> registry) {
        final var biomeData = LibWoverBiome.C.id("vanilla_data");
        if (registry.containsKey(biomeData)) {
            return registry.get(biomeData).orElseThrow().value();
        }

        return register(registry, biomeData, BiomeData.KEY_CODEC);
    }
}
