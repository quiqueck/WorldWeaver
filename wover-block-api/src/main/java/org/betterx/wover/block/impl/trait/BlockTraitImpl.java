package org.betterx.wover.block.impl.trait;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitKey;
import org.betterx.wover.block.api.trait.GenericBlockTrait;
import org.betterx.wover.block.api.trait.RuntimeBlockTrait;

import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

public abstract class BlockTraitImpl<B extends Block, R extends RuntimeBlockTrait<B, R>> implements BlockTrait<B, R> {
    protected BlockTraitImpl() {

    }


    @Override
    public R forRuntime() {
        return null;
    }

    @Override
    public boolean is(@Nullable RuntimeBlockTrait<?, ?> trait) {
        if (trait == null) return false;
        return trait.is(this);
    }

    @Override
    public boolean is(@Nullable BlockTraitKey traitKey) {
        if (traitKey == null) return false;
        return traitKey.equals(this.key());
    }

    @Override
    public boolean is(@Nullable BlockTrait<?, ?> trait) {
        if (trait == null) return false;
        return this.is(trait.key());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj != null && getClass() == obj.getClass()) return true;
        if (obj instanceof RuntimeBlockTrait<?, ?> rt) {
            return this.is(rt);
        }
        if (obj instanceof BlockTraitKey key) {
            return this.is(key);
        }
        if (obj instanceof BlockTrait<?, ?> bt) {
            return this.is(bt);
        }

        return super.equals(obj);
    }

    @Override
    public void configure(BlockDefinition<B, ? extends BlockDefinition<B, ?>> definition) {
        // Default implementation does nothing
    }

    @Override
    public void afterBlockRegistration(
            B block,
            BlockDefinition<B, ? extends BlockDefinition<B, ?>> definition
    ) {
        // Default implementation does nothing
    }

    public abstract static class Generic extends BlockTraitImpl<Block, BlockTraitImpl.VoidRuntime<Block>> implements GenericBlockTrait {

    }
}
