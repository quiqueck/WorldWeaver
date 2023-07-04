package org.betterx.wover.math.api.random;

import net.minecraft.util.RandomSource;

public class RandomHelper {
    public static float inRange(RandomSource random, float min, float max) {
        return min + random.nextFloat() * (max - min);
    }
}
