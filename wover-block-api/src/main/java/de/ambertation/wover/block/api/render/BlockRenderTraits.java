package de.ambertation.wover.block.api.render;

/**
 * Common catalogue of the ready-made client-render block traits (render layer, chest renderer). Unlike the
 * former {@code ClientBlockTraits}, these produce common-safe bindings, so they can be attached from common
 * block-creation code; the client source set performs the real Fabric registration at client init. The
 * {@code ClientBlockTraits} constants remain as client-side aliases of these for source compatibility.
 */
public class BlockRenderTraits {
    /** Selects the render layer (cutout/translucent) a block should use, see {@link RenderLayerBinding}. */
    public static final RenderLayerBuilder RENDER_LAYER = RenderLayerBuilder.BUILDER;
    /** Marks a custom chest block for the wooden-chest render materials, see {@link ChestRendererBinding}. */
    public static final ChestRendererBinding.Builder CHEST_RENDERER = new ChestRendererBinding.Builder();

    private BlockRenderTraits() {
    }
}
