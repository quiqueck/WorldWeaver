package org.betterx.wover.item.api.trait;

import org.betterx.wover.item.api.ItemDefinition;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A build-time (datagen/registration-time) configuration object that customizes an {@link ItemDefinition} and,
 * optionally, contributes a {@link RuntimeItemTrait} that stays attached to the built item.
 *
 * <p>Traits are added to an item definition through
 * {@link ItemDefinition#addTrait(ItemTrait) ItemDefinition.addTrait(...)}. When the item is built,
 * {@link #configure(ItemDefinition)} is called for every added trait so it can modify the definition (add tags,
 * properties, etc.), and if {@link #forRuntime()} returns a non-null value, that runtime trait is attached to the
 * built item (provided it implements {@link ItemWithTraits}) so it can be queried later via the static helpers on
 * this interface (e.g. {@link #hasRuntimeTrait(Item, ItemTraitKey)}). After registration,
 * {@link #afterItemRegistration(Item, ItemDefinition)} is called for any final setup that needs the registered item.
 *
 * @param <I> The item type this trait applies to
 * @param <R> The runtime trait type this trait contributes, see {@link #forRuntime()}
 * @see AbstractItemTraitBuilder
 * @see ItemTraitLookup
 */
public interface ItemTrait<I extends Item, R extends RuntimeItemTrait<I, R>> extends RuntimeItemTrait<I, R> {
    /**
     * Casts the given item to {@link ItemWithTraits} if it implements that interface.
     *
     * @param item The item to check, may be {@code null}
     * @param <I>  The item type
     * @return The item as {@link ItemWithTraits}, or {@code null} if it does not implement the interface
     */
    @SuppressWarnings("unchecked")
    static <I extends Item> @Nullable ItemWithTraits<I> asItemWithTraits(
            @Nullable I item
    ) {
        if (item instanceof ItemWithTraits<?> itemWithTraits) {
            return (ItemWithTraits<I>) itemWithTraits;
        }
        return null;
    }

    /**
     * Casts the given item to {@link ItemWithTraits}, throwing if it does not implement that interface.
     *
     * @param item The item to check
     * @param <I>  The item type
     * @return The item as {@link ItemWithTraits}
     * @throws IllegalArgumentException if the item does not implement {@link ItemWithTraits}
     */
    static <I extends Item> @NotNull ItemWithTraits<I> asItemWithTraitsOrThrow(
            @Nullable I item
    ) {
        ItemWithTraits<I> itemWithTraits = asItemWithTraits(item);
        if (itemWithTraits == null) {
            throw new IllegalArgumentException("Item does not implement ItemWithTraits: " + item);
        }
        return itemWithTraits;
    }

    /**
     * Gets a stream of all runtime traits attached to the given item.
     *
     * @param item The item to inspect, may be {@code null}
     * @return A stream of the item's runtime traits, or an empty stream if the item has none or does not
     * implement {@link ItemWithTraits}
     */
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

    /**
     * Checks whether the given item carries any runtime traits at all.
     *
     * @param item The item to inspect
     * @return {@code true} if the item implements {@link ItemWithTraits} and has at least one runtime trait
     */
    static boolean hasRuntimeTraits(Item item) {
        var itemWithTraits = asItemWithTraits(item);
        if (itemWithTraits == null) return false;
        final var traits = itemWithTraits.wover_itemTraits();
        return traits != null && !traits.isEmpty();
    }

    /**
     * Checks whether the given item carries a runtime trait with the given key.
     *
     * @param item     The item to inspect, may be {@code null}
     * @param traitKey The trait key to look for
     * @return {@code true} if the item has a runtime trait matching the given key, {@code false} otherwise
     */
    static boolean hasRuntimeTrait(@Nullable Item item, ItemTraitKey traitKey) {
        var itemWithTraits = asItemWithTraits(item);
        if (itemWithTraits == null) return false;

        final var traits = itemWithTraits.wover_itemTraits();
        return traits != null && traits.stream().anyMatch(t -> t.is(traitKey));
    }

    /**
     * Gets every runtime trait on the given item that matches the given trait key.
     *
     * @param item     The item to inspect
     * @param traitKey The trait key to look for
     * @param <I>      The item type
     * @param <R>      The runtime trait type expected for the given key
     * @return The matching runtime traits, or {@code null} if the item has none matching the key
     */
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

    /**
     * Creates the runtime counterpart of this trait, to be attached to the built item.
     *
     * @return The runtime trait to attach to the built item, or {@code null} if this trait has no runtime
     * component (e.g. it only affects the item definition at build time)
     */
    R forRuntime();

    /**
     * Applies this trait's configuration to the item definition, e.g. adding tags or properties.
     * Called automatically by {@link ItemDefinition#build()} for every trait added via
     * {@link ItemDefinition#addTrait(ItemTrait) addTrait(...)}.
     *
     * @param definition The item definition to configure
     */
    void configure(ItemDefinition<I, ? extends ItemDefinition<I, ?>> definition);

    /**
     * Called after the item has been built and registered, for any final setup that requires the registered item
     * instance (e.g. registering dispenser behavior).
     *
     * @param item       The registered item instance
     * @param definition The item definition that produced the item
     */
    void afterItemRegistration(
            I item,
            ItemDefinition<I, ? extends ItemDefinition<I, ?>> definition
    );
}
