package org.betterx.wover.recipe.impl;

import org.betterx.wover.recipe.api.BaseRecipeBuilder;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseRecipeBuilderImpl<I extends BaseRecipeBuilder<I>> implements BaseRecipeBuilder<I> {
    public interface UnlockCriterionFactory {
        Criterion<?> createCriterion(RecipeBuilder.Context context);
    }

    protected RecipeCategory category;
    protected String group;
    protected boolean shouldUnlockAdvancements;
    protected final @NotNull ItemStack output;
    protected final @NotNull ResourceKey<Recipe<?>> key;

    protected BaseRecipeBuilderImpl(@NotNull ResourceLocation key, @NotNull ItemLike output) {
        this(key, new ItemStack(output, 1));
    }

    protected BaseRecipeBuilderImpl(@NotNull ResourceLocation key, @NotNull ItemStack output) {
        this.key = ResourceKey.create(Registries.RECIPE, key);
        this.category = RecipeCategory.MISC;
        this.output = output;
        this.unlocks = new HashMap<>();
        this.shouldUnlockAdvancements = true;
    }

    public ResourceKey<Recipe<?>> key() {
        return key;
    }

    public I shouldUnlockAdvancements(boolean shouldUnlockAdvancements) {
        this.shouldUnlockAdvancements = shouldUnlockAdvancements;
        return (I) this;
    }

    public I outputCount(int count) {
        this.output.setCount(count);
        return (I) this;
    }

    @Override
    public I category(@NotNull RecipeCategory category) {
        this.category = category;
        return (I) this;
    }

    public I group(@Nullable String group) {
        this.group = group;
        return (I) this;
    }

    // Advancements
    protected final Map<String, UnlockCriterionFactory> unlocks;

    public I unlocks(String name, Criterion<?> criterion) {
        this.unlocks.put(name, (provider) -> criterion);
        return (I) this;
    }

    public I unlocks(String name, UnlockCriterionFactory criterionFactory) {
        this.unlocks.put(name, criterionFactory);
        return (I) this;
    }

    /**
     * The Recipe will be unlocked by one of the passed Items. As sonn als players have one in their Inventory
     * the recipe will unlock. Those Items are mostly the input Items for the recipe.
     *
     * @param name  The name for this unlock-Criteria
     * @param items {@link Item}s or {@link Block}s that will unlock the recipe.
     */
    public I unlocks(String name, ItemLike... items) {
        return unlocks(name, InventoryChangeTrigger.TriggerInstance.hasItems(items));
    }

    public I unlockedBy(Ingredient ingredient) {
        ingredient.items().forEach(item -> {

            this.unlocks(
                    "has_" + item.value().getDescriptionId(),
                    (context) -> context.has(item.value())
            );

        });

        return (I) this;
    }

    public I unlockedBy(ItemLike item) {
        this.unlocks(
                "has_" + item.asItem().getDescriptionId(),
                (context) -> context.has(item.asItem())
        );

        return (I) this;
    }

    public I unlockedBy(TagKey<Item> tag) {
        this.unlocks(
                "has_tag_" + tag.location().getNamespace() + "_" + tag.location().getPath(),
                (context) -> context.has(tag)
        );

        return (I) this;
    }

    /**
     * The Recipe will be unlocked by one of the passed Items. As sonn als players have one in their Inventory
     * the recipe will unlock. Those Items are mostly the input Items for the recipe.
     * <p>
     * This method will automatically derive a unique name for the criterion and call
     * {@link #unlocks(String, ItemLike...)}
     *
     * @param items {@link Item}s or {@link Block}s that will unlock the recipe.
     */
    public I unlockedBy(ItemLike... items) {
        String name = "has_" +
                Arrays.stream(items)
                      .map(block -> (block instanceof Block)
                              ? BuiltInRegistries.BLOCK.getKey((Block) block)
                              : BuiltInRegistries.ITEM.getKey((Item) block))
                      .filter(id -> id != null)
                      .map(id -> id.getPath())
                      .collect(Collectors.joining("_"));
        if (name.length() > 45) name = name.substring(0, 42);
        return unlocks(name, items);
    }

    /**
     * The Recipe will be unlocked by one of the passed Items. As sonn als players have one in their Inventory
     * the recipe will unlock. Those Items are mostly the input Items for the recipe.
     * <p>
     * This method will automatically get the Items from the stacl and call {@link #unlockedBy(ItemLike...)}
     *
     * @param stacks {@link ItemStack}s that will unlock the recipe. The count is ignored.
     */
    public I unlockedBy(ItemStack... stacks) {
        ItemLike[] items = Arrays.stream(stacks)
                                 .filter(stack -> stack.getCount() > 0)
                                 .map(stack -> (ItemLike) stack.getItem())
                                 .toArray(ItemLike[]::new);

        return unlockedBy(items);
    }

    // Validation and Building
    protected void throwIllegalStateException(String message) {
        throw new IllegalStateException(message + "(" + this.key + ")");
    }

    protected void validate() {
        if (output.getCount() <= 0) {
            throwIllegalStateException("Output-Count is zero");
        }
        if (category == null) {
            throwIllegalStateException("Category is not set");
        }
    }


    //for testing
    //campfireCooking, stonecutting
    protected Ingredient ingredient;

    //campfireCooking
    protected int experience;
    protected int cookingTime;

//    public void build() {
//        var builder = ShapedRecipeBuilder.shaped(category, output.getItem(), output.getCount()).showNotification();
//        ShapelessRecipeBuilder.shapeless(category, output.getItem(), output.getCount());
//        SimpleCookingRecipeBuilder.campfireCooking(ingredient, category, output.getItem(), experience, cookingTime);
//        SimpleCookingRecipeBuilder.blasting(ingredient, category, output.getItem(), experience, cookingTime);
//        SimpleCookingRecipeBuilder.smelting(ingredient, category, output.getItem(), experience, cookingTime);
//        SimpleCookingRecipeBuilder.smoking(ingredient, category, output.getItem(), experience, cookingTime);
//
//        SingleItemRecipeBuilder.stonecutting(ingredient, category, output.getItem(), output.getCount());
//
//        SmithingTransformRecipeBuilder.smithing(ingredient, ingredient, ingredient, category, output.getItem());
//        SmithingTrimRecipeBuilder.smithingTrim(ingredient, ingredient, ingredient, category);
//    }
}
