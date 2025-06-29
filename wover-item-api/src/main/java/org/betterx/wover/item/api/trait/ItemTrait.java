package org.betterx.wover.item.api.trait;

import org.betterx.wover.entrypoint.LibWoverEvents;
import org.betterx.wover.item.api.ItemDefinition;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public abstract class ItemTrait<I extends Item, R extends RuntimeItemTrait<I, R>> {
    public static final VoidRuntime VOID_RUNTIME = new VoidRuntime();

    public abstract static class TraitBuilder {
        public final ItemTraitKey ID;

        protected TraitBuilder(ItemTraitKey id) {
            this.ID = id;
        }

        public <I extends Item, R extends RuntimeItemTrait<I, R>> List<R> getRuntimeTraits(I item) {
            return ItemTrait.getRuntimeTraits(item, ID);
        }
    }

    public static final class VoidRuntime extends RuntimeItemTrait<Item, VoidRuntime> {
        public static final ItemTraitKey ID = ItemTraitKey.of(LibWoverEvents.C, "void_trait");

        private VoidRuntime() {
            super(ID);
        }
    }

    @SuppressWarnings("unchecked")
    public static <I extends Item> @Nullable ItemWithTraits<I> asItemWithTraits(
            @Nullable I item
    ) {
        if (item instanceof ItemWithTraits<?> itemWithTraits) {
            return (ItemWithTraits<I>) itemWithTraits;
        }
        return null;
    }

    public static <I extends Item> @NotNull ItemWithTraits<I> asItemWithTraitsOrThrow(
            @Nullable I item
    ) {
        ItemWithTraits<I> itemWithTraits = asItemWithTraits(item);
        if (itemWithTraits == null) {
            throw new IllegalArgumentException("Item does not implement ItemWithTraits: " + item);
        }
        return itemWithTraits;
    }

    public static @NotNull Stream<? extends RuntimeItemTrait<?, ?>> runtimeTraits(
            @Nullable Item item
    ) {
        ItemWithTraits<?> itemWithTraits = asItemWithTraits(item);
        if (itemWithTraits == null) return Stream.empty();
        ;
        final var traits = itemWithTraits.wover_itemTraits();
        if (traits == null || traits.isEmpty()) {
            return Stream.empty();
        }
        return traits.stream();
    }

    public static boolean hasRuntimeTraits(Item item) {
        var itemWithTraits = asItemWithTraits(item);
        if (itemWithTraits == null) return false;
        final var traits = itemWithTraits.wover_itemTraits();
        return traits != null && !traits.isEmpty();
    }

    public static boolean hasRuntimeTrait(@Nullable Item item, ItemTraitKey traitKey) {
        var itemWithTraits = asItemWithTraits(item);
        if (itemWithTraits == null) return false;

        final var traits = itemWithTraits.wover_itemTraits();
        return traits != null && traits.stream().anyMatch(t -> t.is(traitKey));
    }

    public static <I extends Item, R extends RuntimeItemTrait<I, R>> @Nullable List<R> getRuntimeTraits(
            I item,
            ItemTraitKey traitKey
    ) {
        var itemWithTraits = asItemWithTraits(item);
        if (itemWithTraits == null) return null;
        List<R> result = new ArrayList<>();
        final var traits = itemWithTraits.wover_itemTraits();
        if (traits != null) {
            for (RuntimeItemTrait<I, ?> trait : traits) {
                if (trait != null && trait.is(traitKey)) {
                    result.add((R) trait);
                }
            }
        }

        return result;
    }


    public final ItemTraitKey traitID;

    protected ItemTrait(ItemTraitKey id) {
        this.traitID = id;
    }

    public boolean datagenOnly() {
        return this.clientOnly();
    }

    public boolean clientOnly() {
        return false;
    }

    public R forRuntime() {
        return null;
    }

    public boolean is(@Nullable RuntimeItemTrait<?, ?> trait) {
        if (trait == null) return false;
        return trait.is(this);
    }

    public boolean is(@Nullable ItemTraitKey traitID) {
        if (traitID == null) return false;
        return traitID.equals(this.traitID);
    }

    public boolean is(@Nullable ItemTrait<?, ?> trait) {
        if (trait == null) return false;
        return this.is(trait.traitID);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj != null && getClass() == obj.getClass()) return true;
        if (obj instanceof RuntimeItemTrait<?, ?> rt) {
            return this.is(rt);
        }
        if (obj instanceof ItemTrait<?, ?> rt) {
            return this.is(rt);
        }
        if (obj instanceof ItemTraitKey otherID) {
            return this.is(otherID);
        }

        return super.equals(obj);
    }

    public void configure(ItemDefinition<I, ? extends ItemDefinition<I, ?>> definition) {
        // Default implementation does nothing
    }

    public void afterItemRegistration(
            I item,
            ItemDefinition<I, ? extends ItemDefinition<I, ?>> definition
    ) {
        // Default implementation does nothing
    }

}
