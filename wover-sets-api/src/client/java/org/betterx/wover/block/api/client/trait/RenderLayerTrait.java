package org.betterx.wover.block.api.client.trait;

import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitBuilder;

import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RenderLayerTrait extends BlockTrait<Block, RenderLayerTrait> {
    enum Layer {
        CUTOUT, TRANSLUCENT
    }

    interface Builder extends BlockTraitBuilder<Block, RenderLayerTrait> {
        @Nullable BlockTrait<?, ?> with(@NotNull RenderLayerTrait.Layer layer);
    }

    @NotNull Layer layer();
}
