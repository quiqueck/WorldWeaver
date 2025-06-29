package org.betterx.wover.item.api.trait;


import net.minecraft.world.item.Item;

import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public interface ItemWithTraits<I extends Item> {
    void wover_setItemTraits(@Nullable List<ItemTrait.RuntimeTrait<I, ?>> traits);

    @Nullable Collection<ItemTrait.RuntimeTrait<I, ?>> wover_itemTraits();
}
