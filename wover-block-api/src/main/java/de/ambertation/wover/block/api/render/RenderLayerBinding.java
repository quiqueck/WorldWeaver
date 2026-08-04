package de.ambertation.wover.block.api.render;

import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.entrypoint.LibWoverBlock;

import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

/**
 * Common, client-free runtime trait selecting the non-solid render layer a block should use. It stores only the
 * common-safe {@link RenderLayer} enum; the client source set walks blocks carrying this binding at client init
 * and performs the real {@code BlockRenderLayerMap.putBlock(...)} registration (see the {@code ClientRenderBootstrap}
 * applier). This replaces the former client-only {@code RenderLayerTrait}.
 */
public final class RenderLayerBinding extends BlockTraitImpl<Block, RenderLayerBinding> implements BlockTrait<Block, RenderLayerBinding> {
    /** The trait key every render-layer binding is attached under. */
    public static final BlockTraitKey RENDER_LAYER_KEY = BlockTraitKey.ofUnique(LibWoverBlock.C, "render_layer");

    private final @NotNull RenderLayer layer;

    RenderLayerBinding(@NotNull RenderLayer layer) {
        this.layer = layer;
    }

    /**
     * @return the render layer selected by this binding
     */
    public @NotNull RenderLayer layer() {
        return layer;
    }

    @Override
    public BlockTraitKey key() {
        return RENDER_LAYER_KEY;
    }

    @Override
    public RenderLayerBinding forRuntime() {
        return this;
    }

    @Override
    public boolean keepLatestOnly() {
        return true;
    }
}
