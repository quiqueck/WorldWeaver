package org.betterx.wover.item.api.client.trait;

import org.betterx.wover.item.api.trait.ItemTrait;
import org.betterx.wover.item.api.trait.ItemTraitBuilder;

import net.minecraft.world.item.BoatItem;

/**
 * Trait for rendering boats or rafts, including the option to render them with a chest.
 * This trait registers a Boat-Item for default rendering.
 * <p>
 * You need to place your textures for the boat/raft in `/assets/namespace/textures/entity/boat/`
 * (or `/assets/namespace/textures/entity/chest_boat/`) - the same folders regardless of {@code isRaft}, matching
 * how vanilla's bamboo raft texture lives under `textures/entity/boat/bamboo.png`. The Texture should have the
 * same name as the Boat-Item.
 * <p>
 * Example:
 * for a Boat with the ResourceLocation `namespace:obsidian_boat`, you should place the textures in
 * `/assets/namespace/textures/entity/boat/obsidian_boat.png`.
 * for a ChestBoat with the ResourceLocation `namespace:obsidian_chest_boat`, you should place the textures in
 * `/assets/namespace/textures/entity/chest_boat/obsidian_chest_boat.png`.
 */
public interface BoatRendererTrait extends ItemTrait<BoatItem, BoatRendererTrait> {
    /**
     * Builder for {@link BoatRendererTrait} instances.
     */
    interface Builder extends ItemTraitBuilder<BoatItem, BoatRendererTrait> {
        /**
         * Creates the default-configured boat renderer trait (a plain boat, without a chest).
         *
         * @return The default boat renderer trait
         */
        BoatRendererTrait withDefault();

        /**
         * Creates a boat renderer trait configured for a plain or chest boat.
         *
         * @param withChest Whether the boat has a chest and should be rendered as a chest boat
         * @return The configured boat renderer trait
         */
        default BoatRendererTrait with(boolean withChest) {
            return with(withChest, false);
        }

        /**
         * Creates a boat/raft renderer trait configured for a plain or chest boat/raft.
         *
         * @param withChest Whether the boat/raft has a chest
         * @param isRaft    Whether to render as a raft ({@code RaftRenderer}/{@code RaftModel}) rather than a
         *                  boat ({@code BoatRenderer}/{@code BoatModel})
         * @return The configured boat/raft renderer trait
         */
        BoatRendererTrait with(boolean withChest, boolean isRaft);
    }

    /**
     * Whether this trait renders the boat with a chest.
     *
     * @return {@code true} if the boat should be rendered as a chest boat, {@code false} for a plain boat
     */
    boolean withChest();

    /**
     * @return {@code true} if this trait renders as a raft rather than a boat
     */
    boolean isRaft();
}
