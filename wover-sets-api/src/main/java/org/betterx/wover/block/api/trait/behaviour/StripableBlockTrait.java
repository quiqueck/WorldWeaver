package org.betterx.wover.block.api.trait.behaviour;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitKey;
import org.betterx.wover.block.api.trait.RuntimeBlockTrait;
import org.betterx.wover.entrypoint.LibWoverBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public class StripableBlockTrait extends BlockTrait<Block, StripableBlockTrait.RuntimeTrait> {
    public static final StripableBlockTrait.Builder BUILDER = new StripableBlockTrait.Builder();

    public static class Builder extends BlockTrait.TraitBuilder {
        private Builder() {
            super(BlockTraitKey.of(LibWoverBlock.C, "stripable"));
        }

        public StripableBlockTrait with(Block strippedBlock) {
            return new StripableBlockTrait(() -> strippedBlock.defaultBlockState());
        }

        public StripableBlockTrait with(BlockState strippedBlock) {
            return new StripableBlockTrait(() -> strippedBlock);
        }

        public StripableBlockTrait with(Supplier<BlockState> strippedBlock) {
            return new StripableBlockTrait(strippedBlock);
        }

        @Override
        @SuppressWarnings("unchecked")
        public @Nullable List<StripableBlockTrait.RuntimeTrait> getRuntimeTraits(Block block) {
            return BlockTrait.getRuntimeTraits(block, ID);
        }

        public BlockState getStrippedBlockState(BlockState blockState) {
            final Block block = blockState.getBlock();
            var traits = BUILDER.getRuntimeTraits(block);
            if (traits == null || traits.isEmpty()) {
                return blockState;
            }
            return traits.get(0).strippedBlock.get();
        }
    }

    public final static class RuntimeTrait extends RuntimeBlockTrait<Block, StripableBlockTrait.RuntimeTrait> {
        public final Supplier<BlockState> strippedBlock;

        private RuntimeTrait(StripableBlockTrait sourceTrait) {
            super(sourceTrait);
            this.strippedBlock = sourceTrait.strippedBlock;
        }
    }

    public final Supplier<BlockState> strippedBlock;

    StripableBlockTrait(Supplier<BlockState> strippedBlock) {
        super(BUILDER.ID);
        this.strippedBlock = strippedBlock;
    }


    @Override
    public StripableBlockTrait.RuntimeTrait forRuntime() {
        return new StripableBlockTrait.RuntimeTrait(this);
    }

    @Override
    public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
        definition.getProperties().ignitedByLava();
    }

    @Override
    public void afterBlockRegistration(
            Block block,
            BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition
    ) {

    }
}
