package org.betterx.wover.item.api.trait;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

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

    // Static interface
    private static final List<ItemTrait<?, ?>> EMPTY = List.of();

    protected static @NotNull List<ItemTrait<?, ?>> combine(ItemTrait<?, ?> t0) {
        if (t0 == null) return EMPTY;
        return List.of(t0);
    }

    protected static @NotNull List<ItemTrait<?, ?>> combine(ItemTrait<?, ?> t0, ItemTrait<?, ?> t1) {
        if (t0 == null && t1 == null) return EMPTY;
        if (t0 == null) return List.of(t1);
        if (t1 == null) return List.of(t0);
        return List.of(t0, t1);
    }

    protected static @NotNull List<ItemTrait<?, ?>> combine(
            ItemTrait<?, ?> t0,
            ItemTrait<?, ?> t1,
            ItemTrait<?, ?> t2
    ) {
        if (t0 == null && t1 == null && t2 == null) return EMPTY;
        if (t0 == null && t1 == null) return List.of(t2);
        if (t0 == null && t2 == null) return List.of(t1);
        if (t1 == null && t2 == null) return List.of(t0);
        if (t0 == null) return combine(t1, t2);
        if (t1 == null) return combine(t0, t2);
        if (t2 == null) return combine(t0, t1);
        return List.of(t0, t1, t2);
    }

    protected static @NotNull List<ItemTrait<?, ?>> combine(ItemTrait<?, ?>... traits) {
        if (traits == null || traits.length == 0) return EMPTY;
        List<ItemTrait<?, ?>> result = new ArrayList<>();
        for (ItemTrait<?, ?> trait : traits) {
            if (trait != null) {
                result.add(trait);
            }
        }
        return result.isEmpty() ? EMPTY : result;
    }

    public abstract static class Generic extends AbstractItemTraitBuilder<Item, GenericItemTrait> implements GenericItemTrait.Builder {
        public Generic(ItemTraitKey traitKey) {
            super(traitKey);
        }
    }
}
