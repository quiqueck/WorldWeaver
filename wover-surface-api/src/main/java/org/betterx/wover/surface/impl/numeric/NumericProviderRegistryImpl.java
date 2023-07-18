package org.betterx.wover.surface.impl.numeric;

import org.betterx.wover.entrypoint.WoverSurface;
import org.betterx.wover.legacy.api.LegacyHelper;
import org.betterx.wover.surface.api.noise.NumericProvider;
import org.betterx.wover.surface.api.noise.NumericProviderRegistry;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceKey;

import org.jetbrains.annotations.ApiStatus;

public class NumericProviderRegistryImpl {

    static final ResourceKey<Codec<? extends NumericProvider>> RANDOM_INT
            = NumericProviderRegistry.createKey(WoverSurface.C.id("rnd_int"));
    static final ResourceKey<Codec<? extends NumericProvider>> NETHER_NOISE
            = NumericProviderRegistry.createKey(WoverSurface.C.id("nether_noise"));

    public static void registerWithBCLib(
            ResourceKey<Codec<? extends NumericProvider>> key,
            Codec<? extends NumericProvider> codec
    ) {
        NumericProviderRegistry.register(key, codec);
        NumericProviderRegistry.register(
                LegacyHelper.BCLIB_CORE.convertNamespace(key.location()),
                LegacyHelper.wrap(codec)
        );
    }

    @ApiStatus.Internal
    public static void bootstrap() {
        registerWithBCLib(RANDOM_INT, RandomIntProvider.CODEC);
        registerWithBCLib(NETHER_NOISE, NetherNoiseCondition.CODEC);
    }
}
