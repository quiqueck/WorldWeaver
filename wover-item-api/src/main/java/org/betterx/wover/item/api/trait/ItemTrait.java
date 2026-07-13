package org.betterx.wover.item.api.trait;

import org.betterx.wover.item.api.ItemDefinition;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemTrait<I extends Item, R extends RuntimeItemTrait<I, R>> extends RuntimeItemTrait<I, R> {
    @SuppressWarnings("unchecked")
    static <I extends Item> @Nullable ItemWithTraits<I> asItemWithTraits(
            @Nullable I item
    ) {
        if (item instanceof ItemWithTraits<?> itemWithTraits) {
            return (ItemWithTraits<I>) itemWithTraits;
        }
        return null;
    }

    static <I extends Item> @NotNull ItemWithTraits<I> asItemWithTraitsOrThrow(
            @Nullable I item
    ) {
        ItemWithTraits<I> itemWithTraits = asItemWithTraits(item);
        if (itemWithTraits == null) {
            throw new IllegalArgumentException("Item does not implement ItemWithTraits: " + item);
        }
        return itemWithTraits;
    }

    static @NotNull Stream<? extends RuntimeItemTrait<?, ?>> runtimeTraits(
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

    static boolean hasRuntimeTraits(Item item) {
        var itemWithTraits = asItemWithTraits(item);
        if (itemWithTraits == null) return false;
        final var traits = itemWithTraits.wover_itemTraits();
        return traits != null && !traits.isEmpty();
    }

    static boolean hasRuntimeTrait(@Nullable Item item, ItemTraitKey traitKey) {
        var itemWithTraits = asItemWithTraits(item);
        if (itemWithTraits == null) return false;

        final var traits = itemWithTraits.wover_itemTraits();
        return traits != null && traits.stream().anyMatch(t -> t.is(traitKey));
    }

    static <I extends Item, R extends RuntimeItemTrait<I, R>> @Nullable List<R> getRuntimeTraits(
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

        return result.isEmpty() ? null : result;
    }

    R forRuntime();
    void configure(ItemDefinition<I, ? extends ItemDefinition<I, ?>> definition);
    void afterItemRegistration(
            I item,
            ItemDefinition<I, ? extends ItemDefinition<I, ?>> definition
    );
}
