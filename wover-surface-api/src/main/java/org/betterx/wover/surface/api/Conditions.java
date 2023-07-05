package org.betterx.wover.surface.api;

import org.betterx.wover.surface.api.conditions.NoiseCondition;
import org.betterx.wover.surface.api.conditions.VolumeThresholdCondition;
import org.betterx.wover.surface.api.numeric.NumericProvider;
import org.betterx.wover.surface.impl.conditions.RoughNoiseConditionImpl;
import org.betterx.wover.surface.impl.conditions.ThresholdConditionImpl;
import org.betterx.wover.surface.impl.conditions.VolumeThresholdConditionImpl;
import org.betterx.wover.surface.impl.numeric.NetherNoiseCondition;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

/**
 * A collection of preconfigured and wover specific conditions that can be used to generate surfaces.
 */
public class Conditions {
    /**
     * Generates a 2D condition that is true if the noise value at the current position
     * is above the threshold.
     * <p>
     * The value for a location x/z is calculated as follows:
     * <pre>noise.eval(x * scaleX, z * scaleZ) + roughness.sample(random) &gt; threshold</pre>
     * <p>
     * The noise is an instance of {@link org.betterx.wover.math.api.noise.OpenSimplexNoise} with
     * the seed {@code noiseSeed}.
     *
     * @param noiseSeed The seed for the noise generator. The condition will internally use the
     *                  {@link org.betterx.wover.math.api.noise.OpenSimplexNoise}
     * @param threshold The threshold that the noise value has to be above to be true.
     * @param roughness additional noise on top of the regular noise floor. This is used to create
     *                  more variation in the surface.
     * @param scaleX    The scale of the noise in the x direction.
     * @param scaleZ    The scale of the noise in the z direction.
     * @return A condition that is true if the noise value at the current position is above the threshold.
     */
    public static NoiseCondition threshold(
            long noiseSeed,
            double threshold,
            FloatProvider roughness,
            double scaleX,
            double scaleZ
    ) {
        return new ThresholdConditionImpl(noiseSeed, threshold, roughness, scaleX, scaleZ);
    }


    /**
     * Generates a 3D condition that is true if the noise value at the current position
     * is above the threshold.
     * <p>
     * The value for a location x/y/z is calculated as follows:
     * <pre>noise.eval(x * scaleX, y*scaleY, z * scaleZ) + roughness.sample(random) &gt; threshold</pre>
     * <p>
     * The noise is an instance of {@link org.betterx.wover.math.api.noise.OpenSimplexNoise} with
     * the seed {@code noiseSeed}.
     *
     * @param noiseSeed The seed for the noise generator. The condition will internally use the
     *                  {@link org.betterx.wover.math.api.noise.OpenSimplexNoise}
     * @param threshold The threshold that the noise value has to be above to be true.
     * @param roughness additional noise on top of the regular noise floor. This is used to create
     * @param scaleX    The scale of the noise in the x direction.
     * @param scaleY    The scale of the noise in the y direction.
     * @param scaleZ    The scale of the noise in the z direction.
     * @return A condition that is true if the noise value at the current position is above the threshold.
     */
    public static VolumeThresholdCondition volumeThreshold(
            long noiseSeed,
            double threshold,
            FloatProvider roughness,
            double scaleX,
            double scaleY,
            double scaleZ
    ) {
        return new VolumeThresholdConditionImpl(noiseSeed, threshold, roughness, scaleX, scaleY, scaleZ);
    }


    /**
     * Generates a 3D condition that is true if the noise value is between the min and max threshold.
     * <p>
     * The value for a location x/y/z is calculated as follows:
     * <pre>minThreshold &lt; noise.eval(x * scaleX, y*scaleY, z * scaleZ) + roughness.sample(random) &lt; maxThreshold</pre>
     *
     * @param noise        The noise to use for the condition.
     * @param roughness    The roughness of the noise. This is used to create more variation in the surface.
     * @param minThreshold The minimum threshold that the noise value has to be above to be true.
     * @param maxThreshold The maximum threshold that the noise value has to be below to be true.
     * @return A condition that is true if the noise value at the current position is above the threshold.
     */
    public static SurfaceRules.ConditionSource roughNoise(
            ResourceKey<NormalNoise.NoiseParameters> noise,
            FloatProvider roughness,
            double minThreshold,
            double maxThreshold
    ) {
        return new RoughNoiseConditionImpl(noise, roughness, minThreshold, maxThreshold);
    }


    /**
     * Used by {@link SurfaceRuleBuilder#chancedFloor(BlockState, BlockState)} to
     * choose one of two different surface blocks.
     */
    public static final NoiseCondition DOUBLE_BLOCK_SURFACE_NOISE = threshold(
            4141,
            0,
            UniformFloat.of(-0.4f, 0.4f),
            0.1,
            0.1
    );

    /**
     * Used to generate the surface of a forrest floor.
     */
    public static final NoiseCondition FORREST_FLOOR_SURFACE_NOISE_A = threshold(
            614,
            0,
            UniformFloat.of(-0.2f, 0f),
            0.1,
            0.1
    );

    /**
     * Used to generate the surface of a forrest floor.
     */
    public static final NoiseCondition FORREST_FLOOR_SURFACE_NOISE_B = threshold(
            614,
            0,
            UniformFloat.of(-0.7f, -0.5f),
            0.1,
            0.1
    );

    /**
     * Used to generate the surface of a nether biome.
     */
    public static final NoiseCondition NETHER_SURFACE_NOISE = threshold(
            245,
            0,
            UniformFloat.of(-0.7f, -0.5f),
            0.05,
            0.05
    );

    /**
     * Used to generate the surface of a nether biome with larger scale variations.
     */
    public static final NoiseCondition NETHER_SURFACE_NOISE_LARGE = threshold(
            523,
            0,
            UniformFloat.of(-0.4f, -0.3f),
            0.5,
            0.5
    );

    /**
     * Used to generate the distribution in the nether.
     */
    public static final VolumeThresholdCondition NETHER_VOLUME_NOISE = volumeThreshold(
            245,
            0,
            UniformFloat.of(-0.1f, 0.2f),
            0.1,
            0.2,
            0.1
    );

    public static final VolumeThresholdCondition NETHER_VOLUME_NOISE_LARGE = volumeThreshold(
            523,
            0,
            UniformFloat.of(-0.1f, 0.4f),
            0.2,
            0.2,
            0.2
    );

    /**
     * A simple scalar random number provider
     */
    public static final NumericProvider NETHER_NOISE = new NetherNoiseCondition();


}
