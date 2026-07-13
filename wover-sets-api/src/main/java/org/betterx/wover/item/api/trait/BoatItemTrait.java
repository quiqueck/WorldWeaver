package org.betterx.wover.item.api.trait;

import net.minecraft.world.item.BoatItem;

import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link ItemTrait} marking an item as a (chest) boat, adding the vanilla {@code minecraft:boats}/
 * {@code minecraft:chest_boats} item tag. On the client, the matching builder also bundles a boat renderer
 * trait, see {@code ItemTraits#BOAT_ITEM}.
 */
public interface BoatItemTrait extends ItemTrait<BoatItem, BoatItemTrait> {
    /**
     * Builds {@link BoatItemTrait} instances.
     */
    interface Builder extends ItemTraitBuilder.WithDefaults<BoatItem, BoatItemTrait> {
        /**
         * @param withChest whether the boat has an attached chest
         * @return the combined trait list, or {@code null} if the trait was already added
         */
        @Nullable List<ItemTrait<?, ?>> with(boolean withChest);
    }

    /**
     * @return whether this boat has an attached chest
     */
    boolean withChest();
}
