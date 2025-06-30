package org.betterx.wover.testmod.sets.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverRecipeProvider;
import org.betterx.wover.recipe.api.RecipeBuilder;

public class TestSetProvider extends WoverRecipeProvider {
    public TestSetProvider(ModCore modCore) {
        super(modCore, "sets");
    }

    @Override
    protected void bootstrap(RecipeBuilder.Context context) {

    }
}
