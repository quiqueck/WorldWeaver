package de.ambertation.wover.testmod.entrypoint;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.recipe.api.RecipeBuilder;
import de.ambertation.wover.tabs.api.CreativeTabs;

import net.minecraft.world.item.Items;

import java.util.concurrent.atomic.AtomicInteger;

import net.fabricmc.api.ModInitializer;

public class TestModWoverRecipe implements ModInitializer {
    // ModCore for the TestMod. TestMod's do not share the wover namespace,
    // but (like other Mods that include Wover) have a unique one
    public static final ModCore C = ModCore.create("wover-recipe-testmod");

    /**
     * How often {@code RecipeBuilder.BOOTSTRAP_RECIPES} reached this testmod. {@code RecipeGameTest} uses it to
     * prove that a {@code RecipeManager} built outside the server's resource reload (Polymer's
     * {@code FakeWorld} does that) does not re-run the runtime recipe contributors.
     */
    public static final AtomicInteger BOOTSTRAP_RECIPE_RUNS = new AtomicInteger();

    @Override
    public void onInitialize() {
        RecipeBuilder.BOOTSTRAP_RECIPES.subscribe((ctx) -> {
            BOOTSTRAP_RECIPE_RUNS.incrementAndGet();
            RecipeBuilder.crafting(C.mk("test_diamoan_recipe"), Items.DIAMOND)
                         .addMaterial('C', Items.COAL_BLOCK)
                         .addMaterial('I', Items.IRON_BLOCK)
                         .shape("III", "ICI", "III")
                         .outputCount(1)
                         .showNotification()
                         .build(ctx);

            // cookableFood must cook in a furnace, a smoker and on a campfire, like vanilla food
            // (checked by RecipeGameTest#cookableFoodCooksInFurnaceSmokerAndCampfire).
            RecipeBuilder.cookableFood(C.mk("test_cookable_food"), Items.COOKED_COD)
                         .input(Items.TROPICAL_FISH)
                         .build(ctx);
        });


        CreativeTabs.start(C)
                    .createItemOnlyTab(Items.TURTLE_SCUTE)
                    .buildAndAdd()
                    .processRegistries()
                    .registerAllTabs();
    }
}