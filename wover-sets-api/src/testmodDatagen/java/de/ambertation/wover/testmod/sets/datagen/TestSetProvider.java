package de.ambertation.wover.testmod.sets.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.WoverRecipeProvider;
import de.ambertation.wover.recipe.api.RecipeBuilder;

public class TestSetProvider extends WoverRecipeProvider {
    public TestSetProvider(ModCore modCore) {
        super(modCore, "sets");
    }

    @Override
    protected void bootstrap(RecipeBuilder.Context context) {

    }
}
