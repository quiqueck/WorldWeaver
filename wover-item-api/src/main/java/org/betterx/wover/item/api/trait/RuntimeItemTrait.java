package org.betterx.wover.item.api.trait;

import net.minecraft.world.item.Item;

import org.jetbrains.annotations.Nullable;

public class RuntimeItemTrait<I extends Item, R extends RuntimeItemTrait<I, R>> {
    private final ItemTraitKey traitID;


    @SuppressWarnings("unchecked")
    protected RuntimeItemTrait(ItemTraitKey traitID) {
        this.traitID = traitID;
    }

    public boolean is(@Nullable ItemTraitKey traitID) {
        if (traitID == null) return false;
        return this.traitID.equals(traitID);
    }

    public boolean is(@Nullable ItemTrait<?, ?> trait) {
        if (trait == null) return false;
        return this.is(trait.traitID);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj != null && getClass() == obj.getClass()) return true;
        if (obj instanceof ItemTraitKey otherID) {
            return this.is(otherID);
        }
        if (obj instanceof ItemTrait<?, ?> trait) {
            return this.is(trait);
        }

        return super.equals(obj);
    }
}
