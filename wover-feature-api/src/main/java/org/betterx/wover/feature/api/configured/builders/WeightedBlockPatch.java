package org.betterx.wover.feature.api.configured.builders;

import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.RandomPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;

public interface WeightedBlockPatch extends WeightedBaseBlock<RandomPatchConfiguration, RandomPatchFeature, WeightedBlockPatch> {
    WeightedBlockPatch isEmpty();
    WeightedBlockPatch isEmpty(boolean value);
    WeightedBlockPatch isOn(BlockPredicate predicate);
    WeightedBlockPatch isEmptyAndOn(BlockPredicate predicate);
    WeightedBlockPatch likeDefaultNetherVegetation();
    WeightedBlockPatch likeDefaultNetherVegetation(int xzSpread, int ySpread);
    WeightedBlockPatch likeDefaultBonemeal();
    WeightedBlockPatch tries(int v);
    WeightedBlockPatch spreadXZ(int v);
    WeightedBlockPatch spreadY(int v);
}
