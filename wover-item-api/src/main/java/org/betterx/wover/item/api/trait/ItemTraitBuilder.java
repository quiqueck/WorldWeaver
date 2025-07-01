package org.betterx.wover.item.api.trait;

import net.minecraft.world.item.Item;

import java.util.List;

public interface ItemTraitBuilder<I extends Item, R extends RuntimeItemTrait<I, R>> {
    interface WithDefault<I extends Item, R extends RuntimeItemTrait<I, R>> extends ItemTraitBuilder<I, R> {
        ItemTrait<?, ?> withDefault();
    }

    interface WithDefaults<I extends Item, R extends RuntimeItemTrait<I, R>> extends ItemTraitBuilder<I, R> {
        List<ItemTrait<?, ?>> withDefault();
    }

    ItemTraitKey key();
    List<R> getRuntimeTraits(I item);
}
