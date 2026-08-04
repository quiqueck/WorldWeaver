package de.ambertation.wover.testmod.entrypoint;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.tabs.api.CreativeTabs;

import net.minecraft.world.item.Items;

import net.fabricmc.api.ModInitializer;

public class TestModWoverRecipe implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-recipe-testmod");

    @Override
    public void onInitialize() {
        RecipeBuilder.BOOTSTRAP_RECIPES.subscribe((ctx) -> {
            RecipeBuilder.crafting(C.mk("test_diamoan_recipe"), Items.DIAMOND)
                         .addMaterial('C', Items.COAL_BLOCK)
                         .addMaterial('I', Items.IRON_BLOCK)
                         .shape("III", "ICI", "III")
                         .outputCount(1)
                         .showNotification()
                         .build(ctx);
        });


        CreativeTabs.start(C)
                    .createItemOnlyTab(Items.TURTLE_SCUTE)
                    .buildAndAdd()
                    .processRegistries()
                    .registerAllTabs();
    }
}