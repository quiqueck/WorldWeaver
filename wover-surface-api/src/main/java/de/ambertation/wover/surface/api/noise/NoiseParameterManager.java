package de.ambertation.wover.surface.api.noise;

import de.ambertation.wover.entrypoint.LibWoverMath;
import de.ambertation.wover.surface.impl.noise.NoiseRegistryImpl;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
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
    public static ResourceKey<NormalNoise.NoiseParameters> createKey(Identifier loc) {
        return NoiseRegistryImpl.createKey(loc);
    }

}
