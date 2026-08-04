package de.ambertation.wover.block.impl.trait.behaviour;


import de.ambertation.wover.block.api.trait.AbstractBlockTraitBuilder;
import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.block.api.trait.RuntimeBlockTrait;
import de.ambertation.wover.block.api.trait.behaviour.StripableBlockTrait;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.entrypoint.LibWoverSets;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StripableBlockBuilder extends AbstractBlockTraitBuilder<Block, StripableBlockTrait> implements StripableBlockTrait.Builder {
    public static final StripableBlockTrait.Builder BUILDER = new StripableBlockBuilder();

    private StripableBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "stripable"));
    }

    public @Nullable StripableBlockTrait with(@Nullable Block strippedBlock) {
        if (strippedBlock == null) return null;

        return new Trait((oldState) -> strippedBlock.defaultBlockState());
    }

    public @Nullable StripableBlockTrait with(@Nullable BlockState strippedBlock) {
        if (strippedBlock == null) return null;
        return new Trait((oldState) -> strippedBlock);
    }

    public @Nullable StripableBlockTrait with(@Nullable StripableBlockTrait.BlockStateFactory strippedBlock) {
        if (strippedBlock == null) return null;
        return new Trait(strippedBlock);
    }

    public BlockState getStrippedBlockState(BlockState oldState) {
        final Block block = oldState.getBlock();
        var traits = BUILDER.getRuntimeTraits(block);
        if (traits == null || traits.isEmpty()) {
            return oldState;
        }
        return traits.get(0).strippedBlock(oldState);
    }

    class Trait extends BlockTraitImpl<Block, StripableBlockTrait> implements StripableBlockTrait, RuntimeBlockTrait<Block, StripableBlockTrait> {
        private final StripableBlockTrait.BlockStateFactory strippedBlock;

        Trait(StripableBlockTrait.BlockStateFactory strippedBlock) {
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
        public @NotNull BlockState strippedBlock(@Nullable BlockState oldState) {
            return this.strippedBlock.create(oldState);
        }
    }
}
