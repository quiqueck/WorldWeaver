package org.betterx.wover.recipe.impl;

import org.betterx.wover.recipe.api.CookingRecipeBuilder;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class CookingRecipeBuilderImpl extends BaseRecipeBuilderImpl<CookingRecipeBuilder> implements CookingRecipeBuilder {
    protected float xp;
    protected int cookingTime;
    protected CraftingRecipeBuilderImpl.IngredientFactory input;

    protected boolean blasting, campfire, smoker, smelting;

    public CookingRecipeBuilderImpl(
            ResourceLocation id,
            ItemLike output,
            boolean blasting,
            boolean campfire,
            boolean smoker,
            boolean smelting
    ) {
        super(id, output);
        this.xp = 0;
        this.cookingTime = 200;
        this.blasting = blasting;
        this.campfire = campfire;
        this.smoker = smoker;
        this.smelting = smelting;
    }

    public CookingRecipeBuilder experience(float xp) {
        this.xp = xp;
        return this;
    }

    public CookingRecipeBuilder cookingTime(int time) {
        this.cookingTime = time;
        return this;
    }

    public CookingRecipeBuilder enableSmelter() {
        this.smelting = true;
        return this;
    }

    public CookingRecipeBuilder disableSmelter() {
        this.smelting = false;
        return this;
    }

    public CookingRecipeBuilder enableBlastFurnace() {
        this.blasting = true;
        return this;
    }

    public CookingRecipeBuilder disableBlastFurnace() {
        this.blasting = false;
        return this;
    }

    public CookingRecipeBuilder enableCampfire() {
        this.campfire = true;
        return this;
    }

    public CookingRecipeBuilder disableCampfire() {
        this.campfire = false;
        return this;
    }

    public CookingRecipeBuilder enableSmoker() {
        this.smoker = true;
        return this;
    }

    public CookingRecipeBuilder disableSmoker() {
        this.smoker = false;
        return this;
    }

    public CookingRecipeBuilder input(TagKey<Item> tagKey) {
        this.input = provider -> provider.tag(tagKey);
        unlockedBy(tagKey);
        return this;
    }

    public CookingRecipeBuilder input(ItemLike input) {
        this.input = provider -> Ingredient.of(input);
        unlockedBy(input);
        return this;
    }

    public CookingRecipeBuilder input(Ingredient input) {
        this.input = provider -> input;
        unlockedBy(input);
        return this;
    }

    @Override
    protected void validate() {
        super.validate();

        if (!smelting && !blasting && !campfire && !smoker) {
            throwIllegalStateException(
                    "No target (smelting, blasting, campfire or somer) for cooking recipe was selected");
        }

        if (cookingTime < 0) {
            throwIllegalStateException("cooking time must be positive. Recipe {} will be ignored!");
        }
    }

    @Override
    public void build(HolderGetter<Item> items, RecipeProvider provider, RecipeOutput ctx) {
        if (smelting) {
            buildRecipe(
                    provider, ctx, "smelting",
                    SimpleCookingRecipeBuilder.smelting(
                            input.createIngredient(provider),
                            category,
                            output.getItem(),
                            xp,
                            cookingTime
                    )
            );
        }

        if (blasting) {
            buildRecipe(
                    provider, ctx, "blasting",
                    SimpleCookingRecipeBuilder.blasting(
                            input.createIngredient(provider),
                            category,
                            output.getItem(),
                            xp,
                            cookingTime / 2
                    )
            );
        }

        if (campfire) {
            buildRecipe(
                    provider, ctx, "campfire",
                    SimpleCookingRecipeBuilder.campfireCooking(
                            input.createIngredient(provider),
                            category,
                            output.getItem(),
                            xp,
                            cookingTime * 3
                    )
            );
        }

        if (smoker) {
            buildRecipe(
                    provider, ctx, "smoker",
                    SimpleCookingRecipeBuilder.campfireCooking(
                            input.createIngredient(provider),
                            category,
                            output.getItem(),
                            xp,
                            cookingTime / 2
                    )
            );
        }
    }

    private void buildRecipe(
            RecipeProvider provider,
            RecipeOutput ctx,
            String suffix,
            SimpleCookingRecipeBuilder builder
    ) {
        ResourceLocation loc = id.withSuffix("_" + suffix);

        for (var item : unlocks.entrySet()) {
            builder.unlockedBy(item.getKey(), item.getValue().createCriterion(provider));
        }
        builder.save(ctx, ResourceKey.create(Registries.RECIPE, loc));
    }
}
