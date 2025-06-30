package org.betterx.wover.datagen.impl.provider;

import org.betterx.wover.block.api.trait.BlockRecipeTrait;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverAutoProvider;
import org.betterx.wover.datagen.api.provider.WoverRecipeProvider;
import org.betterx.wover.item.api.trait.ItemRecipeTrait;
import org.betterx.wover.recipe.api.RecipeBuilder;

public class AutoRecipeProvider extends WoverRecipeProvider implements WoverAutoProvider {
    public AutoRecipeProvider(ModCore modCore) {
        super(modCore, "Auto Recipe Provider");
    }

    @Override
    protected void bootstrap(RecipeBuilder.Context ctx) {
        ItemRecipeTrait.bootstrapRecipes(this.modCore, ctx);
        BlockRecipeTrait.bootstrapRecipes(this.modCore, ctx);
    }
}
