package org.betterx.wover.block.api.client.trait;

import org.betterx.wover.block.impl.client.trait.BlockModelTraitBuilder;
import org.betterx.wover.block.impl.client.trait.ChestRenderTraitBuilder;
import org.betterx.wover.block.impl.client.trait.RenderLayerTraitBuilder;
import org.betterx.wover.item.api.client.trait.BoatRendererTrait;
import org.betterx.wover.item.impl.client.trait.BoatRendererTraitBuilder;

/**
 * Central registry of every ready-made client-only {@link org.betterx.wover.block.api.trait.BlockTrait} builder
 * shipped by {@code wover-sets-api}, mirroring {@link org.betterx.wover.block.api.trait.BlockTraits} on the
 * client side.
 */
public class ClientBlockTraits {
    /** Attaches a code-driven client model factory to a block, see {@link BlockModelTrait}. */
    public static final BlockModelTrait.Builder MODEL = BlockModelTraitBuilder.BUILDER;
    /** Registers default boat/chest-boat rendering for a boat item. */
    public static final BoatRendererTrait.Builder BOAT_RENDERER = BoatRendererTraitBuilder.BUILDER;
    /** Attaches the render materials a custom chest block needs, see {@link ChestRenderTrait}. */
    public static final ChestRenderTrait.Builder CHEST_RENDERER = ChestRenderTraitBuilder.BUILDER;
    /** Selects the render layer (cutout/translucent) a block should use, see {@link RenderLayerTrait}. */
    public static final RenderLayerTrait.Builder RENDER_LAYER = RenderLayerTraitBuilder.BUILDER;
}
