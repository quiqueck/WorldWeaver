package de.ambertation.wover.testmod.entrypoint;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.potions.api.PotionManager;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.tabs.api.CreativeTabs;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;

import net.fabricmc.api.ModInitializer;

public class TestModWoverRecipe implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-recipe-testmod");

    /**
     * A potion that only exists so the BOOTSTRAP_POTIONS event can be exercised at runtime by
     * {@code RecipeGameTest}. 26.3 turned brewing into ordinary recipes, so the event's contributions
     * end up in the RecipeManager alongside everything else.
     */
    public static final Holder<Potion> TEST_POTION =
            PotionManager.registerPotion(C, "test_potion", MobEffects.LUCK, 3600);

    @Override
    public void onInitialize() {
        // Brewing a nether star on an awkward potion yields the test potion. Nothing in vanilla brews
        // with a nether star, so every recipe this produces is new.
        PotionManager.BOOTSTRAP_POTIONS.subscribe(builder -> builder.addStartMix(Items.NETHER_STAR, TEST_POTION));

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