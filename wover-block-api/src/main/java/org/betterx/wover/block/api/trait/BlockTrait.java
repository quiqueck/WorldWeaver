package org.betterx.wover.block.api.trait;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.entrypoint.LibWoverEvents;

import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BlockTrait<B extends Block, R extends RuntimeBlockTrait<B, R>> {
    public static final BlockTrait.VoidRuntime<Block> VOID_RUNTIME = new BlockTrait.VoidRuntime<>();

    public abstract static class TraitBuilder {
        private static final List<BlockTrait<?, ?>> EMPTY = List.of();
        public final BlockTraitKey ID;

        protected TraitBuilder(BlockTraitKey id) {
            this.ID = id;
        }

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

        public <B extends Block, R extends RuntimeBlockTrait<B, R>> List<R> getRuntimeTraits(B block) {
            return BlockTrait.getRuntimeTraits(block, ID);
        }
    }

    public static final class VoidRuntime<B extends Block> extends RuntimeBlockTrait<B, VoidRuntime<B>> {
        public static final BlockTraitKey ID = BlockTraitKey.of(LibWoverEvents.C, "void_trait");

        private VoidRuntime() {
            super(ID);
        }
    }

    @SuppressWarnings("unchecked")
    public static <B extends Block> @Nullable BlockWithTraits<B> asBlockWithTraits(
            @Nullable B block
    ) {
        if (block instanceof BlockWithTraits<?> blockWithTraits) {
            return (BlockWithTraits<B>) blockWithTraits;
        }
        return null;
    }

    public static <B extends Block> @NotNull BlockWithTraits<B> asBlockWithTraitsOrThrow(
            @Nullable B block
    ) {
        BlockWithTraits<B> blockWithTraits = asBlockWithTraits(block);
        if (blockWithTraits == null) {
            throw new IllegalArgumentException("Block does not implement BlockWithTraits: " + block);
        }
        return blockWithTraits;
    }

    public static @NotNull Stream<? extends RuntimeBlockTrait<?, ?>> runtimeTraits(
            @Nullable Block block
    ) {
        var blockWithTraits = asBlockWithTraits(block);
        if (blockWithTraits == null) return Stream.empty();
        ;
        final var traits = blockWithTraits.wover_traits();
        if (traits == null || traits.isEmpty()) {
            return Stream.empty();
        }
        return traits.stream();
    }

    public static boolean hasRuntimeTraits(Block block) {
        var blockWithTraits = asBlockWithTraits(block);
        if (blockWithTraits == null) return false;
        final var traits = blockWithTraits.wover_traits();
        return traits != null && !traits.isEmpty();
    }

    public static boolean hasRuntimeTrait(@Nullable Block block, BlockTraitKey traitKey) {
        var blockWithTraits = asBlockWithTraits(block);
        if (blockWithTraits == null) return false;

        final var traits = blockWithTraits.wover_traits();
        return traits != null && traits.stream().anyMatch(t -> t.is(traitKey));
    }

    public static <B extends Block, R extends RuntimeBlockTrait<B, R>> @Nullable List<R> getRuntimeTraits(
            B block,
            BlockTraitKey traitKey
    ) {
        var blockWithTraits = asBlockWithTraits(block);
        if (blockWithTraits == null) return null;
        List<R> result = new ArrayList<>();
        final var traits = blockWithTraits.wover_traits();
        if (traits != null) {
            for (RuntimeBlockTrait<B, ?> trait : traits) {
                if (trait != null && trait.is(traitKey)) {
                    result.add((R) trait);
                }
            }
        }

        return result;
    }


    public final BlockTraitKey traitID;

    protected BlockTrait(BlockTraitKey id) {
        this.traitID = id;
    }


    public R forRuntime() {
        return null;
    }

    public boolean is(@Nullable RuntimeBlockTrait<?, ?> trait) {
        if (trait == null) return false;
        return trait.is(this);
    }

    public boolean is(@Nullable BlockTraitKey traitID) {
        if (traitID == null) return false;
        return traitID.equals(this.traitID);
    }

    public boolean is(@Nullable BlockTrait<?, ?> trait) {
        if (trait == null) return false;
        return this.is(trait.traitID);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj != null && getClass() == obj.getClass()) return true;
        if (obj instanceof RuntimeBlockTrait<?, ?> rt) {
            return this.is(rt);
        }
        if (obj instanceof BlockTraitKey key) {
            return this.is(key);
        }
        if (obj instanceof BlockTrait<?, ?> bt) {
            return this.is(bt);
        }

        return super.equals(obj);
    }

    public void configure(BlockDefinition<B, ? extends BlockDefinition<B, ?>> definition) {
        // Default implementation does nothing
    }

    public void afterBlockRegistration(
            B block,
            BlockDefinition<B, ? extends BlockDefinition<B, ?>> definition
    ) {
        // Default implementation does nothing
    }

}
