package org.betterx.wover.item.api.trait;

import net.minecraft.world.item.Item;

import org.jetbrains.annotations.Nullable;

public interface RuntimeItemTrait<I extends Item, R extends RuntimeItemTrait<I, R>> {
    ItemTraitKey key();

    default boolean is(@Nullable ItemTraitKey traitID) {
        if (traitID == null) return false;
        return traitID.equals(this.key());
    }

    default boolean is(@Nullable RuntimeItemTrait<?, ?> trait) {
        if (trait == null) return false;
        return this.is(trait.key());
    }

    default boolean is(@Nullable ItemTraitBuilder<?, ?> traitBuilder) {
        if (traitBuilder == null) return false;
        return this.is(traitBuilder.key());
    }
}
