package org.betterx.wover.recipe.api;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

/**
 * Fluent builder for cooking recipes (furnace/smelting, blast furnace, smoker and campfire).
 * <p>
 * A single builder instance can emit recipes for more than one cooking device at once: which devices are
 * enabled depends on which {@link RecipeBuilder} factory method was used to create it
 * ({@link RecipeBuilder#cooking}, {@link RecipeBuilder#cookableFood}, {@link RecipeBuilder#smelting},
 * {@link RecipeBuilder#blasting}, {@link RecipeBuilder#smoker}, {@link RecipeBuilder#campfire}), and can be
 * further adjusted with the {@code enable...}/{@code disable...} methods below. {@link #build(RecipeBuilder.Context)}
 * writes one recipe JSON per enabled device, each with a unique id derived from the recipe's base id.
 * <p>
 * Obtain an instance through one of the static factory methods on {@link RecipeBuilder}.
 */
public interface CookingRecipeBuilder extends BaseRecipeBuilder<CookingRecipeBuilder> {
    /**
     * Sets the recipe's input to any item from the given tag.
     *
     * @param input The input tag.
     * @return This builder, for chaining.
     */
    CookingRecipeBuilder input(TagKey<Item> input);

    /**
     * Sets the recipe's input to the given item.
     *
     * @param input The input item.
     * @return This builder, for chaining.
     */
    CookingRecipeBuilder input(ItemLike input);

    /**
     * Sets the recipe's input to the given ingredient.
     *
     * @param in The input ingredient.
     * @return This builder, for chaining.
     */
    CookingRecipeBuilder input(Ingredient in);

    /**
     * Sets the recipe's input from a generic {@link RecipeMaterial}.
     *
     * @param in The input material.
     * @return This builder, for chaining.
     */
    CookingRecipeBuilder input(RecipeMaterial in);

    /**
     * Sets the amount of experience the player receives for cooking (and collecting) the output.
     *
     * @param xp The experience amount.
     * @return This builder, for chaining.
     */
    CookingRecipeBuilder experience(float xp);

    /**
     * Sets the base cooking time (in ticks) needed by a furnace/smelting recipe. The blast furnace, smoker and
     * campfire variants derive their own time from this value (blast furnace and smoker halve it, campfire
     * triples it), matching vanilla behavior. Defaults to {@code 200} (10 seconds).
     *
     * @param time The base cooking time, in ticks.
     * @return This builder, for chaining.
     */
    CookingRecipeBuilder cookingTime(int time);

    /**
     * Enables generation of a furnace/smelting recipe.
     *
     * @return This builder, for chaining.
     */
    CookingRecipeBuilder enableSmelter();

    /**
     * Disables generation of a furnace/smelting recipe.
     *
     * @return This builder, for chaining.
     */
    CookingRecipeBuilder disableSmelter();

    /**
     * Enables generation of a blast furnace recipe.
     *
     * @return This builder, for chaining.
     */
    CookingRecipeBuilder enableBlastFurnace();

    /**
     * Disables generation of a blast furnace recipe.
     *
     * @return This builder, for chaining.
     */
    CookingRecipeBuilder disableBlastFurnace();

    /**
     * Enables generation of a campfire recipe.
     *
     * @return This builder, for chaining.
     */
    CookingRecipeBuilder enableCampfire();

    /**
     * Disables generation of a campfire recipe.
     *
     * @return This builder, for chaining.
     */
    CookingRecipeBuilder disableCampfire();

    /**
     * Enables generation of a smoker recipe.
     *
     * @return This builder, for chaining.
     */
    CookingRecipeBuilder enableSmoker();

    /**
     * Disables generation of a smoker recipe.
     *
     * @return This builder, for chaining.
     */
    CookingRecipeBuilder disableSmoker();
}
