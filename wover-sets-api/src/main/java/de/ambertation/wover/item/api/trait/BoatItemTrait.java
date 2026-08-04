package de.ambertation.wover.item.api.trait;

import net.minecraft.world.item.BoatItem;

import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link ItemTrait} marking an item as a (chest) boat or raft, adding the vanilla {@code minecraft:boats}/
 * {@code minecraft:chest_boats} item tag (rafts are tagged as boats too, matching vanilla's bamboo raft). On the
 * client, the matching builder also bundles a boat/raft renderer trait, see {@code ItemTraits#BOAT_ITEM}.
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
        default @Nullable List<ItemTrait<?, ?>> with(boolean withChest) {
            return with(withChest, false);
        }

        /**
         * @param withChest whether the boat/raft has an attached chest
         * @param isRaft    whether this is a raft (rendered with {@code RaftRenderer}) rather than a boat
         *                  (rendered with {@code BoatRenderer})
         * @return the combined trait list, or {@code null} if the trait was already added
         */
        @Nullable List<ItemTrait<?, ?>> with(boolean withChest, boolean isRaft);
    }

    /**
     * @return whether this boat has an attached chest
     */
    boolean withChest();

    /**
     * @return whether this is a raft rather than a boat
     */
    boolean isRaft();
}
