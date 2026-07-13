package org.betterx.wover.block.api.trait;

import org.betterx.wover.block.impl.trait.BlockTraitImpl;

/**
 * Implemented by {@link org.betterx.wover.block.api.BlockDefinition} to check which {@link BlockTrait}s
 * have been added to it while it is being configured.
 */
public interface BlockTraitLookup {
    /**
     * Checks whether a trait matching the given trait instance was already added.
     *
     * @param trait The trait to check for
     * @return {@code true} if a matching trait was already added
     */
    boolean hasTrait(BlockTraitImpl<?, ?> trait);

    /**
     * Checks whether a trait with the given key was already added.
     *
     * @param traitKey The trait key to check for
     * @return {@code true} if a matching trait was already added
     */
    boolean hasTrait(BlockTraitKey traitKey);

    /**
     * Checks whether a trait matching the given builder's key was already added.
     *
     * @param traitBuilder The trait builder whose key should be checked for
     * @return {@code true} if a matching trait was already added
     */
    boolean hasTrait(BlockTraitBuilder<?, ?> traitBuilder);
}
