package org.betterx.wover.item.api.trait;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.item.impl.trait.ItemRecipeTraitBuilder;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.BiPredicate;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link ItemTrait} that attaches a recipe-generating callback to an item definition, so the item's crafting
 * recipe(s) are generated automatically alongside the item itself instead of being written by hand.
 * <p>
 * Add it to an item definition via its {@link Builder} (see {@link #with}), and every registered item carrying
 * this trait has its recipe built automatically by {@link #bootstrapRecipes} — which
 * {@code wover-recipe-api}'s own datagen auto-provider already calls for every mod, so in most cases nothing
 * further needs to be wired up manually.
 */
public interface ItemRecipeTrait extends ItemTrait<Item, ItemRecipeTrait> {
    /**
     * Builds the recipe(s) for a single item.
     */
    interface RecipeFactory {
        /**
         * Builds and writes the recipe(s) that produce the given item.
         *
         * @param key     The registry key of the item the recipe is being built for.
         * @param item    The item instance the recipe is being built for.
         * @param context The context to write the built recipe(s) to.
         */
        void buildRecipe(ResourceKey<Item> key, Item item, RecipeBuilder.Context context);
    }

    /**
     * Builder for {@link ItemRecipeTrait} instances.
     */
    interface Builder extends ItemTraitBuilder<Item, ItemRecipeTrait> {
        /**
         * Creates a trait that builds its item's recipe(s) with the given factory.
         *
         * @param recipeFactory The factory that builds the recipe(s) for the item this trait is attached to.
         * @return The new trait, or {@code null} outside of a datagen environment (see
         * {@link ModCore#isDatagen()}), since recipe generation is only needed during datagen.
         */
        @Nullable ItemRecipeTrait with(ItemRecipeTrait.RecipeFactory recipeFactory);
    }

    /**
     * The factory that builds the recipe(s) for the item this trait is attached to.
     *
     * @return The recipe factory.
     */
    ItemRecipeTrait.RecipeFactory recipeFactory();

    /**
     * Builds the recipe(s) for every item registered under the given mod that carries an
     * {@link ItemRecipeTrait}.
     *
     * @param modCore The mod whose registered items should be processed.
     * @param context The context to write the built recipes to.
     */
    static void bootstrapRecipes(ModCore modCore, RecipeBuilder.Context context) {
        ItemRecipeTraitBuilder.bootstrapRecipes(modCore, context, (r, i) -> true);
    }

    /**
     * Builds the recipe(s) for every item registered under the given mod that carries an
     * {@link ItemRecipeTrait} and matches the given filter.
     *
     * @param modCore The mod whose registered items should be processed.
     * @param context The context to write the built recipes to.
     * @param filter  Only items for which this predicate returns {@code true} have their recipe(s) built.
     */
    static void bootstrapRecipes(
            ModCore modCore,
            RecipeBuilder.Context context,
            BiPredicate<ResourceKey<Item>, Item> filter
    ) {
        ItemRecipeTraitBuilder.bootstrapRecipes(modCore, context, filter);
    }
}