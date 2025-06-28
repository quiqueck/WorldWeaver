package org.betterx.wover.recipe.api;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;

import org.jetbrains.annotations.NotNull;

public interface BaseRecipeBuilder<I extends BaseRecipeBuilder<I>> {
    I category(@NotNull RecipeCategory category);
    default I setCategory(@NotNull RecipeCategory category) {
        return category(category);
    }
    void build(HolderGetter<Item> items, RecipeProvider provider, RecipeOutput ctx);
}
