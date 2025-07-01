package org.betterx.wover.block.api.trait;

import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
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
        if (t0 == null) return EMPTY;
        return List.of(t0);
    }

    protected static @NotNull List<BlockTrait<?, ?>> combine(BlockTrait<?, ?> t0, BlockTrait<?, ?> t1) {
        if (t0 == null && t1 == null) return EMPTY;
        if (t0 == null) return List.of(t1);
        if (t1 == null) return List.of(t0);
        return List.of(t0, t1);
    }

    protected static @NotNull List<BlockTrait<?, ?>> combine(
            BlockTrait<?, ?> t0,
            BlockTrait<?, ?> t1,
            BlockTrait<?, ?> t2
    ) {
        if (t0 == null && t1 == null && t2 == null) return EMPTY;
        if (t0 == null && t1 == null) return List.of(t2);
        if (t0 == null && t2 == null) return List.of(t1);
        if (t1 == null && t2 == null) return List.of(t0);
        if (t0 == null) return combine(t1, t2);
        if (t1 == null) return combine(t0, t2);
        if (t2 == null) return combine(t0, t1);
        return List.of(t0, t1, t2);
    }

    protected static @NotNull List<BlockTrait<?, ?>> combine(BlockTrait<?, ?>... traits) {
        if (traits == null || traits.length == 0) return EMPTY;
        List<BlockTrait<?, ?>> result = new ArrayList<>();
        for (BlockTrait<?, ?> trait : traits) {
            if (trait != null) {
                result.add(trait);
            }
        }
        return result.isEmpty() ? EMPTY : result;
    }

    public abstract static class Generic extends AbstractBlockTraitBuilder<Block, GenericBlockTrait> implements GenericBlockTrait.Builder {
        public Generic(BlockTraitKey traitKey) {
            super(traitKey);
        }
    }
}
