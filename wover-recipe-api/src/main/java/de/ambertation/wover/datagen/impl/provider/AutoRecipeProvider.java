package de.ambertation.wover.datagen.impl.provider;

import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverAutoProvider;
import de.ambertation.wover.datagen.api.provider.WoverRecipeProvider;
import de.ambertation.wover.item.api.trait.ItemRecipeTrait;
import de.ambertation.wover.recipe.api.RecipeBuilder;

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
