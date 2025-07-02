package org.betterx.wover.item.api.trait;

import net.minecraft.world.item.BoatItem;

public interface BoatItemTrait extends ItemTrait<BoatItem, BoatItemTrait> {
    interface Builder extends ItemTraitBuilder.WithDefaults<BoatItem, BoatItemTrait> {

    }
}
