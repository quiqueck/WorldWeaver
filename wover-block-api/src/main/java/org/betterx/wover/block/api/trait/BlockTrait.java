package org.betterx.wover.block.api.trait;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.entrypoint.LibWoverEvents;

import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BlockTrait<B extends Block, R extends BlockTrait.RuntimeTrait<B, R>> {
    public static final BlockTrait.VoidRuntime VOID_RUNTIME = new BlockTrait.VoidRuntime();

    public abstract static class TraitBuilder {
        public final BlockTraitKey ID;

        protected TraitBuilder(BlockTraitKey id) {
            this.ID = id;
        }

        public <B extends Block, R extends BlockTrait.RuntimeTrait<B, R>> List<R> getRuntimeTraits(B block) {
            return BlockTrait.getRuntimeTraits(block, ID);
        }
    }

    public static final class VoidRuntime extends BlockTrait.RuntimeTrait<Block, BlockTrait.VoidRuntime> {
        public static final BlockTraitKey ID = BlockTraitKey.of(LibWoverEvents.C, "void_trait");

        private VoidRuntime() {
            super(ID);
        }
    }

    public static class RuntimeTrait<B extends Block, R extends BlockTrait.RuntimeTrait<B, R>> {
        public final BlockTraitKey traitID;

        @SuppressWarnings("unchecked")
        protected RuntimeTrait(@NotNull BlockTrait<B, R> sourceTrait) {
            this(sourceTrait.traitID);
        }

        @SuppressWarnings("unchecked")
        protected RuntimeTrait(BlockTraitKey traitID) {
            this.traitID = traitID;
        }

        public boolean is(@Nullable BlockTrait<?, ?> trait) {
            if (trait == null) return false;
            return this.is(trait.traitID);
        }

        public boolean is(@Nullable BlockTraitKey traitID) {
            if (traitID == null) return false;
            return this.traitID.equals(traitID);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj != null && getClass() == obj.getClass()) return true;
            if (obj instanceof BlockTraitKey key) {
                return this.is(key);
            }
            if (obj instanceof BlockTrait<?, ?> bt) {
                return this.is(bt);
            }

            return super.equals(obj);
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

    public static boolean hasRuntimeTraits(Block block) {
        var blockWithTraits = asBlockWithTraits(block);
        if (blockWithTraits == null) return false;
        final var traits = blockWithTraits.wover_traits();
        return traits != null && !traits.isEmpty();
    }


    public static <B extends Block> void forEachRuntimeTrait(
            @Nullable B block,
            @NotNull BiConsumer<B, RuntimeTrait<B, ?>> consumer
    ) {
        BlockWithTraits<B> blockWithTraits = asBlockWithTraits(block);
        if (blockWithTraits == null) return;
        final var traits = blockWithTraits.wover_traits();
        if (traits != null) {
            traits.forEach(trait -> consumer.accept(block, trait));
        }
    }

    public final BlockTraitKey traitID;

    protected BlockTrait(BlockTraitKey id) {
        this.traitID = id;
    }


    public static boolean hasRuntimeTrait(@Nullable Block block, BlockTraitKey traitKey) {
        var blockWithTraits = asBlockWithTraits(block);
        if (blockWithTraits == null) return false;

        final var traits = blockWithTraits.wover_traits();
        return traits != null && traits.stream().anyMatch(t -> t.is(traitKey));
    }

    public static <B extends Block, R extends BlockTrait.RuntimeTrait<B, R>> @Nullable List<R> getRuntimeTraits(
            B block,
            BlockTraitKey traitKey
    ) {
        var blockWithTraits = asBlockWithTraits(block);
        if (blockWithTraits == null) return null;
        List<R> result = new ArrayList<>();
        final var traits = blockWithTraits.wover_traits();
        if (traits != null) {
            for (BlockTrait.RuntimeTrait<B, ?> trait : traits) {
                if (trait != null && trait.is(traitKey)) {
                    result.add((R) trait);
                }
            }
        }

        return result;
    }


    public boolean datagenOnly() {
        return this.clientOnly();
    }

    public boolean clientOnly() {
        return false;
    }

    public R forRuntime() {
        return null;
    }

    public boolean is(@Nullable RuntimeTrait<?, ?> trait) {
        if (trait == null) return false;
        return trait.is(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj != null && getClass() == obj.getClass()) return true;
        if (obj instanceof BlockTrait.RuntimeTrait<?, ?> rt) {
            return this.is(rt);
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
