package de.ambertation.wover.block.impl.trait;

import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.trait.AbstractBlockTraitBuilder;
import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverRecipe;
import de.ambertation.wover.recipe.api.RecipeBuilder;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;

import java.util.function.BiPredicate;
import org.jetbrains.annotations.Nullable;


public class BlockRecipeTraitBuilder extends AbstractBlockTraitBuilder<Block, BlockRecipeTrait> implements BlockRecipeTrait.Builder {
    public static final BlockRecipeTrait.Builder BUILDER = new BlockRecipeTraitBuilder();

    private BlockRecipeTraitBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverRecipe.C, "recipe"));
    }

    public @Nullable BlockRecipeTrait with(BlockRecipeTrait.RecipeFactory recipeFactory) {
        if (!ModCore.isDatagen()) return null;
        return new Trait(recipeFactory);
    }

    public static void bootstrapRecipes(
            ModCore modCore,
            RecipeBuilder.Context context,
            BiPredicate<ResourceKey<Block>, Block> filter
    ) {
        BlockRegistry
                .forMod(modCore)
                .allEntries().filter(e -> filter.test(e.getKey(), e.getValue())).forEach(e -> {
                    var runtimeTraits = BUILDER.getRuntimeTraits(e.getValue());
                    if (runtimeTraits == null) return;
                    runtimeTraits.forEach(trait -> {
                        try {
                            trait.recipeFactory().buildRecipe(e.getKey(), e.getValue(), context);
                        } catch (Exception ex) {
                            LibWoverRecipe.C.LOG.error("Failed to build recipe for block: " + e.getKey(), ex);
                        }
                    });
                });
    }

    class Trait extends BlockTraitImpl<Block, BlockRecipeTrait> implements BlockRecipeTrait {
        public final BlockRecipeTrait.RecipeFactory recipeFactory;

        Trait(BlockRecipeTrait.RecipeFactory recipeFactory) {
            this.recipeFactory = recipeFactory;
        }

        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public RecipeFactory recipeFactory() {
            return this.recipeFactory;
        }

        @Override
        public BlockRecipeTrait forRuntime() {
            return this;
        }
    }
}
