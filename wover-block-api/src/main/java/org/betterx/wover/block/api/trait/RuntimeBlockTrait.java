package org.betterx.wover.block.api.trait;

import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

public interface RuntimeBlockTrait<B extends Block, R extends RuntimeBlockTrait<B, R>> {
    boolean is(@Nullable BlockTrait<?, ?> trait);
    boolean is(@Nullable BlockTraitKey traitKey);
    BlockTraitKey key();
}
