package org.betterx.wover.block.api.trait.behaviour;

import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitBuilder;
import org.betterx.wover.block.api.trait.RuntimeBlockTrait;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface StripableBlockTrait extends BlockTrait<Block, StripableBlockTrait>, RuntimeBlockTrait<Block, StripableBlockTrait> {
    interface BlockStateFactory {
        @NotNull BlockState create(@Nullable BlockState oldState);
    }

    interface Builder extends BlockTraitBuilder<Block, StripableBlockTrait> {
        @Nullable StripableBlockTrait with(@Nullable Block strippedBlock);
        @Nullable StripableBlockTrait with(@Nullable BlockState strippedBlock);
        @Nullable StripableBlockTrait with(@Nullable BlockStateFactory strippedBlock);

        @NotNull BlockState getStrippedBlockState(@Nullable BlockState blockState);
    }

    @NotNull BlockState strippedBlock(@Nullable BlockState oldState);

    static @NotNull BlockStateFactory copyRotatedPillarBlockState(@NotNull BlockStateFactory factory) {
        return (oldState) -> {
            var newState = factory.create(oldState);
            if (oldState != null && oldState.hasProperty(RotatedPillarBlock.AXIS)) {
                return newState.setValue(
                        RotatedPillarBlock.AXIS,
                        oldState.getValue(RotatedPillarBlock.AXIS)
                );
            }
            return newState;
        };
    }
}
