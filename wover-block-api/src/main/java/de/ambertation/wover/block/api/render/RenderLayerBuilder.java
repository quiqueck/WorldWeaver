package de.ambertation.wover.block.api.render;

import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.core.api.ModCore;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Common builder for {@link RenderLayerBinding}. Mirrors the former client-only {@code RenderLayerTrait.Builder}
 * API ({@link #cutout()}/{@link #translucent()}/{@link #with(RenderLayer)}), but produces the common binding and
 * so can be referenced from common block-creation code. As before, the trait is only produced on the client
 * ({@code ModCore.isClient()}); on a dedicated server it returns {@code null}, so nothing is attached.
 */
public final class RenderLayerBuilder {
    /** Shared instance; also exposed on the client as {@code ClientBlockTraits.RENDER_LAYER}. */
    public static final RenderLayerBuilder BUILDER = new RenderLayerBuilder();

    private RenderLayerBuilder() {
    }

    /**
     * @param layer the render layer to select
     * @return the binding, or {@code null} on a dedicated server
     */
    public @Nullable BlockTrait<?, ?> with(@NotNull RenderLayer layer) {
        if (ModCore.isClient()) return new RenderLayerBinding(layer);
        return null;
    }

    /**
     * @return a binding selecting {@link RenderLayer#CUTOUT}, or {@code null} on a dedicated server
     */
    public @Nullable BlockTrait<?, ?> cutout() {
        return with(RenderLayer.CUTOUT);
    }

    /**
     * @return a binding selecting {@link RenderLayer#TRANSLUCENT}, or {@code null} on a dedicated server
     */
    public @Nullable BlockTrait<?, ?> translucent() {
        return with(RenderLayer.TRANSLUCENT);
    }
}
