package org.betterx.wover.item.api.trait;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverRecipe;
import org.betterx.wover.item.api.ItemRegistry;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.world.item.Item;

import java.util.function.Predicate;
import org.jetbrains.annotations.Nullable;

public class ItemRecipeGeneratorTrait extends ItemTrait<Item, ItemRecipeGeneratorTrait.RuntimeTrait> {
    public static final Builder BUILDER = new Builder();

    public static class Builder extends ItemTrait.TraitBuilder {
        private Builder() {
            super(ItemTraitKey.of(LibWoverRecipe.C, "recipe_generator"));
        }

        public @Nullable ItemRecipeGeneratorTrait with(RecipeFactory recipeFactory) {
            if (!ModCore.isDatagen()) return null;
            return new ItemRecipeGeneratorTrait(recipeFactory);
        }
    }

    public static class RuntimeTrait extends ItemTrait.RuntimeTrait<Item, ItemRecipeGeneratorTrait.RuntimeTrait> {
        public final RecipeFactory recipeFactory;

        private RuntimeTrait(RecipeFactory recipeFactory) {
            super(BUILDER.ID);
            this.recipeFactory = recipeFactory;
        }
    }

    public interface RecipeFactory {
        void buildRecipe(Item item, RecipeBuilder.Context context);
    }

    public final RecipeFactory recipeFactory;

    ItemRecipeGeneratorTrait(RecipeFactory recipeFactory) {
        super(BUILDER.ID);
        this.recipeFactory = recipeFactory;
    }

    @Override
    public RuntimeTrait forRuntime() {
        return new RuntimeTrait(recipeFactory);
    }

    @Override
    public boolean datagenOnly() {
        return true;
    }

    @Override
    public boolean clientOnly() {
        return true;
    }

    public static void bootstrapRecipes(ModCore modCore, RecipeBuilder.Context context) {
        bootstrapRecipes(modCore, context, item -> true);
    }

    public static void bootstrapRecipes(ModCore modCore, RecipeBuilder.Context context, Predicate<Item> filter) {
        ItemRegistry
                .forMod(modCore)
                .allItems().filter(filter).forEach(item -> {
                    ItemTrait.<Item, ItemRecipeGeneratorTrait.RuntimeTrait>getRuntimeTraits(item, BUILDER.ID)
                             .forEach(trait -> trait.recipeFactory.buildRecipe(item, context));

                });
    }
}
