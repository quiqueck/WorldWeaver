package org.betterx.wover.surface.api.numeric;

import org.betterx.wover.entrypoint.WoverSurface;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class NumericProviderRegistry {
    public static final ResourceKey<Registry<Codec<? extends NumericProvider>>> NUMERIC_PROVIDER_REGISTRY = ResourceKey.createRegistryKey(
            WoverSurface.C.id("wover/numeric_provider"));
    public static final Registry<Codec<? extends NumericProvider>> NUMERIC_PROVIDER = new MappedRegistry<>(
            NUMERIC_PROVIDER_REGISTRY,
            Lifecycle.experimental()
    );

    public static final ResourceKey<Codec<? extends NumericProvider>> RANDOM_INT
            = createKey(WoverSurface.C.id("rnd_int"));

    public static final ResourceKey<Codec<? extends NumericProvider>> NETHER_NOISE
            = createKey(WoverSurface.C.id("nether_noise"));

    public static ResourceKey<Codec<? extends NumericProvider>> createKey(ResourceLocation location) {
        return ResourceKey.create(NUMERIC_PROVIDER_REGISTRY, location);
    }

    public static ResourceKey<Codec<? extends NumericProvider>> register(
            ResourceKey<Codec<? extends NumericProvider>> key,
            Codec<? extends NumericProvider> codec
    ) {
        Registry.register(NUMERIC_PROVIDER, key, codec);
        return key;
    }

    public static ResourceKey<Codec<? extends NumericProvider>> register(
            ResourceLocation location,
            Codec<? extends NumericProvider> codec
    ) {
        return register(createKey(location), codec);
    }
}
