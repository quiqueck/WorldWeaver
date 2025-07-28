package org.betterx.wover.block.api.trait;

import net.minecraft.world.level.block.Block;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractBlockTraitBuilder<B extends Block, R extends RuntimeBlockTrait<B, R>> implements BlockTraitBuilder<B, R> {
    public final BlockTraitKey traitKey;

    protected AbstractBlockTraitBuilder(BlockTraitKey traitKey) {
        this.traitKey = traitKey;
    }

    @Override
    public @NotNull BlockTraitKey key() {
        return traitKey;
    }

    @Override
    public List<R> getRuntimeTraits(B block) {
        return BlockTrait.getRuntimeTraits(block, traitKey);
    }


    // Static interface
    private static final List<BlockTrait<?, ?>> EMPTY = List.of();

    protected static @NotNull List<BlockTrait<?, ?>> combine(BlockTrait<?, ?> t0) {
        return Combiner.combine(t0);
    }

    protected static @NotNull List<BlockTrait<?, ?>> combine(BlockTrait<?, ?> t0, BlockTrait<?, ?> t1) {
        return Combiner.combine(t0, t1);
    }

    protected static @NotNull List<BlockTrait<?, ?>> combine(
            BlockTrait<?, ?> t0,
            BlockTrait<?, ?> t1,
            BlockTrait<?, ?> t2
    ) {
        return Combiner.combine(t0, t1, t2);
    }

    protected static @NotNull List<BlockTrait<?, ?>> combine(BlockTrait<?, ?>... traits) {
        return Combiner.of(traits).combine();
    }

    public abstract static class Generic extends AbstractBlockTraitBuilder<Block, GenericBlockTrait> implements GenericBlockTrait.Builder {
        public Generic(BlockTraitKey traitKey) {
            super(traitKey);
        }
    }
}
