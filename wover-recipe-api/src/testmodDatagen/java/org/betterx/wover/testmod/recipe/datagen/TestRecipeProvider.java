package org.betterx.wover.testmod.recipe.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverRecipeProvider;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.testmod.recipe.TestEquipmentSet;

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

        TestEquipmentSet.INSTANCE.buildRecipes(context);
    }
}
