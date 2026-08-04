package de.ambertation.wover.item.api.trait;


import net.minecraft.world.item.Item;

import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * Implemented by {@link Item} subclasses that want to carry {@link RuntimeItemTrait}s so they can be inspected
 * at runtime (e.g. via {@link ItemTrait#hasRuntimeTrait(Item, ItemTraitKey)}).
 *
 * <p>{@link de.ambertation.wover.item.api.ItemDefinition#build()} automatically calls
 * {@link #wover_setItemTraits(List)} with the runtime traits collected from every
 * {@link de.ambertation.wover.item.api.ItemDefinition#addTrait(ItemTrait) addTrait(...)} call whose
 * {@link ItemTrait#forRuntime()} returned a non-null value, provided the built item implements this interface.
 * The {@code wover_} prefix avoids clashing with vanilla/other mods' item method names.
 *
 * @param <I> The item type this instance carries traits for
 */
public interface ItemWithTraits<I extends Item> {
    /**
     * Sets the runtime traits carried by this item. Called automatically by
     * {@link de.ambertation.wover.item.api.ItemDefinition#build()}.
     *
     * @param traits The runtime traits to associate with this item, or {@code null}/empty for none
     */
    void wover_setItemTraits(@Nullable List<RuntimeItemTrait<I, ?>> traits);

    /**
     * Gets the runtime traits currently associated with this item.
     *
     * @return The runtime traits, or {@code null} if none were set
     */
    @Nullable Collection<RuntimeItemTrait<I, ?>> wover_itemTraits();
}
