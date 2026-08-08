package de.ambertation.wover.block.api.client.trait;

import de.ambertation.wover.block.api.render.BlockRenderTraits;
import de.ambertation.wover.block.api.render.ChestRendererBinding;
import de.ambertation.wover.block.api.render.RenderLayerBuilder;
import de.ambertation.wover.block.api.render.TintBuilder;
import de.ambertation.wover.block.impl.client.trait.BlockModelTraitBuilder;
import de.ambertation.wover.item.api.render.BoatRendererBuilder;
import de.ambertation.wover.item.api.render.ItemRenderTraits;

/**
 * Central registry of the ready-made client-only {@link de.ambertation.wover.block.api.trait.BlockTrait}/item-trait
 * builders. The model builder ({@link #MODEL}) is the client escape hatch that still stores a code lambda; the
 * render builders are now client-side aliases of the common builders in
 * {@link de.ambertation.wover.block.api.render.BlockRenderTraits}/{@link de.ambertation.wover.item.api.render.ItemRenderTraits}
 * (which produce common bindings applied at client init), kept here for source compatibility.
 */
public class ClientBlockTraits {
    /** Attaches a code-driven client model factory (escape hatch) to a block, see {@link BlockModelTrait}. */
    public static final BlockModelTrait.Builder MODEL = BlockModelTraitBuilder.BUILDER;
    /** Registers default boat/chest-boat rendering for a boat item (alias of {@code ItemRenderTraits.BOAT_RENDERER}). */
    public static final BoatRendererBuilder BOAT_RENDERER = ItemRenderTraits.BOAT_RENDERER;
    /** Marks a custom chest block for the wooden-chest render materials (alias of {@code BlockRenderTraits.CHEST_RENDERER}). */
    public static final ChestRendererBinding.Builder CHEST_RENDERER = BlockRenderTraits.CHEST_RENDERER;
    /** Selects the render layer (cutout/translucent) a block should use (alias of {@code BlockRenderTraits.RENDER_LAYER}). */
    public static final RenderLayerBuilder RENDER_LAYER = BlockRenderTraits.RENDER_LAYER;
    /** Gives the block a tint colour, in the world and optionally on its item (alias of {@code BlockRenderTraits.TINT}). */
    public static final TintBuilder TINT = BlockRenderTraits.TINT;
}
