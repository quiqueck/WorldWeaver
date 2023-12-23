package org.betterx.wover.feature.api;

import org.betterx.wover.feature.impl.configured.ConfiguredFeatureManagerImpl;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class FeatureUtils {
    public static boolean placeInWorld(
            ConfiguredFeature<?, ?> feature,
            ServerLevel level,
            BlockPos pos,
            RandomSource random,
            boolean unchanged
    ) {
        return ConfiguredFeatureManagerImpl.placeInWorld(feature, level, pos, random, unchanged);
    }

    private FeatureUtils() {
    }
}
