package de.ambertation.wover.item.api.render;

/**
 * Common catalogue of ready-made client-render item traits (boat renderer). Produces common-safe bindings; the
 * client source set performs the real renderer registration at client init. {@code ClientBlockTraits.BOAT_RENDERER}
 * remains as a client-side alias for source compatibility.
 */
public class ItemRenderTraits {
    /** Registers default boat/chest-boat rendering for a boat item, see {@link BoatRendererBinding}. */
    public static final BoatRendererBuilder BOAT_RENDERER = BoatRendererBuilder.BUILDER;

    private ItemRenderTraits() {
    }
}
