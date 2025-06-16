package org.betterx.wover.item.api;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumables;

public class DrinkItemConfig<I extends Item> extends FoodItemConfig<I> {
    protected DrinkItemConfig(
            ItemRegistry registry,
            String name,
            ItemConfig.ItemFactory<I, FoodItemConfig<I>> itemFactory
    ) {
        super(registry, name, itemFactory, Consumables.defaultDrink());
    }
}
