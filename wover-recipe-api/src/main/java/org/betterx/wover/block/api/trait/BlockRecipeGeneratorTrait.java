package org.betterx.wover.block.api.trait;

import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverRecipe;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.world.level.block.Block;

import java.util.function.Predicate;
import org.jetbrains.annotations.Nullable;

public class BlockRecipeGeneratorTrait extends BlockTrait<Block, BlockRecipeGeneratorTrait.RuntimeTrait> {
    public static final BlockRecipeGeneratorTrait.Builder BUILDER = new BlockRecipeGeneratorTrait.Builder();

    public static class Builder extends BlockTrait.TraitBuilder {
        private Builder() {
            super(BlockTraitKey.of(LibWoverRecipe.C, "recipe_generator"));
        }

        public @Nullable BlockRecipeGeneratorTrait with(BlockRecipeGeneratorTrait.RecipeFactory recipeFactory) {
            if (!ModCore.isDatagen()) return null;
            return new BlockRecipeGeneratorTrait(recipeFactory);
        }
    }

    public static class RuntimeTrait extends BlockTrait.RuntimeTrait<Block, BlockRecipeGeneratorTrait.RuntimeTrait> {
        public final BlockRecipeGeneratorTrait.RecipeFactory recipeFactory;

        private RuntimeTrait(BlockRecipeGeneratorTrait.RecipeFactory recipeFactory) {
            super(BUILDER.ID);
            this.recipeFactory = recipeFactory;
        }
    }

    public interface RecipeFactory {
        void buildRecipe(Block block, RecipeBuilder.Context context);
    }

    public final BlockRecipeGeneratorTrait.RecipeFactory recipeFactory;

    BlockRecipeGeneratorTrait(BlockRecipeGeneratorTrait.RecipeFactory recipeFactory) {
        super(BUILDER.ID);
        this.recipeFactory = recipeFactory;
    }

    @Override
    public BlockRecipeGeneratorTrait.RuntimeTrait forRuntime() {
        return new BlockRecipeGeneratorTrait.RuntimeTrait(recipeFactory);
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

    public static void bootstrapRecipes(ModCore modCore, RecipeBuilder.Context context, Predicate<Block> filter) {
        BlockRegistry
                .forMod(modCore)
                .allBlocks().filter(filter).forEach(item -> {
                    BlockTrait.<Block, BlockRecipeGeneratorTrait.RuntimeTrait>getRuntimeTraits(item, BUILDER.ID)
                              .forEach(trait -> trait.recipeFactory.buildRecipe(item, context));

                });
    }
}
