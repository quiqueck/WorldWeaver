package org.betterx.wover.feature.api.configured.builders;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public interface WeightedBaseBlock<FC extends FeatureConfiguration, F extends Feature<FC>, W extends WeightedBaseBlock<FC, F, W>> extends FeatureConfigurator<FC, F> {
    W add(Block block, int weight);
    W add(BlockState state, int weight);
    W addAllStates(Block block, int weight);
    W addAllStatesFor(IntegerProperty prop, Block block, int weight);
}
