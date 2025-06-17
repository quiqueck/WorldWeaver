package org.betterx.wover.block.mixin;

import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockWithTraits;

import net.minecraft.world.level.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.Nullable;

@Mixin(Block.class)
public class BlockMixin<B extends Block> implements BlockWithTraits<B> {
    @Unique
    private @Nullable List<BlockTrait.RuntimeTrait<B, ?>> wover_traits;


    @Override
    public void wover_setTraits(@Nullable List<BlockTrait.RuntimeTrait<B, ?>> runtimeTraits) {
        this.wover_traits = runtimeTraits;
    }

    @Unique
    public @Nullable Collection<BlockTrait.RuntimeTrait<B, ?>> wover_traits() {
        return wover_traits;
    }
}
