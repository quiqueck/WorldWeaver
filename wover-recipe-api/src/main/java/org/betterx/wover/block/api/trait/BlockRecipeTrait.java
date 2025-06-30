package org.betterx.wover.block.api.trait;

import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverRecipe;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import java.util.function.BiPredicate;
import org.jetbrains.annotations.Nullable;

public class BlockRecipeTrait extends BlockTrait<Block, BlockRecipeTrait.RuntimeTrait> {
    public static final BlockRecipeTrait.Builder BUILDER = new BlockRecipeTrait.Builder();

    public static class Builder extends BlockTrait.TraitBuilder {
        private Builder() {
            super(BlockTraitKey.of(LibWoverRecipe.C, "recipe_generator"));
        }

        public @Nullable BlockRecipeTrait with(BlockRecipeTrait.RecipeFactory recipeFactory) {
            if (!ModCore.isDatagen()) return null;
            return new BlockRecipeTrait(recipeFactory);
        }
    }

    public static class RuntimeTrait extends RuntimeBlockTrait<Block, RuntimeTrait> {
        public final BlockRecipeTrait.RecipeFactory recipeFactory;

        private RuntimeTrait(BlockRecipeTrait.RecipeFactory recipeFactory) {
            super(BUILDER.ID);
            this.recipeFactory = recipeFactory;
        }
    }

    public interface RecipeFactory {
        void buildRecipe(ResourceKey<Block> key, Block block, RecipeBuilder.Context context);
    }

    public final BlockRecipeTrait.RecipeFactory recipeFactory;

    BlockRecipeTrait(BlockRecipeTrait.RecipeFactory recipeFactory) {
        super(BUILDER.ID);
        this.recipeFactory = recipeFactory;
    }

    @Override
    public BlockRecipeTrait.RuntimeTrait forRuntime() {
        return new BlockRecipeTrait.RuntimeTrait(recipeFactory);
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
                    var runtimeTraits = BlockTrait.<Block, BlockRecipeTrait.RuntimeTrait>getRuntimeTraits(
                            e.getValue(),
                            BUILDER.ID
                    );
                    if (runtimeTraits == null) return;
                    runtimeTraits.forEach(trait -> {
                        try {
                            trait.recipeFactory.buildRecipe(e.getKey(), e.getValue(), context);
                        } catch (Exception ex) {
                            LibWoverRecipe.C.LOG.error("Failed to build recipe for block: " + e.getKey(), ex);
                        }
                    });
                });
    }
}
