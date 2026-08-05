package de.ambertation.wover.feature.api.configured.configurators;

import de.ambertation.wover.feature.impl.random.RandomPatchConfiguration;
import de.ambertation.wover.feature.impl.random.RandomPatchFeature;

import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

/**
 * Places random Blocks in a Patch ({@link RandomPatchFeature}).
 * <p>
 * Similar to {@link RandomPatch}, but instead of defining a {@link net.minecraft.world.level.levelgen.placement.PlacedFeature}
 * you can define a set of Blocks that are arrange in the patch.
 * <p>
 * deprecated: Backed by the {@code random_patch} configured-feature type, which vanilla removed in
 * Minecraft 26.1. WorldWeaver keeps a compatible {@code wover:random_patch} shim so existing data keeps
 * working, and this API remains functional for this release, but it is scheduled for removal. Express the
 * "scatter blocks N times in an area" behaviour with <b>placement modifiers</b> via
 * {@link de.ambertation.wover.feature.api.placed.FeaturePlacementBuilder#scatter(int, int, int, net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate)
 * FeaturePlacementBuilder.scatter(tries, xzSpread, ySpread, filter)} on a {@code simple_block} placed feature
 * ({@code CountPlacement.of(tries)} + {@code RandomOffsetPlacement.of(xz, y)} + an optional
 * {@code BlockPredicateFilter} + {@code BiomeFilter}), instead of a configured {@code random_patch}.
 */
//TODO: @Deprecated(since = "26.1.0", forRemoval = true)
public interface WeightedBlockPatch extends BaseWeightedBlock<RandomPatchConfiguration, RandomPatchFeature, WeightedBlockPatch>, BasePatch<RandomPatchConfiguration, RandomPatchFeature, WeightedBlockPatch> {
    /**
     * Ensures that the position where the Block is placed is empty.
     *
     * @return the same instance
     */
    WeightedBlockPatch isEmpty();

    /**
     * Disables/Enables the empty test
     *
     * @param value {@code true} if the position should be empty, {@code false} if it no test should be performed
     * @return the same instance
     */
    WeightedBlockPatch isEmpty(boolean value);

    /**
     * Ensures that the position where the Block is placed is  on a Block that matches the predicate.
     *
     * @param predicate The predicate to match
     * @return the same instance
     */
    WeightedBlockPatch isOn(BlockPredicate predicate);

    /**
     * Ensures that the position where the Block is placed is empty and on a Block that matches the predicate.
     *
     * @param predicate The predicate to match
     * @return the same instance
     */
    WeightedBlockPatch isEmptyAndOn(BlockPredicate predicate);
}
