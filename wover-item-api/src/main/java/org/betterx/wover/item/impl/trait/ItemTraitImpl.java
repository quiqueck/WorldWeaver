package org.betterx.wover.item.impl.trait;

import org.betterx.wover.item.api.ItemDefinition;
import org.betterx.wover.item.api.trait.*;

import net.minecraft.world.item.Item;


public abstract class ItemTraitImpl<I extends Item, R extends RuntimeItemTrait<I, R>> implements ItemTrait<I, R> {
    protected ItemTraitImpl() {

    }

    @Override
    public R forRuntime() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj != null && getClass() == obj.getClass()) return true;
        if (obj instanceof RuntimeItemTrait<?, ?> rt) {
            return this.is(rt);
        }
        if (obj instanceof ItemTraitBuilder<?, ?> builder) {
            return this.is(builder);
        }
        if (obj instanceof ItemTraitKey otherID) {
            return this.is(otherID);
        }

        return super.equals(obj);
    }

    @Override
    public void configure(ItemDefinition<I, ? extends ItemDefinition<I, ?>> definition) {
        // Default implementation does nothing
    }

    @Override
    public void afterItemRegistration(
            I item,
            ItemDefinition<I, ? extends ItemDefinition<I, ?>> definition
    ) {
        // Default implementation does nothing
    }

    public abstract static class Generic extends ItemTraitImpl<Item, GenericItemTrait> implements GenericItemTrait {

    }

}
