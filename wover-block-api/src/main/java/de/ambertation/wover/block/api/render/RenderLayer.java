package de.ambertation.wover.block.api.render;

/**
 * The non-solid render layers a block can request through {@link RenderLayerBinding}. Common-safe (no client
 * types); the client source set maps these to the matching {@code net.minecraft.client...ChunkSectionLayer} when
 * it wires up {@code BlockRenderLayerMap} at client init.
 */
public enum RenderLayer {
    /** {@code RenderType.cutout()} - fully opaque or fully transparent pixels, no blending. */
    CUTOUT,
    /** {@code RenderType.translucent()} - alpha-blended, partially transparent pixels. */
    TRANSLUCENT
}
