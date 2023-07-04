package org.betterx.wover.surface.api.noise;

import org.betterx.wover.entrypoint.WoverMath;
import org.betterx.wover.surface.impl.noise.NoiseRegistryImpl;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoiseRegistry {
    public static final ResourceKey<Registry<NormalNoise.NoiseParameters>> NOISE_REGISTRY = Registries.NOISE;
    public static final ResourceKey<NormalNoise.NoiseParameters> ROUGHNESS_NOISE =
            createKey(WoverMath.C.id("roughness_noise"));

    public static ResourceKey<NormalNoise.NoiseParameters> createKey(ResourceLocation loc) {
        return NoiseRegistryImpl.createKey(loc);
    }

    public static NormalNoise getOrCreateNoise(
            RegistryAccess registryAccess,
            RandomSource randomSource,
            ResourceKey<NormalNoise.NoiseParameters> noise
    ) {
        return NoiseRegistryImpl.getOrCreateNoise(registryAccess, randomSource, noise);
    }
}
