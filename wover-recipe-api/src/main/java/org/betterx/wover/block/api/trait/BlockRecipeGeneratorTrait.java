package org.betterx.wover.block.api.trait;

import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverRecipe;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import java.util.function.BiPredicate;
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

    public static class RuntimeTrait extends RuntimeBlockTrait<Block, RuntimeTrait> {
        public final BlockRecipeGeneratorTrait.RecipeFactory recipeFactory;

        private RuntimeTrait(BlockRecipeGeneratorTrait.RecipeFactory recipeFactory) {
            super(BUILDER.ID);
            this.recipeFactory = recipeFactory;
        }
    }

    public interface RecipeFactory {
        void buildRecipe(ResourceKey<Block> key, Block block, RecipeBuilder.Context context);
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
        bootstrapRecipes(modCore, context, (r, i) -> true);
    }

    public static void bootstrapRecipes(
            ModCore modCore,
            RecipeBuilder.Context context,
            BiPredicate<ResourceKey<Block>, Block> filter
    ) {
        BlockRegistry
                .forMod(modCore)
                .allEntries().filter(e -> filter.test(e.getKey(), e.getValue())).forEach(e -> {
                    var runtimeTraits = BlockTrait.<Block, BlockRecipeGeneratorTrait.RuntimeTrait>getRuntimeTraits(
                            e.getValue(),
                            BUILDER.ID
                    );
                    if (runtimeTraits == null) return;
                    runtimeTraits.forEach(trait -> trait.recipeFactory.buildRecipe(e.getKey(), e.getValue(), context));
                });
    }
}
