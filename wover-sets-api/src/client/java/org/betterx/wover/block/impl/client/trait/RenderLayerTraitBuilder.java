package org.betterx.wover.block.impl.client.trait;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.client.trait.RenderLayerTrait;
import org.betterx.wover.block.api.trait.AbstractBlockTraitBuilder;
import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitKey;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverSets;

import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.block.Block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RenderLayerTraitBuilder extends AbstractBlockTraitBuilder<Block, RenderLayerTrait> implements RenderLayerTrait.Builder {
    public static final RenderLayerTrait.Builder BUILDER = new RenderLayerTraitBuilder();

    protected RenderLayerTraitBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "render_layer"));
    }

    @Override
    public @Nullable BlockTrait<?, ?> with(@NotNull RenderLayerTrait.Layer layer) {
        if (ModCore.isClient()) return new Trait(layer);
        return null;
    }

    @Environment(EnvType.CLIENT)
    private class Trait extends BlockTraitImpl<Block, RenderLayerTrait> implements RenderLayerTrait {
        final @NotNull Layer layer;

        private Trait(@NotNull Layer layer) {
            this.layer = layer;
        }

        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public @NotNull Layer layer() {
            return this.layer;
        }

        @Override
        public void afterBlockRegistration(
                Block block,
                BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition
        ) {
            if (layer == Layer.CUTOUT) BlockRenderLayerMap.putBlock(block, ChunkSectionLayer.CUTOUT);
            else if (layer == Layer.TRANSLUCENT) BlockRenderLayerMap.putBlock(block, ChunkSectionLayer.TRANSLUCENT);
        }
    }
}

