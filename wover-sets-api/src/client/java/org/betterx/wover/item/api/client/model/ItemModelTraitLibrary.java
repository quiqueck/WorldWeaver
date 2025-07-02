package org.betterx.wover.item.api.client.model;

import org.betterx.wover.item.api.client.trait.ClientItemTraits;
import org.betterx.wover.item.api.client.trait.ItemModelTrait;

import net.minecraft.client.data.models.model.ModelTemplates;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ItemModelTraitLibrary {
    public static ItemModelTrait boat() {
        return ClientItemTraits.MODEL.with((key, item, generator) -> {
            generator.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
        });
    }

    public static ItemModelTrait chestBoat() {
        return boat();
    }
}
