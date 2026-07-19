package org.betterx.wover.surface.impl.numeric;

import org.betterx.wover.entrypoint.LibWoverSurface;
import org.betterx.wover.surface.api.noise.NumericProvider;
import org.betterx.wover.surface.api.noise.NumericProviderRegistry;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceKey;

import org.jetbrains.annotations.ApiStatus;

public class NumericProviderRegistryImpl {
    public static final ResourceKey<MapCodec<? extends NumericProvider>> RANDOM_INT
            = NumericProviderRegistry.createKey(LibWoverSurface.C.id("rnd_int"));
    public static final ResourceKey<MapCodec<? extends NumericProvider>> NETHER_NOISE
            = NumericProviderRegistry.createKey(LibWoverSurface.C.id("nether_noise"));

    public static void register(
            ResourceKey<MapCodec<? extends NumericProvider>> key,
            MapCodec<? extends NumericProvider> codec
    ) {
        NumericProviderRegistry.register(key, codec);
    }

    @ApiStatus.Internal
    public static void bootstrap() {
        register(RANDOM_INT, RandomIntProvider.CODEC);
        register(NETHER_NOISE, NetherNoiseCondition.CODEC);
    }
}
