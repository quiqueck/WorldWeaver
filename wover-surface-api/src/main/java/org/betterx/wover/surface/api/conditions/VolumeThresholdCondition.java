package org.betterx.wover.surface.api.conditions;

import org.betterx.wover.math.api.noise.OpenSimplexNoise;

import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;

public interface VolumeThresholdCondition extends NoiseCondition {
    interface Context {
        OpenSimplexNoise getNoise();
        RandomSource getRandom();
        long getSeed();
    }

    Context getNoiseContext();
    double getScaleX();
    double getScaleY();
    double getScaleZ();
    FloatProvider getRange();
}
