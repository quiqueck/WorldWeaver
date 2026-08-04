package de.ambertation.wover.recipe.impl;

import de.ambertation.wover.recipe.api.CookingRecipeBuilder;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.recipe.api.RecipeMaterial;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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


    public CookingRecipeBuilder input(RecipeMaterial input) {
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

        if (!smelting && !blasting && !campfire && !smoker) {
            throwIllegalStateException(
                    "No target (smelting, blasting, campfire or somer) for cooking recipe was selected");
        }

        if (cookingTime < 0) {
            throwIllegalStateException("cooking time must be positive. Recipe {} will be ignored!");
        }
    }

    @Override
    public void build(RecipeBuilder.Context context) {
        if (smelting) {
            buildRecipe(
                    context, "smelting",
                    SimpleCookingRecipeBuilder.smelting(
                            input.createIngredient(context),
                            category,
                            output.getItem(),
                            xp,
                            cookingTime
                    )
            );
        }

        if (blasting) {
            buildRecipe(
                    context, "blasting",
                    SimpleCookingRecipeBuilder.blasting(
                            input.createIngredient(context),
                            category,
                            output.getItem(),
                            xp,
                            cookingTime / 2
                    )
            );
        }

        if (campfire) {
            buildRecipe(
                    context, "campfire",
                    SimpleCookingRecipeBuilder.campfireCooking(
                            input.createIngredient(context),
                            category,
                            output.getItem(),
                            xp,
                            cookingTime * 3
                    )
            );
        }

        if (smoker) {
            buildRecipe(
                    context, "smoker",
                    SimpleCookingRecipeBuilder.smoking(
                            input.createIngredient(context),
                            category,
                            output.getItem(),
                            xp,
                            cookingTime / 2
                    )
            );
        }
    }

    private void buildRecipe(
            RecipeBuilder.Context context,
            String suffix,
            SimpleCookingRecipeBuilder builder
    ) {
        ResourceLocation loc = key.location().withSuffix("_" + suffix);

        for (var item : unlocks.entrySet()) {
            builder.unlockedBy(item.getKey(), item.getValue().createCriterion(context));
        }
        builder.save(context.recipeOutput(), ResourceKey.create(Registries.RECIPE, loc));
    }
}
