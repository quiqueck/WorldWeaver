package org.betterx.wover.recipe.impl;

import org.betterx.wover.recipe.api.SmithingRecipeBuilder;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import org.jetbrains.annotations.NotNull;

public class SmithingRecipeBuilderImpl extends BaseRecipeBuilderImpl<SmithingRecipeBuilder> implements SmithingRecipeBuilder {
    protected CraftingRecipeBuilderImpl.IngredientFactory template;
    protected CraftingRecipeBuilderImpl.IngredientFactory base;
    protected CraftingRecipeBuilderImpl.IngredientFactory addon;

    public SmithingRecipeBuilderImpl(
            @NotNull ResourceLocation id,
            @NotNull ItemLike output
    ) {
        super(id, output);
    }

    @Override
    public SmithingRecipeBuilderImpl template(SmithingTemplateItem in) {
        this.template = provider -> Ingredient.of(in);
        unlockedBy(in);
        return this;
    }

    @Override
    public SmithingRecipeBuilderImpl base(TagKey<Item> in) {
        this.base = provider -> provider.tag(in);
        return this;
    }

    @Override
    public SmithingRecipeBuilderImpl base(ItemLike in) {
        this.base = provider -> Ingredient.of(in);
        return this;
    }

    @Override
    public SmithingRecipeBuilderImpl base(Ingredient in) {
        this.base = provider -> in;
        return this;
    }

    @Override
    public SmithingRecipeBuilderImpl addon(TagKey<Item> in) {
        this.addon = provider -> provider.tag(in);
        return this;
    }

    @Override
    public SmithingRecipeBuilderImpl addon(ItemLike in) {
        this.addon = provider -> Ingredient.of(in);
        return this;
    }

    @Override
    public SmithingRecipeBuilderImpl addon(Ingredient in) {
        this.addon = provider -> in;
        return this;
    }

    @Override
    protected void validate() {
        super.validate();

        if (template == null) {
            throwIllegalStateException("Template must be set");
        }
        if (base == null) {
            throwIllegalStateException("Base must be set");
        }
        if (addon == null) {
            throwIllegalStateException("Addon must be set");
        }
        if (output.getCount() != 1) {
            throwIllegalStateException("Output count must be 1");
        }
    }

    @Override
    public void build(HolderGetter<Item> items, RecipeProvider provider, RecipeOutput ctx) {
        final SmithingTransformRecipeBuilder builder = SmithingTransformRecipeBuilder.smithing(
                template.createIngredient(provider),
                base.createIngredient(provider),
                addon.createIngredient(provider),
                category,
                output.getItem()
        );

        for (var item : unlocks.entrySet()) {
            builder.unlocks(item.getKey(), item.getValue().createCriterion(provider));
        }
        builder.save(ctx, this.key());
    }
}
