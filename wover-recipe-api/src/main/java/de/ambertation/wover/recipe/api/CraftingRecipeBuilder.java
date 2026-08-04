package de.ambertation.wover.recipe.api;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.Nullable;

/**
 * Fluent builder for crafting-table recipes, covering both shaped and shapeless recipes.
 * <p>
 * A recipe is shaped by default: use {@link #shape(String...)} to define the pattern and
 * {@link #addMaterial(char, ItemLike...)} (or one of its overloads) to bind each character used in the pattern
 * to an ingredient. Call {@link #shapeless()} instead of {@link #shape(String...)} to produce a shapeless recipe,
 * in which case every material added via {@code addMaterial} is required once, regardless of the key it was
 * registered under.
 * <p>
 * Obtain an instance through {@link RecipeBuilder#crafting}.
 */
public interface CraftingRecipeBuilder extends BaseRecipeBuilder<CraftingRecipeBuilder>, BaseUnlockableRecipeBuilder<CraftingRecipeBuilder> {
    /**
     * Sets the recipe-book group the resulting recipe is merged into.
     *
     * @param group The group name, or {@code null} for no group.
     * @return This builder, for chaining.
     */
    CraftingRecipeBuilder group(@Nullable String group);

    /**
     * Sets how many copies of the output item the recipe produces. Defaults to {@code 1}.
     *
     * @param count The output stack size.
     * @return This builder, for chaining.
     */
    CraftingRecipeBuilder outputCount(int count);

    /**
     * Binds a character used in {@link #shape(String...)} (or, for a shapeless recipe, an unkeyed required
     * ingredient) to any item from the given tag. Also adds an unlock criterion for the tag.
     *
     * @param key   The character to bind, as used in {@link #shape(String...)}.
     * @param value The tag to accept as this material.
     * @return This builder, for chaining.
     */
    CraftingRecipeBuilder addMaterial(char key, TagKey<Item> value);

    /**
     * Binds a character used in {@link #shape(String...)} (or, for a shapeless recipe, an unkeyed required
     * ingredient) to any of the given items. Also adds an unlock criterion for the items.
     *
     * @param key    The character to bind, as used in {@link #shape(String...)}.
     * @param values The items to accept as this material.
     * @return This builder, for chaining.
     */
    CraftingRecipeBuilder addMaterial(char key, ItemLike... values);

    /**
     * Binds a character used in {@link #shape(String...)} (or, for a shapeless recipe, an unkeyed required
     * ingredient) to a generic {@link RecipeMaterial}. Also adds an unlock criterion derived from the material.
     *
     * @param key   The character to bind, as used in {@link #shape(String...)}.
     * @param value The material to accept.
     * @return This builder, for chaining.
     */
    CraftingRecipeBuilder addMaterial(char key, RecipeMaterial value);

    /**
     * Defines the pattern of a shaped recipe. Every row must have the same length, a space character
     * ({@code ' '}) marks an empty cell, and every other character used must be bound with one of the
     * {@code addMaterial} overloads.
     * <p>
     * Calling this method switches the builder to shaped mode; use {@link #shapeless()} to switch back.
     *
     * @param shape The rows of the recipe pattern, top to bottom.
     * @return This builder, for chaining.
     */
    CraftingRecipeBuilder shape(String... shape);

    /**
     * Switches the builder to shapeless mode, discarding any pattern set via {@link #shape(String...)}. Every
     * material added via {@code addMaterial} becomes a required (unordered) ingredient.
     *
     * @return This builder, for chaining.
     */
    CraftingRecipeBuilder shapeless();

    /**
     * Enables the toast notification shown to players when the recipe is unlocked. This is the default.
     *
     * @return This builder, for chaining.
     */
    CraftingRecipeBuilder showNotification();

    /**
     * Binds a character used in {@link #shape(String...)} directly to an {@link Ingredient}.
     *
     * @param key        The character to bind, as used in {@link #shape(String...)}.
     * @param ingredient The ingredient to accept as this material.
     * @return This builder, for chaining.
     */
    CraftingRecipeBuilder addMaterial(char key, Ingredient ingredient);

    /**
     * Binds a character used in {@link #shape(String...)} (or, for a shapeless recipe, an unkeyed required
     * ingredient) to any of the given item stacks (their count is ignored). Also adds an unlock criterion for
     * the stacks' items.
     *
     * @param key    The character to bind, as used in {@link #shape(String...)}.
     * @param values The item stacks whose items should be accepted as this material.
     * @return This builder, for chaining.
     */
    CraftingRecipeBuilder addMaterial(char key, ItemStack... values);

}
