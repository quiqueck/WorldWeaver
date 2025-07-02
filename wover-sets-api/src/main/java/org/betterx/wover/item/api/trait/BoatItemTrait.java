package org.betterx.wover.item.api.trait;

import net.minecraft.world.item.BoatItem;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public interface BoatItemTrait extends ItemTrait<BoatItem, BoatItemTrait> {
    interface Builder extends ItemTraitBuilder.WithDefaults<BoatItem, BoatItemTrait> {
        @Nullable List<ItemTrait<?, ?>> with(boolean withChest);
    }

    boolean withChest();
}
