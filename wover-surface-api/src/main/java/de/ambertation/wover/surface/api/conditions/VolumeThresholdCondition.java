package de.ambertation.wover.surface.api.conditions;

import de.ambertation.wover.math.api.MathHelper;
import de.ambertation.wover.math.api.noise.OpenSimplexNoise;

import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;

/**
 * A Getter Interface for {@link NoiseCondition}s that evaluate a custom noise function for a
 * <b>3D Location</b> and compares it with a threshold.
 *
 * @see de.ambertation.wover.surface.api.Conditions#volumeThreshold(long, double, FloatProvider, double, double, double)
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
         * A single, shared {@link RandomSource}.
         *
         * @return the {@link RandomSource}
         * @deprecated Drawing from this source makes the result depend on how many draws happened
         * before it, and thus on the order in which chunks reached the surface builder. Since chunks
         * are built on several worker threads that order is not reproducible, so a world generated
         * from it cannot be generated again. Use {@link #randomAt(int, int, int)} instead, which
         * yields the same distribution but as a pure function of the position.
         */
        @Deprecated(forRemoval = true)
        RandomSource getRandom();

        /**
         * A {@link RandomSource} seeded from this context's seed and a block position.
         * <p>
         * Two calls for the same position always return an identically seeded source, and positions
         * are independent of each other, so anything derived from it is reproducible no matter in
         * which order (or on how many threads) the world is generated.
         *
         * @param x the block x coordinate
         * @param y the block y coordinate
         * @param z the block z coordinate
         * @return a {@link RandomSource} that only depends on the seed and the position
         */
        default RandomSource randomAt(int x, int y, int z) {
            return RandomSource.create(MathHelper.getSeed(Long.hashCode(getSeed()), x, y, z));
        }

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
     * Additional noise on top of the regular noise floor. This is used to create
     * more variation in the surface.
     *
     * @return the additional noise on top of the regular noise floor
     */
    FloatProvider getRoughness();
}
