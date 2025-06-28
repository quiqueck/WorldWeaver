package org.betterx.wover.recipe.api;

import net.minecraft.data.recipes.RecipeCategory;

import org.jetbrains.annotations.NotNull;

public interface BaseRecipeBuilder<I extends BaseRecipeBuilder<I>> {
    I category(@NotNull RecipeCategory category);
    default I setCategory(@NotNull RecipeCategory category) {
        return category(category);
    }
    void build(RecipeBuilder.Context ctx);
}
