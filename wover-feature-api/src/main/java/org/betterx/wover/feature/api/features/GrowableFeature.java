package org.betterx.wover.feature.api.features;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public interface GrowableFeature<FC extends FeatureConfiguration> {
    boolean grow(
            ServerLevelAccessor level,
            BlockPos pos,
            RandomSource random,
            FC configuration
    );
}
