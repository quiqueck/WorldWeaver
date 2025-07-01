package org.betterx.wover.item.api.trait;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.item.impl.trait.ItemRecipeTraitBuilder;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.BiPredicate;
import org.jetbrains.annotations.Nullable;

public interface ItemRecipeTrait extends ItemTrait<Item, ItemRecipeTrait> {
    interface RecipeFactory {
        void buildRecipe(ResourceKey<Item> key, Item item, RecipeBuilder.Context context);
    }

    interface Builder extends ItemTraitBuilder<Item, ItemRecipeTrait> {
        @Nullable ItemRecipeTrait with(ItemRecipeTrait.RecipeFactory recipeFactory);
    }

    ItemRecipeTrait.RecipeFactory recipeFactory();

    static void bootstrapRecipes(ModCore modCore, RecipeBuilder.Context context) {
        ItemRecipeTraitBuilder.bootstrapRecipes(modCore, context, (r, i) -> true);
    }

    static void bootstrapRecipes(
            ModCore modCore,
            RecipeBuilder.Context context,
            BiPredicate<ResourceKey<Item>, Item> filter
    ) {
        ItemRecipeTraitBuilder.bootstrapRecipes(modCore, context, filter);
    }
}