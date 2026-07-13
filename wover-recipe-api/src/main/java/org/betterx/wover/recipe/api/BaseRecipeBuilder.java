package org.betterx.wover.recipe.api;

import net.minecraft.data.recipes.RecipeCategory;

import org.jetbrains.annotations.NotNull;

/**
 * Base interface shared by every recipe builder in this module ({@link CraftingRecipeBuilder},
 * {@link CookingRecipeBuilder}, {@link SmithingRecipeBuilder}, {@link StonecutterRecipeBuilder}).
 * <p>
 * Instances are created through the static factory methods on {@link RecipeBuilder} and configured with a
 * fluent chain of calls before being finalized with {@link #build(RecipeBuilder.Context)}.
 *
 * @param <I> The concrete builder type, used so that fluent methods declared here return the subtype instead
 *            of {@link BaseRecipeBuilder}.
 */
public interface BaseRecipeBuilder<I extends BaseRecipeBuilder<I>> {
    /**
     * Sets the creative-inventory/recipe-book category the resulting recipe is grouped under.
     *
     * @param category The category to use.
     * @return This builder, for chaining.
     */
    I category(@NotNull RecipeCategory category);

    /**
     * Alias for {@link #category(RecipeCategory)}.
     *
     * @param category The category to use.
     * @return This builder, for chaining.
     */
    default I setCategory(@NotNull RecipeCategory category) {
        return category(category);
    }

    /**
     * Validates the builder state and writes the finished recipe (and, where applicable, its unlock
     * advancement) to the passed context.
     *
     * @param ctx The datagen context to write the recipe to.
     */
    void build(RecipeBuilder.Context ctx);
}
