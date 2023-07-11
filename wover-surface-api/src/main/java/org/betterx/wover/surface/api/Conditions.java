package org.betterx.wover.surface.api;

import org.betterx.wover.surface.api.conditions.NoiseCondition;
import org.betterx.wover.surface.api.conditions.VolumeThresholdCondition;
import org.betterx.wover.surface.impl.conditions.RoughNoiseConditionImpl;
import org.betterx.wover.surface.impl.conditions.ThresholdConditionImpl;
import org.betterx.wover.surface.impl.conditions.VolumeThresholdConditionImpl;

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
     * <p>
     * When used in a .json file, the condition would look like this:
     * <pre class="json"> {
     *   "type": "wover:threshold_condition",
     *   "scale_x": 0.5,
     *   "scale_z": 0.5,
     *   "seed": 523,
     *   "threshold": 0.05,
     *   "roughness": {
     *     "type": "minecraft:uniform",
     *     "value": {
     *       "max_exclusive": -0.3,
     *       "min_inclusive": -0.4
     *     }
     *   }
     * }</pre>
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
     * <pre>noise.eval(x * scaleX, y * scaleY, z * scaleZ) + roughness.sample(random) &gt; threshold</pre>
     * <p>
     * The noise is an instance of {@link org.betterx.wover.math.api.noise.OpenSimplexNoise} with
     * the seed {@code noiseSeed}.
     * <p>
     * When used in a .json file, the condition would look like this:
     *
     * <pre class="json"> {
     *   "type": "wover:volume_threshold_condition",
     *   "scale_x": 0.2,
     *   "scale_y": 0.2,
     *   "scale_z": 0.2,
     *   "seed": 523,
     *   "threshold": 0.03,
     *   "roughness": {
     *     "type": "minecraft:uniform",
     *     "value": {
     *       "max_exclusive": 0.4,
     *       "min_inclusive": -0.1
     *     }
     *   }
     * }</pre>
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
     * <pre>minThreshold &lt; noise.eval(x * scaleX, y * scaleY, z * scaleZ) + roughness.sample(random) &lt; maxThreshold</pre>
     * <p>
     * You can use this condition in any place where a rule source is expecting a condition.
     * In a .json file , the condition would look like this:
     *
     * <pre class="json"> {
     *   "type": "wover:rough_noise_condition",
     *   "min_threshold": 0.19,
     *   "max_threshold": 0.31,
     *   "noise": "minecraft:gravel_layer",
     *   "roughness": {
     *     "type": "minecraft:uniform",
     *     "value": {
     *       "min_inclusive": -0.2,
     *       "max_exclusive": 0.4
     *     }
     *   }
     * }</pre>
     * The {@code max_threshold} is optional. If not specified, it will default to {@link Double#MAX_VALUE}.
     *
     * @param noise        The noise to use for the condition.
     * @param roughness    The roughness of the noise. This is used to create more variation in the surface.
     * @param minThreshold The minimum threshold that the noise value has to be above to be true.
     * @param maxThreshold The maximum threshold that the noise value has to be below to be true.
     * @return A condition that is true if the noise value at the current position is between both thresholds.
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
     * Generates a 3D condition that is true if the noise value is above the min threshold.
     * <p>
     * This will create the same {@link net.minecraft.world.level.levelgen.SurfaceRules.ConditionSource}
     * as {@link #roughNoise(ResourceKey, FloatProvider, double, double)} with the max threshold
     * set to {@link Double#MAX_VALUE}.
     *
     * @param noise        The noise to use for the condition.
     * @param roughness    The roughness of the noise. This is used to create more variation in the surface.
     * @param minThreshold The minimum threshold that the noise value has to be above to be true.
     * @return A condition that is true if the noise value at the current position is above the threshold.
     */
    public static SurfaceRules.ConditionSource roughNoise(
            ResourceKey<NormalNoise.NoiseParameters> noise,
            FloatProvider roughness,
            double minThreshold
    ) {
        return new RoughNoiseConditionImpl(noise, roughness, minThreshold, Double.MAX_VALUE);
    }

    /**
     * Generates a 3D condition that is true if the noise value is above the min threshold.
     * <p>
     * This will create the same {@link net.minecraft.world.level.levelgen.SurfaceRules.ConditionSource}
     * as {@link #roughNoise(ResourceKey, FloatProvider, double, double)} with the max threshold
     * set to {@link Double#MAX_VALUE} and the roughness set to {@code UniformFloat.of(-0.2, 0.4)}.
     *
     * @param noise        The noise to use for the condition.
     * @param minThreshold The minimum threshold that the noise value has to be above to be true.
     * @return A condition that is true if the noise value at the current position is above the threshold.
     */
    public static SurfaceRules.ConditionSource roughNoise(
            ResourceKey<NormalNoise.NoiseParameters> noise,
            double minThreshold
    ) {
        return new RoughNoiseConditionImpl(noise, UniformFloat.of(-0.2f, 0.4f), minThreshold, Double.MAX_VALUE);
    }


    /**
     * Used by {@link SurfaceRuleBuilder#chancedFloor(BlockState, BlockState)} to
     * choose one of two different surface blocks.
     *
     * @see #threshold(long, double, FloatProvider, double, double)
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
     *
     * @see #threshold(long, double, FloatProvider, double, double)
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
     *
     * @see #threshold(long, double, FloatProvider, double, double)
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
     *
     * @see #threshold(long, double, FloatProvider, double, double)
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
     *
     * @see #threshold(long, double, FloatProvider, double, double)
     */
    public static final NoiseCondition NETHER_SURFACE_NOISE_LARGE = threshold(
            523,
            0,
            UniformFloat.of(-0.4f, -0.3f),
            0.5,
            0.5
    );

    /**
     * Used to generate the 3D distribution in the nether.
     *
     * @see #volumeThreshold(long, double, FloatProvider, double, double, double)
     */
    public static final VolumeThresholdCondition NETHER_VOLUME_NOISE = volumeThreshold(
            245,
            0,
            UniformFloat.of(-0.1f, 0.2f),
            0.1,
            0.2,
            0.1
    );

    /**
     * Used to generate a larger 3D distribution in the nether.
     *
     * @see #volumeThreshold(long, double, FloatProvider, double, double, double)
     */
    public static final VolumeThresholdCondition NETHER_VOLUME_NOISE_LARGE = volumeThreshold(
            523,
            0,
            UniformFloat.of(-0.1f, 0.4f),
            0.2,
            0.2,
            0.2
    );

    private Conditions() {

    }
}
