package org.betterx.wover.testmod.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.recipe.api.RecipeBuilder;
import org.betterx.wover.tabs.api.CreativeTabs;
import org.betterx.wover.testmod.recipe.TestEquipmentSet;

import net.minecraft.world.item.Items;

import net.fabricmc.api.ModInitializer;

public class TestModWoverRecipe implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-recipe-testmod");

    @Override
    public void onInitialize() {
        TestEquipmentSet.ensureStaticInit();

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