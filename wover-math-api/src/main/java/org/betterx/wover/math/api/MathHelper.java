package org.betterx.wover.math.api;

public class MathHelper {
    public static int floor(double x) {
        return x < 0 ? (int) (x - 1) : (int) x;
    }

    public static float lengthSqr(float x, float y, float z) {
        return x * x + y * y + z * z;
    }

    public static long getSeed(int value) {
        // Perform bitwise XOR operations on the integer value
        int hash = value;
        hash ^= (hash >>> 20) ^ (hash >>> 12);
        hash ^= (hash >>> 7) ^ (hash >>> 4);

        // Convert the hash to a long value
        long randomSeed = (long) hash & 0xFFFFFFFFL;

        return randomSeed;
    }

    public static int getSeed(int seed, int x, int y) {
        int h = seed + x * 374761393 + y * 668265263;
        h = (h ^ (h >> 13)) * 1274126177;
        return h ^ (h >> 16);
    }

    public static int getSeed(int seed, int x, int y, int z) {
        int h = seed + x * 374761393 + y * 668265263 + z;
        h = (h ^ (h >> 13)) * 1274126177;
        return h ^ (h >> 16);
    }
}
