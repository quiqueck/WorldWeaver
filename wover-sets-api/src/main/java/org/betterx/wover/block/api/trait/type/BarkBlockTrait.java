package org.betterx.wover.block.api.trait.type;

import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.GenericBlockTrait;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public interface BarkBlockTrait extends GenericBlockTrait {
    interface Builder extends GenericBlockTrait.BuilderWithDefault {
        @Nullable List<BlockTrait<?, ?>> with(Block strippedBlockState);
        @Nullable List<BlockTrait<?, ?>> with(BlockState strippedBlockState);
        @Nullable List<BlockTrait<?, ?>> with(Supplier<BlockState> strippedBlockState);
    }
}
