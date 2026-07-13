package org.betterx.wover.block.api.trait;

import net.minecraft.world.level.block.Block;

import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Convenience base class for implementing {@link BlockTraitBuilder}. Handles storing the trait's
 * {@link BlockTraitKey} and implementing {@link #key()}/{@link #getRuntimeTraits(Block)}, and offers
 * {@code combine(...)} helpers (delegating to {@link Combiner}) for builders that need to bundle several
 * {@link BlockTrait}s behind a single {@code withDefault()} call.
 *
 * @param <B> The type of {@link Block} the built traits apply to
 * @param <R> The runtime trait type produced by the built traits
 */
public abstract class AbstractBlockTraitBuilder<B extends Block, R extends RuntimeBlockTrait<B, R>> implements BlockTraitBuilder<B, R> {
    /**
     * The unique key identifying traits created by this builder.
     */
    public final BlockTraitKey traitKey;

    /**
     * Creates a new builder for the given trait key.
     *
     * @param traitKey The unique key identifying traits created by this builder
     */
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

    /**
     * Wraps a single (possibly {@code null}) trait into a list, for use as a builder's {@code withDefault()} result.
     *
     * @param t0 The trait to wrap
     * @return A list containing the non-null trait(s)
     */
    protected static @NotNull List<BlockTrait<?, ?>> combine(BlockTrait<?, ?> t0) {
        return Combiner.combine(t0);
    }

    /**
     * Wraps up to two (possibly {@code null}) traits into a list, for use as a builder's {@code withDefault()} result.
     *
     * @param t0 The first trait to wrap
     * @param t1 The second trait to wrap
     * @return A list containing the non-null trait(s)
     */
    protected static @NotNull List<BlockTrait<?, ?>> combine(BlockTrait<?, ?> t0, BlockTrait<?, ?> t1) {
        return Combiner.combine(t0, t1);
    }

    /**
     * Wraps up to three (possibly {@code null}) traits into a list, for use as a builder's {@code withDefault()} result.
     *
     * @param t0 The first trait to wrap
     * @param t1 The second trait to wrap
     * @param t2 The third trait to wrap
     * @return A list containing the non-null trait(s)
     */
    protected static @NotNull List<BlockTrait<?, ?>> combine(
            BlockTrait<?, ?> t0,
            BlockTrait<?, ?> t1,
            BlockTrait<?, ?> t2
    ) {
        return Combiner.combine(t0, t1, t2);
    }

    /**
     * Wraps any number of (possibly {@code null}) traits into a list, for use as a builder's {@code withDefault()} result.
     *
     * @param traits The traits to wrap
     * @return A list containing the non-null trait(s)
     */
    protected static @NotNull List<BlockTrait<?, ?>> combine(BlockTrait<?, ?>... traits) {
        return Combiner.of(traits).combine();
    }

    /**
     * Convenience base class for builders of {@link GenericBlockTrait}s, i.e. traits that apply to
     * {@link Block} rather than to a specific block subclass.
     */
    public abstract static class Generic extends AbstractBlockTraitBuilder<Block, GenericBlockTrait> implements GenericBlockTrait.Builder {
        /**
         * Creates a new builder for the given trait key.
         *
         * @param traitKey The unique key identifying traits created by this builder
         */
        public Generic(BlockTraitKey traitKey) {
            super(traitKey);
        }
    }
}
