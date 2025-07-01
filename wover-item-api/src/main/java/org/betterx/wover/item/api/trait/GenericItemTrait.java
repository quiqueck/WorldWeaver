package org.betterx.wover.item.api.trait;

import net.minecraft.world.item.Item;

public interface GenericItemTrait extends ItemTrait<Item, GenericItemTrait> {
    interface BuilderWithDefaults extends ItemTraitBuilder.WithDefaults<Item, GenericItemTrait> {
    }

    interface BuilderWithDefault extends ItemTraitBuilder.WithDefault<Item, GenericItemTrait> {
    }

    interface Builder extends ItemTraitBuilder<Item, GenericItemTrait> {
    }
}

