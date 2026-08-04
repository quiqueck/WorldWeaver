package de.ambertation.wover.item.api.render;

import de.ambertation.wover.entrypoint.LibWoverItem;
import de.ambertation.wover.item.api.trait.ItemTrait;
import de.ambertation.wover.item.api.trait.ItemTraitKey;
import de.ambertation.wover.item.impl.trait.ItemTraitImpl;

import net.minecraft.world.item.Item;

/**
 * Common, client-free trait requesting default boat/raft rendering for a boat item. It stores only the
 * common-safe {@code withChest}/{@code isRaft} booleans; the client source set walks boat items carrying this
 * binding at client init and performs the real {@code EntityModelLayerRegistry}/{@code EntityRendererRegistry}
 * registration - the same body the former {@code BoatRendererTrait.afterItemRegistration(...)} ran. This
 * replaces the client-only {@code BoatRendererTrait}.
 */
public final class BoatRendererBinding extends ItemTraitImpl<Item, BoatRendererBinding> implements ItemTrait<Item, BoatRendererBinding> {
    /** The trait key every boat-renderer binding is attached under. */
    public static final ItemTraitKey BOAT_RENDERER_KEY = ItemTraitKey.ofUnique(LibWoverItem.C, "boat_model");

    private final boolean withChest;
    private final boolean isRaft;

    BoatRendererBinding(boolean withChest, boolean isRaft) {
        this.withChest = withChest;
        this.isRaft = isRaft;
    }

    /**
     * @return whether this boat renders with a chest
     */
    public boolean withChest() {
        return withChest;
    }

    /**
     * @return whether this renders as a raft rather than a boat
     */
    public boolean isRaft() {
        return isRaft;
    }

    @Override
    public ItemTraitKey key() {
        return BOAT_RENDERER_KEY;
    }

    @Override
    public BoatRendererBinding forRuntime() {
        return this;
    }
}
