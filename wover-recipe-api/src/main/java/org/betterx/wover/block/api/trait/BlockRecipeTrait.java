package org.betterx.wover.block.api.trait;

import org.betterx.wover.block.impl.trait.BlockRecipeTraitBuilder;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import java.util.function.BiPredicate;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link BlockTrait} that attaches a recipe-generating callback to a block definition, so the block's crafting
 * recipe(s) are generated automatically alongside the block itself instead of being written by hand.
 * <p>
 * Add it to a block definition via its {@link Builder} (see {@link #with}), and every registered block carrying
 * this trait has its recipe built automatically by {@link #bootstrapRecipes} — which
 * {@code wover-recipe-api}'s own datagen auto-provider already calls for every mod, so in most cases nothing
 * further needs to be wired up manually.
 */
public interface BlockRecipeTrait extends BlockTrait<Block, BlockRecipeTrait>, RuntimeBlockTrait<Block, BlockRecipeTrait> {
    /**
     * Builds the recipe(s) for a single block.
     */
    interface RecipeFactory {
        /**
         * Builds and writes the recipe(s) that produce the given block.
         *
         * @param key     The registry key of the block the recipe is being built for.
         * @param block   The block instance the recipe is being built for.
         * @param context The context to write the built recipe(s) to.
         */
        void buildRecipe(ResourceKey<Block> key, Block block, RecipeBuilder.Context context);
    }

    /**
     * Builder for {@link BlockRecipeTrait} instances.
     */
    interface Builder extends BlockTraitBuilder<Block, BlockRecipeTrait> {
        /**
         * Creates a trait that builds its block's recipe(s) with the given factory.
         *
         * @param recipeFactory The factory that builds the recipe(s) for the block this trait is attached to.
         * @return The new trait, or {@code null} outside of a datagen environment (see
         * {@link ModCore#isDatagen()}), since recipe generation is only needed during datagen.
         */
        @Nullable BlockRecipeTrait with(BlockRecipeTrait.RecipeFactory recipeFactory);
    }

    /**
     * The factory that builds the recipe(s) for the block this trait is attached to.
     *
     * @return The recipe factory.
     */
    BlockRecipeTrait.RecipeFactory recipeFactory();

    /**
     * Builds the recipe(s) for every block registered under the given mod that carries a
     * {@link BlockRecipeTrait}.
     *
     * @param modCore The mod whose registered blocks should be processed.
     * @param context The context to write the built recipes to.
     */
    static void bootstrapRecipes(ModCore modCore, RecipeBuilder.Context context) {
        BlockRecipeTraitBuilder.bootstrapRecipes(modCore, context, (r, i) -> true);
    }

    /**
     * Builds the recipe(s) for every block registered under the given mod that carries a
     * {@link BlockRecipeTrait} and matches the given filter.
     *
     * @param modCore The mod whose registered blocks should be processed.
     * @param context The context to write the built recipes to.
     * @param filter  Only blocks for which this predicate returns {@code true} have their recipe(s) built.
     */
    static void bootstrapRecipes(
            ModCore modCore,
            RecipeBuilder.Context context,
            BiPredicate<ResourceKey<Block>, Block> filter
    ) {
        BlockRecipeTraitBuilder.bootstrapRecipes(modCore, context, filter);
    }
}
