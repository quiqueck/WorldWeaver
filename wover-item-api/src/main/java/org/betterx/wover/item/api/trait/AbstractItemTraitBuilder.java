package org.betterx.wover.item.api.trait;

import net.minecraft.world.item.Item;

import java.util.List;

public abstract class AbstractItemTraitBuilder<I extends Item, R extends RuntimeItemTrait<I, R>> implements ItemTraitBuilder<I, R> {
    public final ItemTraitKey traitKey;

    protected AbstractItemTraitBuilder(ItemTraitKey traitKey) {
        this.traitKey = traitKey;
    }

    public ItemTraitKey key() {
        return traitKey;
    }

    public List<R> getRuntimeTraits(I item) {
        return ItemTrait.getRuntimeTraits(item, traitKey);
    }

    public abstract static class Generic extends AbstractItemTraitBuilder<Item, GenericItemTrait> implements GenericItemTrait.Builder {
        public Generic(ItemTraitKey traitKey) {
            super(traitKey);
        }
    }
}
