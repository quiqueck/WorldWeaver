package de.ambertation.wover.feature.api.features;

import de.ambertation.wover.feature.api.FeatureUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * Marks a {@link Feature} as capable of "growing" itself directly (for example a sapling growing into a
 * tree), as opposed to only being placed as part of the regular worldgen pipeline.
 * <p>
 * When a {@link Feature} that implements this interface is placed through
 * {@link FeatureUtils#placeInWorld(net.minecraft.world.level.levelgen.feature.ConfiguredFeature, net.minecraft.world.level.WorldGenLevel, BlockPos, RandomSource, boolean)}
 * (or one of its overloads) with {@code unchanged} set to {@code false}, {@link #grow} is called instead of
 * the regular {@link Feature#place}.
 *
 * @param <FC> The type of the {@link FeatureConfiguration} used by this feature
 */
public interface GrowableFeature<FC extends FeatureConfiguration> {
    /**
     * Grows the feature at the given position.
     *
     * @param level         the level to grow the feature in
     * @param pos           the position to grow the feature at
     * @param random        the random source to use
     * @param configuration the configuration of the feature
     * @return {@code true} if the feature was grown, {@code false} otherwise
     */
    boolean grow(
            ServerLevelAccessor level,
            BlockPos pos,
            RandomSource random,
            FC configuration
    );
}
