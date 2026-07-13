package org.betterx.wover.item.api.trait;

import net.minecraft.world.item.Item;

import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link ItemTrait} marking an item as a custom elytra, recording the item it is repaired with.
 */
public interface ElytraItemTrait extends ItemTrait<Item, ElytraItemTrait> {
    /**
     * Builds {@link ElytraItemTrait} instances.
     */
    interface Builder extends ItemTraitBuilder.WithDefaults<Item, ElytraItemTrait> {
        /**
         * @param repairedWith the item used to repair this elytra (e.g. in an anvil)
         * @return the combined trait list, or {@code null} if the trait was already added
         */
        @Nullable List<ItemTrait<?, ?>> with(Item repairedWith);
    }

    /**
     * @return the item used to repair this elytra
     */
    Item repairedWith();
}
