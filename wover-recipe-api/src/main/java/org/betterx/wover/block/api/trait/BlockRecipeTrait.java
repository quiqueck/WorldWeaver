package org.betterx.wover.block.api.trait;

import org.betterx.wover.block.impl.trait.BlockRecipeTraitBuilder;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import java.util.function.BiPredicate;
import org.jetbrains.annotations.Nullable;

public interface BlockRecipeTrait extends BlockTrait<Block, BlockRecipeTrait>, RuntimeBlockTrait<Block, BlockRecipeTrait> {
    interface RecipeFactory {
        void buildRecipe(ResourceKey<Block> key, Block block, RecipeBuilder.Context context);
    }

    interface Builder extends TraitBuilder<Block, BlockRecipeTrait> {
        @Nullable BlockRecipeTrait with(BlockRecipeTrait.RecipeFactory recipeFactory);
    }

    BlockRecipeTrait.RecipeFactory recipeFactory();

    static void bootstrapRecipes(ModCore modCore, RecipeBuilder.Context context) {
        BlockRecipeTraitBuilder.bootstrapRecipes(modCore, context, (r, i) -> true);
    }

    static void bootstrapRecipes(
            ModCore modCore,
            RecipeBuilder.Context context,
            BiPredicate<ResourceKey<Block>, Block> filter
    ) {
        BlockRecipeTraitBuilder.bootstrapRecipes(modCore, context, filter);
    }
}
