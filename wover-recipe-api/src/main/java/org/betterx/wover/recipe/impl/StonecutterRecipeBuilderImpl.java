package org.betterx.wover.recipe.impl;

import org.betterx.wover.recipe.api.StonecutterRecipeBuilder;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.NotNull;

public class StonecutterRecipeBuilderImpl extends BaseRecipeBuilderImpl<StonecutterRecipeBuilder> implements StonecutterRecipeBuilder {
    CraftingRecipeBuilderImpl.IngredientFactory input;

    public StonecutterRecipeBuilderImpl(
            @NotNull ResourceLocation id,
            @NotNull ItemLike output
    ) {
        super(id, output);
    }


    public StonecutterRecipeBuilder input(TagKey<Item> tagKey) {
        this.input = provider -> provider.tag(tagKey);
        unlockedBy(tagKey);
        return this;
    }

    public StonecutterRecipeBuilder input(ItemLike input) {
        this.input = provider -> Ingredient.of(input);
        unlockedBy(input);
        return this;
    }

    public StonecutterRecipeBuilder input(Ingredient input) {
        this.input = provider -> input;
        unlockedBy(input);
        return this;
    }

    @Override
    protected void validate() {
        super.validate();

        if (input == null) {
            throwIllegalStateException("Input must be set");
        }
    }

    @Override
    public void build(HolderGetter<Item> items, RecipeProvider provider, RecipeOutput ctx) {
        final SingleItemRecipeBuilder builder = SingleItemRecipeBuilder.stonecutting(
                input.createIngredient(provider), category, output.getItem(), output.getCount()
        );

        for (var item : unlocks.entrySet()) {
            builder.unlockedBy(item.getKey(), item.getValue().createCriterion(provider));
        }

        builder.group(group);
        builder.save(ctx, this.key());
    }
}
