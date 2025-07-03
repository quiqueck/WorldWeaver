package org.betterx.wover.recipe.api;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.Nullable;

public interface StonecutterRecipeBuilder extends BaseRecipeBuilder<StonecutterRecipeBuilder>, BaseUnlockableRecipeBuilder<StonecutterRecipeBuilder> {
    StonecutterRecipeBuilder input(TagKey<Item> input);
    StonecutterRecipeBuilder input(ItemLike input);
    StonecutterRecipeBuilder input(Ingredient in);
    StonecutterRecipeBuilder input(RecipeMaterial in);

    StonecutterRecipeBuilder outputCount(int count);
    StonecutterRecipeBuilder group(@Nullable String group);
}
