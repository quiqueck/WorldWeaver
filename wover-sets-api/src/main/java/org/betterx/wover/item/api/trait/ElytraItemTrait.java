package org.betterx.wover.item.api.trait;

import net.minecraft.world.item.Item;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public interface ElytraItemTrait extends ItemTrait<Item, ElytraItemTrait> {
    interface Builder extends ItemTraitBuilder.WithDefaults<Item, ElytraItemTrait> {
        @Nullable List<ItemTrait<?, ?>> with(Item repairedWith);
    }

    Item repairedWith();
}
