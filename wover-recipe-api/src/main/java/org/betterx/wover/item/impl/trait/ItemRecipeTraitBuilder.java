package org.betterx.wover.item.impl.trait;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverRecipe;
import org.betterx.wover.item.api.ItemRegistry;
import org.betterx.wover.item.api.trait.AbstractItemTraitBuilder;
import org.betterx.wover.item.api.trait.ItemRecipeTrait;
import org.betterx.wover.item.api.trait.ItemTraitKey;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.BiPredicate;
import org.jetbrains.annotations.Nullable;

public class ItemRecipeTraitBuilder extends AbstractItemTraitBuilder<Item, ItemRecipeTrait> implements ItemRecipeTrait.Builder {
    public static final ItemRecipeTrait.Builder BUILDER = new ItemRecipeTraitBuilder();

    private ItemRecipeTraitBuilder() {
        super(ItemTraitKey.of(LibWoverRecipe.C, "recipe"));
    }

    public @Nullable ItemRecipeTrait with(ItemRecipeTrait.RecipeFactory recipeFactory) {
        if (!ModCore.isDatagen()) return null;
        return new Trait(recipeFactory);
    }

    public static void bootstrapRecipes(
            ModCore modCore,
            RecipeBuilder.Context context,
            BiPredicate<ResourceKey<Item>, Item> filter
    ) {
        ItemRegistry
                .forMod(modCore)
                .allEntries().filter(e -> filter.test(e.getKey(), e.getValue())).forEach(e -> {
                    var runtimeTraits = BUILDER.getRuntimeTraits(e.getValue());
                    if (runtimeTraits == null) return;
                    runtimeTraits.forEach(trait -> trait.recipeFactory().buildRecipe(e.getKey(), e.getValue(), context));
                });
    }

    class Trait extends ItemTraitImpl<Item, ItemRecipeTrait> implements ItemRecipeTrait {
        public final ItemRecipeTrait.RecipeFactory recipeFactory;

        Trait(ItemRecipeTrait.RecipeFactory recipeFactory) {
            this.recipeFactory = recipeFactory;
        }

        @Override
        public ItemTraitKey key() {
            return traitKey;
        }

        @Override
        public ItemRecipeTrait forRuntime() {
            return this;
        }

        @Override
        public RecipeFactory recipeFactory() {
            return this.recipeFactory;
        }
    }
}
