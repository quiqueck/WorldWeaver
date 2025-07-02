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
    interface Builder extends ItemTraitBuilder<BoatItem, BoatRendererTrait> {
        BoatRendererTrait withDefault();
        BoatRendererTrait with(boolean withChest);
    }

    boolean withChest();
}
