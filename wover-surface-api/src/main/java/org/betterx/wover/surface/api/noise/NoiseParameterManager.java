package org.betterx.wover.surface.api.noise;

import org.betterx.wover.entrypoint.LibWoverMath;
import org.betterx.wover.surface.impl.noise.NoiseRegistryImpl;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

/**
 * Helper to create entries in the Noise Parameter Registry {@link Registries#NOISE}.
 */
public class NoiseParameterManager {
    private NoiseParameterManager() {
    }

    /**
     * Key for a WorldWeaver Noise function with custom Parameters
     */
    public static final ResourceKey<NormalNoise.NoiseParameters> ROUGHNESS_NOISE =
            createKey(LibWoverMath.C.id("roughness_noise"));

    /**
     * Key for a Noise function with custom Parameters
     */
    public static ResourceKey<NormalNoise.NoiseParameters> createKey(ResourceLocation loc) {
        return NoiseRegistryImpl.createKey(loc);
    }

    /**
     * Creates a Noise instance with custom Parameters
     *
     * @param registryAccess The Registry Access
     * @param randomSource   The Random Source
     * @param noise          The Key for the Noise Parameters
     * @return The Noise function
     */
    public static NormalNoise getOrCreateNoise(
            RegistryAccess registryAccess,
            RandomSource randomSource,
            ResourceKey<NormalNoise.NoiseParameters> noise
    ) {
        return NoiseRegistryImpl.getOrCreateNoise(registryAccess, randomSource, noise);
    }
}
