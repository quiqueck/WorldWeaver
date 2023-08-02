package org.betterx.wover.feature.api.configured.builders;

import org.betterx.wover.feature.api.features.PlaceBlockFeature;
import org.betterx.wover.feature.api.features.config.PlaceFacingBlockConfig;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public interface FacingBlock extends FeatureConfigurator<PlaceFacingBlockConfig, PlaceBlockFeature<PlaceFacingBlockConfig>> {
    FacingBlock allHorizontal();
    FacingBlock allVertical();
    FacingBlock allDirections();
    FacingBlock add(Block block);
    FacingBlock add(BlockState state);
    FacingBlock add(Block block, int weight);
    FacingBlock add(BlockState state, int weight);
    FacingBlock addAllStates(Block block, int weight);
    FacingBlock addAllStatesFor(IntegerProperty prop, Block block, int weight);
}
