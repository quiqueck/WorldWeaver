package org.betterx.wover.block.api.trait;

import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

public interface RuntimeBlockTrait<B extends Block, R extends RuntimeBlockTrait<B, R>> {
    default boolean is(@Nullable BlockTraitKey traitKey) {
        if (traitKey == null) return false;
        return key().equals(traitKey);
    }
    default boolean is(@Nullable RuntimeBlockTrait<?, ?> trait) {
        if (trait == null) return false;
        return is(trait.key());
    }
    default boolean is(@Nullable BlockTraitBuilder traitBuilder) {
        if (traitBuilder == null) return false;
        return is(traitBuilder.key());
    }
    BlockTraitKey key();
}
