package org.betterx.wover.block.api.client.trait;

import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitBuilder;

import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link BlockTrait} selecting the non-solid {@code net.minecraft.client.renderer.RenderType} a block should
 * be rendered with ({@code RenderType.cutout()} or {@code RenderType.translucent()}), for blocks that don't use
 * vanilla's default solid render layer (e.g. leaves, glass-like blocks).
 */
public interface RenderLayerTrait extends BlockTrait<Block, RenderLayerTrait> {
    /**
     * The render layers this trait can select.
     */
    enum Layer {
        /** {@code RenderType.cutout()} - fully opaque or fully transparent pixels, no blending. */
        CUTOUT,
        /** {@code RenderType.translucent()} - alpha-blended, partially transparent pixels. */
        TRANSLUCENT
    }

    /**
     * Builds {@link RenderLayerTrait} instances.
     */
    interface Builder extends BlockTraitBuilder<Block, RenderLayerTrait> {
        /**
         * @param layer the render layer to select
         * @return the new trait
         */
        @Nullable BlockTrait<?, ?> with(@NotNull RenderLayerTrait.Layer layer);

        /**
         * @return a trait selecting {@link Layer#CUTOUT}
         */
        @Nullable BlockTrait<?, ?> cutout();

        /**
         * @return a trait selecting {@link Layer#TRANSLUCENT}
         */
        @Nullable BlockTrait<?, ?> translucent();
    }

    /**
     * @return the render layer selected by this trait
     */
    @NotNull Layer layer();
}
