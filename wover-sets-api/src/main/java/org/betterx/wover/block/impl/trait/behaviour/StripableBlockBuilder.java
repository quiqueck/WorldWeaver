package org.betterx.wover.block.impl.trait.behaviour;


import org.betterx.wover.block.api.trait.AbstractTraitBuilder;
import org.betterx.wover.block.api.trait.BlockTraitKey;
import org.betterx.wover.block.api.trait.RuntimeBlockTrait;
import org.betterx.wover.block.api.trait.behaviour.StripableBlockTrait;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;
import org.betterx.wover.entrypoint.LibWoverBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class StripableBlockBuilder extends AbstractTraitBuilder<Block, StripableBlockTrait> implements StripableBlockTrait.Builder {
    public static final StripableBlockTrait.Builder BUILDER = new StripableBlockBuilder();

    private StripableBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverBlock.C, "stripable"));
    }

    public StripableBlockTrait with(Block strippedBlock) {
        return new Trait(() -> strippedBlock.defaultBlockState());
    }

    public StripableBlockTrait with(BlockState strippedBlock) {
        return new Trait(() -> strippedBlock);
    }

    public StripableBlockTrait with(Supplier<BlockState> strippedBlock) {
        return new Trait(strippedBlock);
    }

    public BlockState getStrippedBlockState(BlockState blockState) {
        final Block block = blockState.getBlock();
        var traits = BUILDER.getRuntimeTraits(block);
        if (traits == null || traits.isEmpty()) {
            return blockState;
        }
        return traits.get(0).strippedBlock();
    }

    class Trait extends BlockTraitImpl<Block, StripableBlockTrait> implements StripableBlockTrait, RuntimeBlockTrait<Block, StripableBlockTrait> {
        private final Supplier<BlockState> strippedBlock;

        Trait(Supplier<BlockState> strippedBlock) {
            this.strippedBlock = strippedBlock;
        }

        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public StripableBlockTrait forRuntime() {
            return this;
        }

        @Override
        public BlockState strippedBlock() {
            return this.strippedBlock.get();
        }
    }
}
