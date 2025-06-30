package org.betterx.wover.recipe.api;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.Nullable;

public interface CraftingRecipeBuilder extends BaseRecipeBuilder<CraftingRecipeBuilder>, BaseUnlockableRecipeBuilder<CraftingRecipeBuilder> {
    CraftingRecipeBuilder group(@Nullable String group);
    CraftingRecipeBuilder outputCount(int count);

    CraftingRecipeBuilder addMaterial(char key, TagKey<Item> value);
    CraftingRecipeBuilder addMaterial(char key, ItemLike... values);
    CraftingRecipeBuilder addMaterial(char key, RecipeMaterial value);
    CraftingRecipeBuilder shape(String... shape);

    CraftingRecipeBuilder shapeless();
    CraftingRecipeBuilder showNotification();

    @Deprecated(forRemoval = true)
    CraftingRecipeBuilder addMaterial(char key, Ingredient ingredient);
    @Deprecated(forRemoval = true)
    CraftingRecipeBuilder addMaterial(char key, ItemStack... values);

}
