package de.ambertation.wover.item.api.render;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.item.api.trait.ItemTrait;

import org.jetbrains.annotations.Nullable;

/**
 * Common builder for {@link BoatRendererBinding}, mirroring the former client-only
 * {@code BoatRendererTrait.Builder} API. Produces the common binding (so common item-creation code can attach
 * it); only on the client ({@code ModCore.isClient()}), matching the old behaviour.
 */
public final class BoatRendererBuilder {
    /** Shared instance; also exposed on the client as {@code ClientBlockTraits.BOAT_RENDERER}. */
    public static final BoatRendererBuilder BUILDER = new BoatRendererBuilder();

    private BoatRendererBuilder() {
    }

    /**
     * Creates the default-configured (plain boat, no chest) binding.
     *
     * @return the binding, or {@code null} on a dedicated server
     */
    public @Nullable ItemTrait<?, ?> withDefault() {
        return with(false, false);
    }

    /**
     * @param withChest whether the boat has a chest
     * @return the binding, or {@code null} on a dedicated server
     */
    public @Nullable ItemTrait<?, ?> with(boolean withChest) {
        return with(withChest, false);
    }

    /**
     * @param withChest whether the boat/raft has a chest
     * @param isRaft    whether to render as a raft rather than a boat
     * @return the binding, or {@code null} on a dedicated server
     */
    public @Nullable ItemTrait<?, ?> with(boolean withChest, boolean isRaft) {
        if (ModCore.isClient()) return new BoatRendererBinding(withChest, isRaft);
        return null;
    }
}
