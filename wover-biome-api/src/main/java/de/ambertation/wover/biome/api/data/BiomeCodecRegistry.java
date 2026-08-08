package de.ambertation.wover.biome.api.data;

import de.ambertation.wover.biome.impl.data.BiomeCodecRegistryImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

/**
 * A built-in registry of the {@link MapCodec}s that can be used to (de)serialize a subclass of
 * {@link BiomeData}.
 * <p>
 * {@link BiomeData#codec()} dispatches to the {@link MapCodec} registered here for the concrete
 * {@link BiomeData} subtype, similar to how vanilla dispatches {@link net.minecraft.world.level.levelgen.feature.Feature}
 * or {@link net.minecraft.world.level.levelgen.placement.PlacementModifier} instances. Custom
 * {@link BiomeData} subclasses need to {@link #register(Identifier, MapCodec)} their codec
 * here before they can be loaded from a datapack.
 */
public class BiomeCodecRegistry {
    /**
     * The Key of the Registry. ({@code wover/biome_codec})
     */
    public static final ResourceKey<Registry<MapCodec<? extends BiomeData>>> BIOME_CODEC_REGISTRY =
            BiomeCodecRegistryImpl.BIOME_CODEC_REGISTRY;

    /**
     * The Registry itself.
     */
    public static final Registry<MapCodec<? extends BiomeData>> BIOME_CODECS = BiomeCodecRegistryImpl.BIOME_CODECS;

    /**
     * Registers a new {@link MapCodec} for a {@link BiomeData} subtype.
     * <p>
     * 26.3 removed {@code KeyDispatchDataCodec}, which used to be nothing but a wrapper around the
     * {@link MapCodec} this registry actually stores, so the codec is now passed directly.
     *
     * @param location The id of the {@link BiomeData} subtype.
     * @param codec    The {@link MapCodec} of the {@link BiomeData} subtype.
     * @return The registered {@link MapCodec}.
     */
    public static MapCodec<? extends BiomeData> register(
            Identifier location,
            MapCodec<? extends BiomeData> codec
    ) {
        return BiomeCodecRegistryImpl.register(BiomeCodecRegistryImpl.BIOME_CODECS, location, codec);
    }
}
