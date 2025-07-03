package org.betterx.wover.recipe.impl;

import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.recipe.api.RecipeMaterial;
import org.betterx.wover.recipe.api.StonecutterRecipeBuilder;

import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

    public StonecutterRecipeBuilder input(RecipeMaterial input) {
        final var self = this;
        input.consume(
                new RecipeMaterial.Consumer() {
                    @Override
                    public void apply(TagKey<Item> tag) {
                        self.input(tag);
                    }

                    @Override
                    public void apply(ItemStack... stacks) {
                        if (stacks.length == 0) {
                            throwIllegalStateException("No stacks provided for input");
                        }
                        self.input(stacks[0].getItem());
                    }

                    @Override
                    public void apply(ItemLike... items) {
                        if (items.length == 0) {
                            throwIllegalStateException("No stacks provided for input");
                        }
                        self.input(items[0]);
                    }

                    @Override
                    public void apply(Ingredient ingredient) {
                        self.input(ingredient);
                    }
                }
        );
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
    public void build(RecipeBuilder.Context context) {
        final SingleItemRecipeBuilder builder = SingleItemRecipeBuilder.stonecutting(
                input.createIngredient(context), category, output.getItem(), output.getCount()
        );

        for (var item : unlocks.entrySet()) {
            builder.unlockedBy(item.getKey(), item.getValue().createCriterion(context));
        }

        builder.group(group);
        builder.save(context.recipeOutput(), this.key());
    }
}
