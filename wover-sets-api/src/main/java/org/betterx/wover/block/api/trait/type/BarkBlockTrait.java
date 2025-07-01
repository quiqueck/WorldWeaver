package org.betterx.wover.block.api.trait.type;

import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.GenericBlockTrait;
import org.betterx.wover.block.api.trait.behaviour.StripableBlockTrait;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public interface BarkBlockTrait extends GenericBlockTrait {
    interface Builder extends GenericBlockTrait.BuilderWithDefault {
        @Nullable List<BlockTrait<?, ?>> with(@Nullable Block strippedBlockState);
        @Nullable List<BlockTrait<?, ?>> with(@Nullable BlockState strippedBlockState);
        @Nullable List<BlockTrait<?, ?>> with(@Nullable StripableBlockTrait.BlockStateFactory strippedBlockState);
    }
}
