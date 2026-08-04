package de.ambertation.wover.testmod.recipe.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.provider.WoverRecipeProvider;
import de.ambertation.wover.recipe.api.RecipeBuilder;

import net.minecraft.world.item.Items;

public class TestRecipeProvider extends WoverRecipeProvider {
    public TestRecipeProvider(ModCore modCore) {
        super(modCore, "recipes");
    }

    @Override
    protected void bootstrap(RecipeBuilder.Context context) {
        RecipeBuilder.crafting(modCore.mk("test_recipe"), Items.BEDROCK)
                     .addMaterial('D', Items.DIAMOND)
                     .addMaterial('B', Items.BASALT)
                     .shape(" D ", "DBD", " D ")
                     .outputCount(2)
                     .showNotification()
                     .build(context);
    }
}
