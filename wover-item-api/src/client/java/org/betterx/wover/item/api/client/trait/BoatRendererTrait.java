package org.betterx.wover.item.api.client.trait;

import org.betterx.wover.item.api.trait.ItemTrait;
import org.betterx.wover.item.api.trait.ItemTraitBuilder;

import net.minecraft.world.item.BoatItem;

/**
 * Trait for rendering boats, including the option to render them with a chest.
 * This trait registers a Boat-Item for default rendering.
 * <p>
 * You need to place your textures for the boat in `/assets/namespace/textures/entity/boat/`
 * (or `/assets/namespace/textures/entity/chest_boat/`). The Texture should have the same name as the Boat-Item.
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
        BoatRendererTrait with(boolean withChest);
    }

    /**
     * Whether this trait renders the boat with a chest.
     *
     * @return {@code true} if the boat should be rendered as a chest boat, {@code false} for a plain boat
     */
    boolean withChest();
}
