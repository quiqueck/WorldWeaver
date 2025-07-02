package org.betterx.wover.item.api.client.trait;

import org.betterx.wover.item.api.trait.ItemTrait;
import org.betterx.wover.item.api.trait.ItemTraitBuilder;

import net.minecraft.world.item.BoatItem;

public interface BoatRendererTrait extends ItemTrait<BoatItem, BoatRendererTrait> {
    interface Builder extends ItemTraitBuilder<BoatItem, BoatRendererTrait> {
        BoatRendererTrait withDefault();
    }
}
