package org.betterx.wover.recipe.api;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.Nullable;

/**
 * Fluent builder for stonecutter recipes.
 * <p>
 * Obtain an instance through {@link RecipeBuilder#stonecutting}.
 */
public interface StonecutterRecipeBuilder extends BaseRecipeBuilder<StonecutterRecipeBuilder>, BaseUnlockableRecipeBuilder<StonecutterRecipeBuilder> {
    /**
     * Sets the recipe's input to any item from the given tag. Also adds an unlock criterion for the tag.
     *
     * @param input The input tag.
     * @return This builder, for chaining.
     */
    StonecutterRecipeBuilder input(TagKey<Item> input);

    /**
     * Sets the recipe's input to the given item. Also adds an unlock criterion for it.
     *
     * @param input The input item.
     * @return This builder, for chaining.
     */
    StonecutterRecipeBuilder input(ItemLike input);

    /**
     * Sets the recipe's input directly. Also adds an unlock criterion for it.
     *
     * @param in The input ingredient.
     * @return This builder, for chaining.
     */
    StonecutterRecipeBuilder input(Ingredient in);

    /**
     * Sets the recipe's input from a generic {@link RecipeMaterial}. Also adds an unlock criterion derived
     * from the material.
     *
     * @param in The input material.
     * @return This builder, for chaining.
     */
    StonecutterRecipeBuilder input(RecipeMaterial in);

    /**
     * Sets how many copies of the output item the recipe produces. Defaults to {@code 1}.
     *
     * @param count The output stack size.
     * @return This builder, for chaining.
     */
    StonecutterRecipeBuilder outputCount(int count);

    /**
     * Sets the recipe-book group the resulting recipe is merged into.
     *
     * @param group The group name, or {@code null} for no group.
     * @return This builder, for chaining.
     */
    StonecutterRecipeBuilder group(@Nullable String group);
}
