package org.betterx.wover.surface.api;

import org.betterx.wover.surface.api.conditions.ThresholdCondition;
import org.betterx.wover.surface.api.conditions.VolumeThresholdCondition;
import org.betterx.wover.surface.api.numeric.NumericProvider;
import org.betterx.wover.surface.impl.numeric.NetherNoiseCondition;

import net.minecraft.util.valueproviders.UniformFloat;

public class Conditions {
    public static final ThresholdCondition DOUBLE_BLOCK_SURFACE_NOISE = new ThresholdCondition(
            4141,
            0,
            UniformFloat.of(-0.4f, 0.4f),
            0.1,
            0.1
    );

    public static final ThresholdCondition FORREST_FLOOR_SURFACE_NOISE_A = new ThresholdCondition(
            614,
            0,
            UniformFloat.of(-0.2f, 0f),
            0.1,
            0.1
    );

    public static final ThresholdCondition FORREST_FLOOR_SURFACE_NOISE_B = new ThresholdCondition(
            614,
            0,
            UniformFloat.of(-0.7f, -0.5f),
            0.1,
            0.1
    );

    public static final ThresholdCondition NETHER_SURFACE_NOISE = new ThresholdCondition(
            245,
            0,
            UniformFloat.of(-0.7f, -0.5f),
            0.05,
            0.05
    );

    public static final ThresholdCondition NETHER_SURFACE_NOISE_LARGE = new ThresholdCondition(
            523,
            0,
            UniformFloat.of(-0.4f, -0.3f),
            0.5,
            0.5
    );

    public static final VolumeThresholdCondition NETHER_VOLUME_NOISE = new VolumeThresholdCondition(
            245,
            0,
            UniformFloat.of(-0.1f, 0.2f),
            0.1,
            0.2,
            0.1
    );

    public static final VolumeThresholdCondition NETHER_VOLUME_NOISE_LARGE = new VolumeThresholdCondition(
            523,
            0,
            UniformFloat.of(-0.1f, 0.4f),
            0.2,
            0.2,
            0.2
    );

    public static final NumericProvider NETHER_NOISE = new NetherNoiseCondition();


}
