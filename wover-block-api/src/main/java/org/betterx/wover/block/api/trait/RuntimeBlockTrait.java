package org.betterx.wover.block.api.trait;

import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

/**
 * The runtime-facing counterpart of a {@link BlockTrait}: the part of a trait that survives on the finished
 * block (see {@link BlockWithTraits}) after the {@link org.betterx.wover.block.api.BlockDefinition} that
 * configured it is gone.
 *
 * @param <B> The type of {@link Block} this trait applies to
 * @param <R> The concrete runtime trait type
 */
public interface RuntimeBlockTrait<B extends Block, R extends RuntimeBlockTrait<B, R>> {
    /**
     * Checks whether this trait's key equals {@code traitKey}.
     *
     * @param traitKey The key to compare against
     * @return {@code true} if the keys match
     */
    default boolean is(@Nullable BlockTraitKey traitKey) {
        if (traitKey == null) return false;
        return key().equals(traitKey);
    }

    /**
     * Checks whether this trait's key equals {@code trait}'s key.
     *
     * @param trait The trait to compare against
     * @return {@code true} if the keys match
     */
    default boolean is(@Nullable RuntimeBlockTrait<?, ?> trait) {
        if (trait == null) return false;
        return is(trait.key());
    }

    /**
     * Checks whether this trait's key equals {@code traitBuilder}'s key.
     *
     * @param traitBuilder The trait builder to compare against
     * @return {@code true} if the keys match
     */
    default boolean is(@Nullable BlockTraitBuilder traitBuilder) {
        if (traitBuilder == null) return false;
        return is(traitBuilder.key());
    }

    /**
     * Gets the unique key identifying this trait.
     *
     * @return The trait key
     */
    BlockTraitKey key();
}
