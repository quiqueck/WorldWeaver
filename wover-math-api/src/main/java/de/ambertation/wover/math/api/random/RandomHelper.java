package de.ambertation.wover.math.api.random;

import net.minecraft.util.RandomSource;

/**
 * Small helper for drawing random values from a bounded range.
 * <p>
 * Complements vanilla's {@link RandomSource}, which only provides unbounded
 * {@code nextFloat()}/{@code nextInt()} style methods.
 */
public class RandomHelper {
    /**
     * Draws a random {@code float} uniformly distributed in the range {@code [min, max)}.
     *
     * @param random the random source to draw from
     * @param min    the inclusive lower bound of the range
     * @param max    the exclusive upper bound of the range
     * @return a random value {@code >= min} and {@code < max}
     */
    public static float inRange(RandomSource random, float min, float max) {
        return min + random.nextFloat() * (max - min);
    }
}
