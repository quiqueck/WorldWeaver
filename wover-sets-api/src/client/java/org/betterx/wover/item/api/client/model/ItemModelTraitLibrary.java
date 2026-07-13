package org.betterx.wover.item.api.client.model;

import org.betterx.wover.item.api.client.trait.ClientItemTraits;
import org.betterx.wover.item.api.client.trait.ItemModelTrait;

import net.minecraft.client.data.models.model.ModelTemplates;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Ready-made {@link ItemModelTrait} factories for boat items, matching vanilla's flat boat/chest-boat item
 * icons. Used by {@code org.betterx.wover.sets.api.blocks.types.Boat}.
 */
@Environment(EnvType.CLIENT)
public class ItemModelTraitLibrary {
    /**
     * @return a trait generating a plain flat item model for a boat
     */
    public static ItemModelTrait boat() {
        return ClientItemTraits.MODEL.with((key, item, generator) -> {
            generator.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
        });
    }

    /**
     * @return a trait generating a plain flat item model for a chest boat (identical to {@link #boat()})
     */
    public static ItemModelTrait chestBoat() {
        return boat();
    }
}
