package org.betterx.wover.feature.api.configured.builders;

import org.betterx.wover.feature.api.features.PillarFeature;
import org.betterx.wover.feature.api.features.config.PillarFeatureConfig;

import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import org.jetbrains.annotations.NotNull;

public interface AsPillar extends FeatureConfigurator<PillarFeatureConfig, PillarFeature> {
    AsPillar transformer(@NotNull PillarFeatureConfig.KnownTransformers transformer);
    AsPillar allowedPlacement(BlockPredicate predicate);
    AsPillar direction(Direction v);
    AsPillar blockState(Block v);
    AsPillar blockState(BlockState v);
    AsPillar blockState(BlockStateProvider v);
    AsPillar maxHeight(int v);
    AsPillar maxHeight(IntProvider v);
    AsPillar minHeight(int v);
    AsPillar minHeight(IntProvider v);
}
