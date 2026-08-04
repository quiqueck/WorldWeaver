package de.ambertation.wover.recipe.impl;

import de.ambertation.wover.recipe.api.CraftingRecipeBuilder;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.recipe.api.RecipeMaterial;

import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CraftingRecipeBuilderImpl extends BaseRecipeBuilderImpl<CraftingRecipeBuilder> implements
        CraftingRecipeBuilder {
    public interface IngredientFactory {
        Ingredient createIngredient(RecipeBuilder.Context context);
    }

    private String[] shape;
    protected boolean showNotification;
    protected final Map<Character, IngredientFactory> materials;

    public CraftingRecipeBuilderImpl(ResourceLocation id, ItemLike output) {
        super(id, output);
        this.showNotification = true;
        this.materials = new HashMap<>();
    }

    @Override
    public CraftingRecipeBuilder addMaterial(char key, TagKey<Item> tagKey) {
        unlockedBy(tagKey);
        return _addMaterial(key, (provider) -> provider.tag(tagKey));
    }

    @Override
    public CraftingRecipeBuilder addMaterial(char key, ItemStack... values) {
        unlockedBy(values);
        return _addMaterial(
                key,
                provider -> Ingredient.of(Arrays.stream(values).map(ItemStack::getItem))
        );
    }

    @Override
    public CraftingRecipeBuilder addMaterial(char key, ItemLike... values) {
        unlockedBy(values);
        return _addMaterial(key, provider -> Ingredient.of(values));
    }

    @Override
    public CraftingRecipeBuilderImpl addMaterial(char key, Ingredient value) {
        unlockedBy(value);
        return _addMaterial(key, items -> value);
    }

    @Override
    public CraftingRecipeBuilderImpl addMaterial(char key, RecipeMaterial value) {
        final var self = this;
        value.consume(
                new RecipeMaterial.Consumer() {
                    @Override
                    public void apply(TagKey<Item> tag) {
                        self.addMaterial(key, tag);
                    }

                    @Override
                    public void apply(ItemStack... stacks) {
                        self.addMaterial(key, stacks);
                    }

                    @Override
                    public void apply(ItemLike... items) {
                        self.addMaterial(key, items);
                    }

                    @Override
                    public void apply(Ingredient ingredient) {
                        self.addMaterial(key, ingredient);
                    }
                }
        );
        return this;
    }

    public CraftingRecipeBuilderImpl _addMaterial(char key, IngredientFactory factory) {
        materials.put(key, factory);
        return this;
    }

    @Override
    public CraftingRecipeBuilderImpl shape(String... shape) {
        if (shape.length != 0) {
            for (int i = 1; i < shape.length; i++) {
                if (shape[i].length() != shape[0].length()) {
                    throw new IllegalArgumentException("Shape must be rectangular");
                }
            }
        }
        this.shape = shape;
        return this;
    }

    @Override
    public CraftingRecipeBuilder shapeless() {
        this.shape = null;
        return this;
    }

    @Override
    public CraftingRecipeBuilder showNotification() {
        this.showNotification = true;
        return this;
    }

    protected boolean isShaped() {
        return shape != null && shape.length > 0;
    }

    @Override
    protected void validate() {
        super.validate();
        if (isShaped()) {
            for (String row : shape) {
                for (char c : row.toCharArray()) {
                    if (c != ' ' && materials.get(c) == null)
                        throwIllegalStateException("Material for character '" + c + "' is not set");
                }
            }

            for (Character m : materials.keySet()) {
                if (m == ' ') {
                    throwIllegalStateException("Material-key for character ' ' is not allowed");
                }
                //if letter is not used in shape array then throw
                if (Arrays.stream(shape).noneMatch(row -> row.contains(m.toString()))) {
                    throwIllegalStateException("Material for character '" + m + "' is not used in shape");
                }
            }
        }
    }

    private void buildShaped(RecipeBuilder.Context context) {
        var builder = ShapedRecipeBuilder.shaped(context.itemLookup(), category, output.getItem(), output.getCount());

        for (Map.Entry<Character, IngredientFactory> mat : materials.entrySet()) {
            builder.define(mat.getKey(), mat.getValue().createIngredient(context));
        }

        for (String row : shape) builder.pattern(row);

        if (shouldUnlockAdvancements) {
            for (var item : unlocks.entrySet()) {
                builder.unlockedBy(item.getKey(), item.getValue().createCriterion(context));
            }
        }

        builder.showNotification(this.showNotification);
        builder.group(this.group);
        builder.save(context.recipeOutput(), this.key());
    }

    private void buildShapeless(RecipeBuilder.Context context) {
        var builder = ShapelessRecipeBuilder.shapeless(
                context.itemLookup(),
                category,
                output.getItem(),
                output.getCount()
        );

        for (Map.Entry<Character, IngredientFactory> mat : materials.entrySet()) {
            builder.requires(mat.getValue().createIngredient(context));
        }

        if (shouldUnlockAdvancements) {
            for (var item : unlocks.entrySet()) {
                builder.unlockedBy(item.getKey(), item.getValue().createCriterion(context));
            }
        }

        builder.group(this.group);
        builder.save(context.recipeOutput(), this.key());
    }

    @Override
    public void build(RecipeBuilder.Context context) {
        validate();
        if (isShaped()) {
            buildShaped(context);
        } else {
            buildShapeless(context);
        }
    }
}
