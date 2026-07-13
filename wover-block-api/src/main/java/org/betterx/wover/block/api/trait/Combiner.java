package org.betterx.wover.block.api.trait;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Fluent helper for building the {@code null}-filtering, flattened {@link List} of {@link BlockTrait}s that
 * a {@link BlockTraitBuilder.WithDefaults#withDefault()} implementation needs to return. Useful for
 * builders that bundle several independent traits behind one {@code withDefault()} call.
 * <p>
 * The static {@code combine(...)} overloads are shorthands for the common case of combining up to three
 * fixed traits without needing to create a {@link Combiner} instance.
 */
public class Combiner {
    private static final List<BlockTrait<?, ?>> EMPTY = List.of();

    /**
     * Wraps a single (possibly {@code null}) trait into a list.
     *
     * @param t0 The trait to wrap
     * @return A list containing the non-null trait(s)
     */
    public static @NotNull List<BlockTrait<?, ?>> combine(BlockTrait<?, ?> t0) {
        if (t0 == null) return EMPTY;
        return List.of(t0);
    }

    /**
     * Wraps up to two (possibly {@code null}) traits into a list.
     *
     * @param t0 The first trait to wrap
     * @param t1 The second trait to wrap
     * @return A list containing the non-null trait(s)
     */
    public static @NotNull List<BlockTrait<?, ?>> combine(BlockTrait<?, ?> t0, BlockTrait<?, ?> t1) {
        if (t0 == null && t1 == null) return EMPTY;
        if (t0 == null) return List.of(t1);
        if (t1 == null) return List.of(t0);
        return List.of(t0, t1);
    }

    /**
     * Wraps up to three (possibly {@code null}) traits into a list.
     *
     * @param t0 The first trait to wrap
     * @param t1 The second trait to wrap
     * @param t2 The third trait to wrap
     * @return A list containing the non-null trait(s)
     */
    public static @NotNull List<BlockTrait<?, ?>> combine(
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

    /**
     * Starts a new {@link Combiner} pre-filled with the (non-null) traits from the given list.
     *
     * @param traits The traits to add
     * @return A new combiner
     */
    public static @NotNull Combiner of(List<BlockTrait<?, ?>> traits) {
        return new Combiner().add(traits);
    }

    /**
     * Starts a new {@link Combiner} pre-filled with the given (non-null) traits.
     *
     * @param traits The traits to add
     * @return A new combiner
     */
    public static @NotNull Combiner of(BlockTrait<?, ?>... traits) {
        return new Combiner().add(traits);
    }

    /**
     * Starts a new {@link Combiner} pre-filled with the given builder's default trait.
     *
     * @param traitBuilder The trait builder whose default trait should be added
     * @return A new combiner
     */
    public static @NotNull Combiner of(@NotNull BlockTraitBuilder.WithDefault<?, ?> traitBuilder) {
        return new Combiner().add(traitBuilder.withDefault());
    }

    /**
     * Starts a new {@link Combiner} pre-filled with the given builder's default traits.
     *
     * @param traitBuilder The trait builder whose default traits should be added
     * @return A new combiner
     */
    public static @NotNull Combiner of(@NotNull BlockTraitBuilder.WithDefaults<?, ?> traitBuilder) {
        return new Combiner().add(traitBuilder.withDefault());
    }

    private final @NotNull List<BlockTrait<?, ?>> blockTraits;

    private Combiner() {
        blockTraits = new ArrayList<>();
    }

    /**
     * Adds the given (non-null) traits to this combiner.
     *
     * @param traits The traits to add
     * @return This combiner, for chaining
     */
    public Combiner add(BlockTrait<?, ?>... traits) {
        if (traits == null || traits.length == 0) return this;
        for (BlockTrait<?, ?> trait : traits) {
            if (trait != null) blockTraits.add(trait);
        }
        return this;
    }

    /**
     * Adds the given (non-null) traits to this combiner.
     *
     * @param traits The traits to add
     * @return This combiner, for chaining
     */
    public Combiner add(List<BlockTrait<?, ?>> traits) {
        if (traits != null && !traits.isEmpty()) {
            for (BlockTrait<?, ?> trait : traits) {
                if (trait != null) blockTraits.add(trait);
            }
        }
        return this;
    }

    /**
     * Adds the given builder's default trait to this combiner.
     *
     * @param traitBuilder The trait builder whose default trait should be added
     * @return This combiner, for chaining
     */
    public Combiner add(@NotNull BlockTraitBuilder.WithDefault<?, ?> traitBuilder) {
        return this.add(traitBuilder.withDefault());
    }

    /**
     * Adds the given builder's default traits to this combiner.
     *
     * @param traitBuilder The trait builder whose default traits should be added
     * @return This combiner, for chaining
     */
    public Combiner add(@NotNull BlockTraitBuilder.WithDefaults<?, ?> traitBuilder) {
        return this.add(traitBuilder.withDefault());
    }

    /**
     * Returns the combined list of (non-null) traits added so far.
     *
     * @return The combined trait list
     */
    public @NotNull List<BlockTrait<?, ?>> combine() {
        return blockTraits.isEmpty() ? EMPTY : blockTraits;
    }
}
