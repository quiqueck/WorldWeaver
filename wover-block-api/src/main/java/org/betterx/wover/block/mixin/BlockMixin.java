package org.betterx.wover.block.mixin;

import org.betterx.wover.block.api.trait.BlockWithTraits;
import org.betterx.wover.block.api.trait.RuntimeBlockTrait;

import net.minecraft.world.level.block.Block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.Nullable;

@Mixin(Block.class)
public class BlockMixin<B extends Block> implements BlockWithTraits<B> {
    @Unique
    private @Nullable List<RuntimeBlockTrait<B, ?>> wover_traits;


    @Override
    public void wover_setTraits(@Nullable List<RuntimeBlockTrait<B, ?>> runtimeTraits) {
        this.wover_traits = runtimeTraits;
    }

    @Unique
    public @Nullable Collection<RuntimeBlockTrait<B, ?>> wover_traits() {
        return wover_traits;
    }
}
