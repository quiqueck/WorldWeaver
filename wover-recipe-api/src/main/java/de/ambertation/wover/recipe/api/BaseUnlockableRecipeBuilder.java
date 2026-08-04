package de.ambertation.wover.recipe.api;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

/**
 * Adds advancement/recipe-book "unlock" behavior to a {@link BaseRecipeBuilder}.
 * <p>
 * By default, a recipe automatically unlocks once the player has one of the items used as its input
 * (a {@code has_<item>} criterion is derived and added for every material passed to the builder). The methods
 * on this interface let you add further custom unlock criteria, or disable the automatic advancement entirely.
 *
 * @param <I> The concrete builder type, used so that fluent methods declared here return the subtype instead
 *            of {@link BaseUnlockableRecipeBuilder}.
 */
public interface BaseUnlockableRecipeBuilder<I extends BaseRecipeBuilder<I>> {
    /**
     * Enables or disables generation of the recipe's unlock advancement.
     * <p>
     * When disabled, none of the criteria added via {@link #unlockedBy} or {@link #unlocks} are written, and the
     * recipe will only be discoverable if it is unlocked through some other means.
     *
     * @param shouldUnlockAdvancements {@code true} (the default) to generate the unlock advancement.
     * @return This builder, for chaining.
     */
    I shouldUnlockAdvancements(boolean shouldUnlockAdvancements);

    /**
     * Adds an unlock criterion that is satisfied once the player has the given item in their inventory.
     * A criterion name is derived automatically from the item.
     *
     * @param item The item that should unlock the recipe.
     * @return This builder, for chaining.
     */
    I unlockedBy(ItemLike item);

    /**
     * Adds an unlock criterion that is satisfied once the player has an item from the given tag in their
     * inventory. A criterion name is derived automatically from the tag.
     *
     * @param tag The tag that should unlock the recipe.
     * @return This builder, for chaining.
     */
    I unlockedBy(TagKey<Item> tag);

    /**
     * Adds an unlock criterion that is satisfied once the player has any one of the given items in their
     * inventory. A single criterion name is derived automatically from all passed items.
     *
     * @param items The items that should unlock the recipe.
     * @return This builder, for chaining.
     */
    I unlockedBy(ItemLike... items);

    /**
     * Adds an unlock criterion with an explicit name that is satisfied once the player has any one of the
     * given items in their inventory.
     *
     * @param name  The name for the unlock criterion.
     * @param items The items that should unlock the recipe.
     * @return This builder, for chaining.
     */
    I unlocks(String name, ItemLike... items);
}
