package de.ambertation.wover.testmod.recipe.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover.testmod.entrypoint.TestModWoverRecipe;

public class TestModWoverRecipeDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addProvider(TestRecipeProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return TestModWoverRecipe.C;
    }

}
