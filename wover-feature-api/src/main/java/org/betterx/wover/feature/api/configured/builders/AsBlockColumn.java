package org.betterx.wover.feature.api.configured.builders;

import net.minecraft.core.Direction;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.BlockColumnFeature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockColumnConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public interface AsBlockColumn extends FeatureConfigurator<BlockColumnConfiguration, BlockColumnFeature> {
    AsBlockColumn add(int height, Block block);
    AsBlockColumn add(int height, BlockState state);
    AsBlockColumn add(int height, BlockStateProvider state);
    AsBlockColumn addRandom(int height, BlockState... states);
    AsBlockColumn addRandom(IntProvider height, BlockState... states);
    AsBlockColumn add(IntProvider height, Block block);
    AsBlockColumn add(IntProvider height, BlockState state);
    AsBlockColumn add(IntProvider height, BlockStateProvider state);
    AsBlockColumn addTripleShape(BlockState state, IntProvider midHeight);
    AsBlockColumn addTripleShapeUpsideDown(BlockState state, IntProvider midHeight);
    AsBlockColumn addBottomShapeUpsideDown(BlockState state, IntProvider midHeight);
    AsBlockColumn addBottomShape(BlockState state, IntProvider midHeight);
    AsBlockColumn addTopShapeUpsideDown(BlockState state, IntProvider midHeight);
    AsBlockColumn addTopShape(BlockState state, IntProvider midHeight);
    AsBlockColumn direction(Direction dir);
    AsBlockColumn prioritizeTip();
    AsBlockColumn prioritizeTip(boolean v);
    AsBlockColumn allowedPlacement(BlockPredicate v);
}
