package org.betterx.wover.item.api.trait;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverRecipe;
import org.betterx.wover.item.api.ItemRegistry;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.BiPredicate;
import org.jetbrains.annotations.Nullable;

public class ItemRecipeTrait extends ItemTrait<Item, ItemRecipeTrait.RuntimeTrait> {
    public static final Builder BUILDER = new Builder();

    public static class Builder extends ItemTrait.TraitBuilder {
        private Builder() {
            super(ItemTraitKey.of(LibWoverRecipe.C, "recipe_generator"));
        }

        public @Nullable ItemRecipeTrait with(RecipeFactory recipeFactory) {
            if (!ModCore.isDatagen()) return null;
            return new ItemRecipeTrait(recipeFactory);
        }
    }

    public static class RuntimeTrait extends RuntimeItemTrait<Item, RuntimeTrait> {
        public final RecipeFactory recipeFactory;

        private RuntimeTrait(RecipeFactory recipeFactory) {
            super(BUILDER.ID);
            this.recipeFactory = recipeFactory;
        }
    }

    public interface RecipeFactory {
        void buildRecipe(ResourceKey<Item> key, Item item, RecipeBuilder.Context context);
    }

    public final RecipeFactory recipeFactory;

    ItemRecipeTrait(RecipeFactory recipeFactory) {
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
        bootstrapRecipes(modCore, context, (r, i) -> true);
    }

    public static void bootstrapRecipes(
            ModCore modCore,
            RecipeBuilder.Context context,
            BiPredicate<ResourceKey<Item>, Item> filter
    ) {
        ItemRegistry
                .forMod(modCore)
                .allEntries().filter(e -> filter.test(e.getKey(), e.getValue())).forEach(e -> {
                    var runtimeTraits = ItemTrait.<Item, RuntimeTrait>getRuntimeTraits(e.getValue(), BUILDER.ID);
                    if (runtimeTraits == null) return;
                    runtimeTraits.forEach(trait -> trait.recipeFactory.buildRecipe(e.getKey(), e.getValue(), context));
                });
    }
}
