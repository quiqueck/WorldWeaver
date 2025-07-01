package org.betterx.wover.block.api.trait.behaviour;

import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitBuilder;
import org.betterx.wover.block.api.trait.RuntimeBlockTrait;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public interface StripableBlockTrait extends BlockTrait<Block, StripableBlockTrait>, RuntimeBlockTrait<Block, StripableBlockTrait> {
    interface Builder extends BlockTraitBuilder<Block, StripableBlockTrait> {
        StripableBlockTrait with(Block strippedBlock);
        StripableBlockTrait with(BlockState strippedBlock);
        StripableBlockTrait with(Supplier<BlockState> strippedBlock);

        BlockState getStrippedBlockState(BlockState blockState);
    }

    BlockState strippedBlock();
}
