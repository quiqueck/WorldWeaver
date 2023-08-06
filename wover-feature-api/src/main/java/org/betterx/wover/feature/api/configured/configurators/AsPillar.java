package org.betterx.wover.feature.api.configured.configurators;

import org.betterx.wover.feature.api.features.PillarFeature;
import org.betterx.wover.feature.api.features.config.PillarFeatureConfig;

import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import org.jetbrains.annotations.NotNull;

/**
 * Places a pillar of blocks ({@link PillarFeature}).
 * <p>
 * A pillar is a column of blocks that can be configured to have a
 * minimum and maximum height and a growing direction. The pillar is
 * built from a predefined collection of {@link BlockState}s.
 */
public interface AsPillar extends FeatureConfigurator<PillarFeatureConfig, PillarFeature> {
    AsPillar transformer(@NotNull PillarFeatureConfig.KnownTransformers transformer);
    /**
     * Determines for each position that was selected to be part of the pillar weather or not
     * it should be placed.
     *
     * @param predicate The predicate that determines if a block should be placed
     * @return the same instance
     */
    AsPillar allowedPlacement(BlockPredicate predicate);
    /**
     * Defines the direction in which the pillar should grow.
     *
     * @param direction The direction
     * @return the same instance
     */
    AsPillar direction(Direction direction);
    /**
     * The {@link Block} to use for the pillar. The default {@link BlockState} will be used.
     *
     * @param block The block
     * @return the same instance
     */
    AsPillar blockState(Block block);

    /**
     * The {@link BlockState} to use for the pillar.
     *
     * @param state The block state
     * @return the same instance
     */
    AsPillar blockState(BlockState state);

    /**
     * The {@link BlockStateProvider} to use for the pillar.
     *
     * @param provider The block state provider
     * @return the same instance
     */
    AsPillar blockState(BlockStateProvider provider);

    /**
     * The height for the pillar to grow to.
     *
     * @param max The maximum height
     * @return the same instance
     */
    AsPillar maxHeight(int max);

    /**
     * The provider for the maximum height for the pillar to grow to. This is useful to have randomized heights
     *
     * @param max An integer provider that determines the maximum height
     * @return the same instance
     */
    AsPillar maxHeight(IntProvider max);
    /**
     * The minimum height for the pillar to grow to.
     *
     * @param min The minimum height
     * @return the same instance
     */
    AsPillar minHeight(int min);
    /**
     * The minimum height provider for the pillar to grow to. This is useful to have randomized heights
     *
     * @param min An integer provider that determines the minimum height
     * @return the same instance
     */
    AsPillar minHeight(IntProvider min);
}
