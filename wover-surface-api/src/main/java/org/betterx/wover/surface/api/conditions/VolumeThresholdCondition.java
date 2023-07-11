package org.betterx.wover.surface.api.conditions;

import org.betterx.wover.math.api.noise.OpenSimplexNoise;

import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;

/**
 * A Getter Interface for {@link NoiseCondition}s that evaluate a custom noise function for a
 * <b>3D Location</b> and compares it with a threshold.
 *
 * @see org.betterx.wover.surface.api.Conditions#volumeThreshold(long, double, FloatProvider, double, double, double)
 */
public interface VolumeThresholdCondition extends NoiseCondition {
    /**
     * The evaluation Context object.
     * This Object will contain the instance of the Noise function, along with
     * the seed and the {@link RandomSource}.
     */
    interface Context {
        /**
         * The location based noise function.
         *
         * @return the noise function
         */
        OpenSimplexNoise getNoise();

        /**
         * The {@link RandomSource} used to evaluate the additional roughness function.
         *
         * @return the {@link RandomSource}
         */
        RandomSource getRandom();

        /**
         * The seed used to create the noise function.
         *
         * @return the seed
         */
        long getSeed();
    }

    /**
     * The current evaluation context.
     *
     * @return the evaluation context
     */
    Context getNoiseContext();
    /**
     * The scale of the noise in the x direction
     *
     * @return the scale of the noise in the x direction
     */
    double getScaleX();
    /**
     * The scale of the noise in the y direction
     *
     * @return the scale of the noise in the y direction
     */
    double getScaleY();
    /**
     * The scale of the noise in the z direction
     *
     * @return the scale of the noise in the z direction
     */
    double getScaleZ();
    /**
     * additional noise on top of the regular noise floor. This is used to create
     *
     * @return the additional noise on top of the regular noise floor
     */
    FloatProvider getRoughness();
}
