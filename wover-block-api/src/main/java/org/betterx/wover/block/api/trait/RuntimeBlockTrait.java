package org.betterx.wover.block.api.trait;

import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RuntimeBlockTrait<B extends Block, R extends RuntimeBlockTrait<B, R>> {
    public final BlockTraitKey traitID;

    @SuppressWarnings("unchecked")
    protected RuntimeBlockTrait(@NotNull BlockTrait<B, R> sourceTrait) {
        this(sourceTrait.traitID);
    }

    @SuppressWarnings("unchecked")
    protected RuntimeBlockTrait(BlockTraitKey traitID) {
        this.traitID = traitID;
    }

    public boolean is(@Nullable BlockTrait<?, ?> trait) {
        if (trait == null) return false;
        return this.is(trait.traitID);
    }

    public boolean is(@Nullable BlockTraitKey traitID) {
        if (traitID == null) return false;
        return this.traitID.equals(traitID);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj != null && getClass() == obj.getClass()) return true;
        if (obj instanceof BlockTraitKey key) {
            return this.is(key);
        }
        if (obj instanceof BlockTrait<?, ?> bt) {
            return this.is(bt);
        }

        return super.equals(obj);
    }
}
