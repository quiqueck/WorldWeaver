package de.ambertation.wover.item.api.trait;

import de.ambertation.wover.item.impl.trait.ItemTraitImpl;

/**
 * Implemented by types (namely {@link de.ambertation.wover.item.api.ItemDefinition}) that can be queried for
 * whether a configured {@link ItemTrait} is present, identified by its {@link ItemTraitKey}.
 *
 * @see ItemTraitKey
 * @see ItemTrait
 */
public interface ItemTraitLookup {
    /**
     * Checks whether a trait with the same {@link ItemTraitKey} as the given trait is present.
     *
     * @param trait The trait to check for, may be {@code null}
     * @return {@code true} if a trait with the same key is present, {@code false} otherwise
     */
    boolean hasTrait(ItemTraitImpl<?, ?> trait);

    /**
     * Checks whether a trait with the given {@link ItemTraitKey} is present.
     *
     * @param traitKey The trait key to check for, may be {@code null}
     * @return {@code true} if a trait with the given key is present, {@code false} otherwise
     */
    boolean hasTrait(ItemTraitKey traitKey);

    /**
     * Checks whether a trait with the same {@link ItemTraitKey} as the given trait builder is present.
     *
     * @param traitBuilder The trait builder to check for, may be {@code null}
     * @return {@code true} if a trait with the same key is present, {@code false} otherwise
     */
    boolean hasTrait(ItemTraitBuilder<?, ?> traitBuilder);
}