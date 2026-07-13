package org.betterx.wover.biome.api.data;

import org.betterx.wover.biome.impl.data.BiomeCodecRegistryImpl;
import org.betterx.wover.core.api.registry.DatapackRegistryBuilder;
import org.betterx.wover.entrypoint.LibWoverSurface;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.KeyDispatchDataCodec;

/**
 * A built-in registry of the {@link MapCodec}s that can be used to (de)serialize a subclass of
 * {@link BiomeData}.
 * <p>
 * {@link BiomeData#codec()} dispatches to the {@link MapCodec} registered here for the concrete
 * {@link BiomeData} subtype, similar to how vanilla dispatches {@link net.minecraft.world.level.levelgen.feature.Feature}
 * or {@link net.minecraft.world.level.levelgen.placement.PlacementModifier} instances. Custom
 * {@link BiomeData} subclasses need to {@link #register(ResourceLocation, KeyDispatchDataCodec)} their codec
 * here before they can be loaded from a datapack.
 */
public class BiomeCodecRegistry {
    /**
     * The Key of the Registry. ({@code wover/biome_codec})
     */
    public static final ResourceKey<Registry<MapCodec<? extends BiomeData>>> BIOME_CODEC_REGISTRY =
            DatapackRegistryBuilder.createRegistryKey(LibWoverSurface.C.id("wover/biome_codec"));

    /**
     * The Registry itself.
     */
    public static final Registry<MapCodec<? extends BiomeData>> BIOME_CODECS = BiomeCodecRegistryImpl.BIOME_CODECS;

    /**
     * Registers a new {@link MapCodec} for a {@link BiomeData} subtype.
     *
     * @param location             The id of the {@link BiomeData} subtype.
     * @param keyDispatchDataCodec The {@link KeyDispatchDataCodec} of the {@link BiomeData} subtype.
     * @return The registered {@link MapCodec}.
     */
    public static MapCodec<? extends BiomeData> register(
            ResourceLocation location,
            KeyDispatchDataCodec<? extends BiomeData> keyDispatchDataCodec
    ) {
        return BiomeCodecRegistryImpl.register(BiomeCodecRegistryImpl.BIOME_CODECS, location, keyDispatchDataCodec);
    }
}
