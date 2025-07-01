package org.betterx.wover.block.impl.trait;

import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitKey;
import org.betterx.wover.block.api.trait.RuntimeBlockTrait;

import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RuntimeBlockTraitImpl<B extends Block, R extends RuntimeBlockTrait<B, R>> implements RuntimeBlockTrait<B, R> {
    private final BlockTraitKey traitKey;

    protected RuntimeBlockTraitImpl(@NotNull BlockTraitImpl<B, R> sourceTrait) {
        this(sourceTrait.key());
    }

    protected RuntimeBlockTraitImpl(BlockTraitKey key) {
        this.traitKey = key;
    }

    @Override
    public boolean is(@Nullable BlockTrait<?, ?> trait) {
        if (trait == null) return false;
        return this.is(trait.key());
    }

    @Override
    public boolean is(@Nullable BlockTraitKey traitKey) {
        if (traitKey == null) return false;
        return this.traitKey.equals(traitKey);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj != null && getClass() == obj.getClass()) return true;
        if (obj instanceof BlockTraitKey key) {
            return this.is(key);
        }
        if (obj instanceof BlockTraitImpl<?, ?> bt) {
            return this.is(bt);
        }

        return super.equals(obj);
    }

    @Override
    public BlockTraitKey key() {
        return traitKey;
    }
}
