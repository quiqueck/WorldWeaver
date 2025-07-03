package org.betterx.wover.recipe.api;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public interface CookingRecipeBuilder extends BaseRecipeBuilder<CookingRecipeBuilder> {
    CookingRecipeBuilder input(TagKey<Item> input);
    CookingRecipeBuilder input(ItemLike input);
    CookingRecipeBuilder input(Ingredient in);
    CookingRecipeBuilder input(RecipeMaterial in);

    CookingRecipeBuilder experience(float xp);
    CookingRecipeBuilder cookingTime(int time);

    CookingRecipeBuilder enableSmelter();
    CookingRecipeBuilder disableSmelter();
    CookingRecipeBuilder enableBlastFurnace();
    CookingRecipeBuilder disableBlastFurnace();
    CookingRecipeBuilder enableCampfire();
    CookingRecipeBuilder disableCampfire();
    CookingRecipeBuilder enableSmoker();
    CookingRecipeBuilder disableSmoker();
}
