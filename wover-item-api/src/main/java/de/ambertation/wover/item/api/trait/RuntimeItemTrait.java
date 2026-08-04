package de.ambertation.wover.item.api.trait;

import net.minecraft.world.item.Item;

import org.jetbrains.annotations.Nullable;

/**
 * The runtime counterpart of an {@link ItemTrait}: the part of a trait's configuration that survives after the
 * item was built and is attached to the item instance (via {@link ItemWithTraits}) so it can be inspected while
 * the game is running, e.g. to change item behavior based on which traits it carries.
 *
 * @param <I> The item type this trait applies to
 * @param <R> The concrete runtime trait type, for {@code is(...)}/equality comparisons
 * @see ItemTrait#forRuntime()
 * @see ItemWithTraits
 */
public interface RuntimeItemTrait<I extends Item, R extends RuntimeItemTrait<I, R>> {
    /**
     * The key identifying which kind of trait this is.
     *
     * @return The trait key
     */
    ItemTraitKey key();

    /**
     * Checks whether this trait has the given key.
     *
     * @param traitID The trait key to compare against, may be {@code null}
     * @return {@code true} if this trait's {@link #key()} equals the given key, {@code false} otherwise
     */
    default boolean is(@Nullable ItemTraitKey traitID) {
        if (traitID == null) return false;
        return traitID.equals(this.key());
    }

    /**
     * Checks whether this trait has the same key as another runtime trait.
     *
     * @param trait The trait to compare against, may be {@code null}
     * @return {@code true} if both traits share the same {@link #key()}, {@code false} otherwise
     */
    default boolean is(@Nullable RuntimeItemTrait<?, ?> trait) {
        if (trait == null) return false;
        return this.is(trait.key());
    }

    /**
     * Checks whether this trait was produced by the given trait builder.
     *
     * @param traitBuilder The trait builder to compare against, may be {@code null}
     * @return {@code true} if this trait's {@link #key()} equals the builder's key, {@code false} otherwise
     */
    default boolean is(@Nullable ItemTraitBuilder<?, ?> traitBuilder) {
        if (traitBuilder == null) return false;
        return this.is(traitBuilder.key());
    }
}
