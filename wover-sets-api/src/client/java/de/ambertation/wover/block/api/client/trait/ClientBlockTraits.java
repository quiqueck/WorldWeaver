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
    /**
     * Attaches a code-driven client model factory (escape hatch) to a block, see {@link BlockModelTrait}.
     *
     * @deprecated Attach a {@link de.ambertation.wover.block.api.model.BlockModelBinding} instead: declare a
     * {@link de.ambertation.wover.block.api.model.ModelKey} with a common payload in shared code (or use a shape
     * from {@link de.ambertation.wover.block.api.model.ModelTraitLibrary}) and register its
     * {@link de.ambertation.wover.block.api.client.model.ClientModelFactory} in
     * {@link de.ambertation.wover.block.api.client.model.ClientBlockModelRegistry} from a
     * {@code wover.client.traits} entrypoint. This lambda-based trait has to be built in shared block
     * registration code, so it only stays off a dedicated server as long as every call sits behind a
     * {@code ModCore.isDatagen()} check - the binding needs no such guard.
     */
    @Deprecated(forRemoval = true)
    public static final BlockModelTrait.Builder MODEL = BlockModelTraitBuilder.BUILDER;
    /**
     * Registers default boat/chest-boat rendering for a boat item (alias of {@code ItemRenderTraits.BOAT_RENDERER}).
     *
     * @deprecated Use {@link de.ambertation.wover.item.api.render.ItemRenderTraits#BOAT_RENDERER}.
     * This class is client-only, so reading the alias from common code crashes a dedicated server.
     */
    @Deprecated(forRemoval = true)
    public static final BoatRendererBuilder BOAT_RENDERER = ItemRenderTraits.BOAT_RENDERER;
    /**
     * Marks a custom chest block for the wooden-chest render materials (alias of {@code BlockRenderTraits.CHEST_RENDERER}).
     *
     * @deprecated Use {@link de.ambertation.wover.block.api.render.BlockRenderTraits#CHEST_RENDERER}.
     * This class is client-only, so reading the alias from common code crashes a dedicated server.
     */
    @Deprecated(forRemoval = true)
    public static final ChestRendererBinding.Builder CHEST_RENDERER = BlockRenderTraits.CHEST_RENDERER;
    /**
     * Selects the render layer (cutout/translucent) a block should use (alias of {@code BlockRenderTraits.RENDER_LAYER}).
     *
     * @deprecated Use {@link de.ambertation.wover.block.api.render.BlockRenderTraits#RENDER_LAYER}.
     * This class is client-only, so reading the alias from common code crashes a dedicated server.
     */
    @Deprecated(forRemoval = true)
    public static final RenderLayerBuilder RENDER_LAYER = BlockRenderTraits.RENDER_LAYER;
    /**
     * Gives the block a tint colour, in the world and optionally on its item (alias of {@code BlockRenderTraits.TINT}).
     *
     * @deprecated Use {@link de.ambertation.wover.block.api.render.BlockRenderTraits#TINT}.
     * This class is client-only, so reading the alias from common code crashes a dedicated server.
     */
    @Deprecated(forRemoval = true)
    public static final TintBuilder TINT = BlockRenderTraits.TINT;
}
