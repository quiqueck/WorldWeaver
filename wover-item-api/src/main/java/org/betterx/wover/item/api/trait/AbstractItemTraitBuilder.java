package org.betterx.wover.item.api.trait;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Base implementation of {@link ItemTraitBuilder} that stores the shared {@link ItemTraitKey} and provides
 * {@link #combine} helper methods for implementations of {@link ItemTraitBuilder.WithDefaults} that need to
 * bundle several (possibly {@code null}) traits into a single list.
 *
 * <p>Concrete trait builders (typically exposed as singleton constants) extend this class, implement
 * {@link ItemTraitBuilder.WithDefault}/{@link ItemTraitBuilder.WithDefaults} as needed, and nest their
 * {@link ItemTrait} implementation as an inner class.
 *
 * @param <I> The item type the built traits apply to
 * @param <R> The runtime trait type produced by traits built with this builder
 */
public abstract class AbstractItemTraitBuilder<I extends Item, R extends RuntimeItemTrait<I, R>> implements ItemTraitBuilder<I, R> {
    /**
     * The key shared by every trait produced by this builder
     */
    public final ItemTraitKey traitKey;

    /**
     * Creates a new trait builder for the given trait key.
     *
     * @param traitKey The key shared by every trait produced by this builder
     */
    protected AbstractItemTraitBuilder(ItemTraitKey traitKey) {
        this.traitKey = traitKey;
    }

    /**
     * {@inheritDoc}
     */
    public ItemTraitKey key() {
        return traitKey;
    }

    /**
     * {@inheritDoc}
     */
    public List<R> getRuntimeTraits(I item) {
        return ItemTrait.getRuntimeTraits(item, traitKey);
    }

    // Static interface
    private static final List<ItemTrait<?, ?>> EMPTY = List.of();

    /**
     * Combines a single (possibly {@code null}) trait into a list, omitting {@code null}.
     *
     * @param t0 The trait to include, may be {@code null}
     * @return A list containing the non-null trait, or an empty list
     */
    protected static @NotNull List<ItemTrait<?, ?>> combine(ItemTrait<?, ?> t0) {
        if (t0 == null) return EMPTY;
        return List.of(t0);
    }

    /**
     * Combines up to two (possibly {@code null}) traits into a list, omitting any {@code null} entries.
     *
     * @param t0 The first trait, may be {@code null}
     * @param t1 The second trait, may be {@code null}
     * @return A list containing the non-null traits, or an empty list
     */
    protected static @NotNull List<ItemTrait<?, ?>> combine(ItemTrait<?, ?> t0, ItemTrait<?, ?> t1) {
        if (t0 == null && t1 == null) return EMPTY;
        if (t0 == null) return List.of(t1);
        if (t1 == null) return List.of(t0);
        return List.of(t0, t1);
    }

    /**
     * Combines up to three (possibly {@code null}) traits into a list, omitting any {@code null} entries.
     *
     * @param t0 The first trait, may be {@code null}
     * @param t1 The second trait, may be {@code null}
     * @param t2 The third trait, may be {@code null}
     * @return A list containing the non-null traits, or an empty list
     */
    protected static @NotNull List<ItemTrait<?, ?>> combine(
            ItemTrait<?, ?> t0,
            ItemTrait<?, ?> t1,
            ItemTrait<?, ?> t2
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
     * Combines an arbitrary number of (possibly {@code null}) traits into a list, omitting any {@code null} entries.
     *
     * @param traits The traits to combine, may be {@code null} or contain {@code null} entries
     * @return A list containing the non-null traits, or an empty list
     */
    protected static @NotNull List<ItemTrait<?, ?>> combine(ItemTrait<?, ?>... traits) {
        if (traits == null || traits.length == 0) return EMPTY;
        List<ItemTrait<?, ?>> result = new ArrayList<>();
        for (ItemTrait<?, ?> trait : traits) {
            if (trait != null) {
                result.add(trait);
            }
        }
        return result.isEmpty() ? EMPTY : result;
    }

    /**
     * Convenience base class for trait builders that build {@link GenericItemTrait}s (traits that apply to any
     * {@link Item} rather than a specific subtype).
     */
    public abstract static class Generic extends AbstractItemTraitBuilder<Item, GenericItemTrait> implements GenericItemTrait.Builder {
        /**
         * Creates a new generic trait builder for the given trait key.
         *
         * @param traitKey The key shared by every trait produced by this builder
         */
        public Generic(ItemTraitKey traitKey) {
            super(traitKey);
        }
    }
}
