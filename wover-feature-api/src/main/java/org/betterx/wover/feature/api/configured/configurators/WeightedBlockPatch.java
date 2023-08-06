package org.betterx.wover.feature.api.configured.configurators;

import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.RandomPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;

/**
 * Places random Blocks in a Patch ({@link RandomPatchFeature}).
 * <p>
 * Similar to {@link RandomPatch}, but instead of defining a {@link net.minecraft.world.level.levelgen.placement.PlacedFeature}
 * you can define a set of Blocks that are arrange in the patch.
 */
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
