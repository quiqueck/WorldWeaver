package org.betterx.wover.biome.api.data;

import org.betterx.wover.biome.impl.data.BiomeCodecRegistryImpl;
import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.entrypoint.WoverSurface;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.KeyDispatchDataCodec;

public class BiomeCodecRegistry {
    public static final ResourceKey<Registry<Codec<? extends BiomeData>>> BIOME_CODEC_REGISTRY =
            DatapackRegistryBuilder.createRegistryKey(WoverSurface.C.id("wover/biome_codec"));

    public static final Registry<Codec<? extends BiomeData>> BIOME_CODECS = BiomeCodecRegistryImpl.BIOME_CODECS;

    public static Codec<? extends BiomeData> register(
            ResourceLocation location,
            KeyDispatchDataCodec<? extends BiomeData> keyDispatchDataCodec
    ) {
        return BiomeCodecRegistryImpl.register(BiomeCodecRegistryImpl.BIOME_CODECS, location, keyDispatchDataCodec);
    }
}
