package de.ambertation.wover.item.api.trait;

import net.minecraft.world.item.Item;

import java.util.List;

/**
 * A factory for {@link ItemTrait} instances of a specific kind, identified by a single shared {@link ItemTraitKey}.
 * Trait builders are typically exposed as singleton constants (see {@code ItemTraits} in modules that add traits,
 * e.g. {@code wover-sets-api}) so callers can add the same kind of trait to multiple item definitions via
 * {@link de.ambertation.wover.item.api.ItemDefinition#addTrait(ItemTrait) addTrait(...)}.
 *
 * @param <I> The item type the built traits apply to
 * @param <R> The runtime trait type produced by traits built with this builder
 * @see AbstractItemTraitBuilder
 */
public interface ItemTraitBuilder<I extends Item, R extends RuntimeItemTrait<I, R>> {
    /**
     * A trait builder that can produce a single default-configured trait, for use with
     * {@link de.ambertation.wover.item.api.ItemDefinition#addTrait(WithDefault) addTrait(...)}.
     *
     * @param <I> The item type the built trait applies to
     * @param <R> The runtime trait type produced by the built trait
     */
    interface WithDefault<I extends Item, R extends RuntimeItemTrait<I, R>> extends ItemTraitBuilder<I, R> {
        /**
         * Creates the default-configured trait instance.
         *
         * @return The default trait instance
         */
        ItemTrait<?, ?> withDefault();
    }

    /**
     * A trait builder that can produce several default-configured traits at once, for use with
     * {@link de.ambertation.wover.item.api.ItemDefinition#addTrait(WithDefaults) addTrait(...)}.
     *
     * @param <I> The item type the built traits apply to
     * @param <R> The runtime trait type produced by the built traits
     */
    interface WithDefaults<I extends Item, R extends RuntimeItemTrait<I, R>> extends ItemTraitBuilder<I, R> {
        /**
         * Creates the default-configured trait instances.
         *
         * @return The default trait instances
         */
        List<ItemTrait<?, ?>> withDefault();
    }

    /**
     * The key shared by every trait produced by this builder.
     *
     * @return The trait key
     */
    ItemTraitKey key();

    /**
     * Gets the runtime traits of this builder's kind that are currently attached to the given item.
     *
     * @param item The item to inspect
     * @return The runtime traits found on the item, or {@code null}/empty if none are present
     */
    List<R> getRuntimeTraits(I item);
}
