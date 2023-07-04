package org.betterx.wover.surface.impl.numeric;

import org.betterx.wover.entrypoint.WoverSurface;
import org.betterx.wover.legacy.api.LegacyHelper;
import org.betterx.wover.surface.api.numeric.NumericProvider;
import org.betterx.wover.surface.api.numeric.NumericProviderRegistry;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceKey;

import org.jetbrains.annotations.ApiStatus;

public class NumericProviderRegistryImpl {
    public static void registerWithBCLib(
            ResourceKey<Codec<? extends NumericProvider>> key,
            Codec<? extends NumericProvider> codec
    ) {
        NumericProviderRegistry.register(key, codec);
        NumericProviderRegistry.register(WoverSurface.C.legacyBCLibId(key.location()), LegacyHelper.wrap(codec));
    }

    @ApiStatus.Internal
    public static void bootstrap() {
        registerWithBCLib(NumericProviderRegistry.RANDOM_INT, RandomIntProvider.CODEC);
        registerWithBCLib(NumericProviderRegistry.NETHER_NOISE, NetherNoiseCondition.CODEC);
    }
}
