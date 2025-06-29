package org.betterx.wover.item.mixin;

import org.betterx.wover.item.api.trait.ItemWithTraits;
import org.betterx.wover.item.api.trait.RuntimeItemTrait;

import net.minecraft.world.item.Item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.Nullable;

@Mixin(Item.class)
public class ItemMixin<I extends Item> implements ItemWithTraits<I> {
    @Unique
    private @Nullable List<RuntimeItemTrait<I, ?>> wover_itemTraits;


    @Override
    public void wover_setItemTraits(@Nullable List<RuntimeItemTrait<I, ?>> runtimeTraits) {
        this.wover_itemTraits = runtimeTraits;
    }

    @Unique
    public @Nullable Collection<RuntimeItemTrait<I, ?>> wover_itemTraits() {
        return wover_itemTraits;
    }
}
