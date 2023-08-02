package org.betterx.wover.feature.api.configured.builders;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.feature.NetherForestVegetationFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NetherForestVegetationConfig;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

public interface NetherForrestVegetation extends FeatureConfigurator<NetherForestVegetationConfig, NetherForestVegetationFeature> {
    NetherForrestVegetation spreadWidth(int v);
    NetherForrestVegetation spreadHeight(int v);
    NetherForrestVegetation addAllStates(Block block, int weight);
    NetherForrestVegetation addAllStatesFor(IntegerProperty prop, Block block, int weight);
    NetherForrestVegetation add(Block block, int weight);
    NetherForrestVegetation add(BlockState state, int weight);
    NetherForrestVegetation provider(WeightedStateProvider provider);
}
