package org.betterx.wover.math.api;

/**
 * A small collection of stateless math helpers used across WorldWeaver's world generation code.
 * <p>
 * It provides a floor operation that behaves correctly for negative numbers (unlike a plain
 * {@code (int)} cast), a cheap squared-length calculation for 3D vectors, and a family of
 * deterministic integer hashing/seed-derivation functions. The hash functions are used to turn a
 * world seed plus a block/chunk position into a well-distributed pseudo-random seed, so that
 * noise and randomness stay reproducible for the same world seed and coordinates.
 */
public class MathHelper {
    /**
     * Floors a double value to an {@code int}, rounding towards negative infinity.
     * <p>
     * Unlike a plain {@code (int) x} cast, which truncates towards zero, this method returns the
     * correct result for negative inputs (e.g. {@code floor(-0.5)} returns {@code -1}, not
     * {@code 0}).
     *
     * @param x the value to floor
     * @return the largest {@code int} that is less than or equal to {@code x}
     */
    public static int floor(double x) {
        return x < 0 ? (int) (x - 1) : (int) x;
    }

    /**
     * Calculates the squared length (magnitude) of a 3D vector.
     * <p>
     * This avoids the relatively expensive {@link Math#sqrt(double)} call and is useful whenever
     * only relative distances need to be compared (e.g. finding the nearest of several points).
     *
     * @param x the x component of the vector
     * @param y the y component of the vector
     * @param z the z component of the vector
     * @return {@code x*x + y*y + z*z}, i.e. the squared length of the vector
     */
    public static float lengthSqr(float x, float y, float z) {
        return x * x + y * y + z * z;
    }

    /**
     * Derives a pseudo-random seed from a single {@code int} value by mixing its bits (a simple
     * avalanche/xorshift-style hash).
     *
     * @param value the value to derive a seed from
     * @return a 32-bit hash of {@code value}, zero-extended to a {@code long}
     */
    public static long getSeed(int value) {
        // Perform bitwise XOR operations on the integer value
        int hash = value;
        hash ^= (hash >>> 20) ^ (hash >>> 12);
        hash ^= (hash >>> 7) ^ (hash >>> 4);

        // Convert the hash to a long value
        long randomSeed = (long) hash & 0xFFFFFFFFL;

        return randomSeed;
    }

    /**
     * Derives a pseudo-random, well-distributed seed from a base seed and a 2D position.
     * <p>
     * Useful for deriving a deterministic per-column (x/z) or per-chunk seed from the world seed,
     * so the same coordinates always produce the same seed.
     *
     * @param seed the base seed (e.g. the world seed)
     * @param x    the x coordinate to mix into the seed
     * @param y    the y (or z) coordinate to mix into the seed
     * @return a hashed, well-distributed seed derived from {@code seed}, {@code x} and {@code y}
     */
    public static int getSeed(int seed, int x, int y) {
        int h = seed + x * 374761393 + y * 668265263;
        h = (h ^ (h >> 13)) * 1274126177;
        return h ^ (h >> 16);
    }

    /**
     * Derives a pseudo-random, well-distributed seed from a base seed and a 3D position.
     *
     * @param seed the base seed (e.g. the world seed)
     * @param x    the x coordinate to mix into the seed
     * @param y    the y coordinate to mix into the seed
     * @param z    the z coordinate to mix into the seed
     * @return a hashed, well-distributed seed derived from {@code seed}, {@code x}, {@code y} and
     * {@code z}
     */
    public static int getSeed(int seed, int x, int y, int z) {
        int h = seed + x * 374761393 + y * 668265263 + z;
        h = (h ^ (h >> 13)) * 1274126177;
        return h ^ (h >> 16);
    }
}
