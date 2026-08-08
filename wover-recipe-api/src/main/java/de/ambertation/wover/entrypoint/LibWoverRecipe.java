package de.ambertation.wover.entrypoint;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover.datagen.impl.provider.AutoRecipeProvider;
import de.ambertation.wover.recipe.impl.SyncedRecipesImpl;

import net.fabricmc.api.ModInitializer;

public class LibWoverRecipe implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-recipe", "wover");

    @Override
    public void onInitialize() {
        WoverDataGenEntryPoint.registerAutoProvider(AutoRecipeProvider::new);
        // Common, not server-only: the same push is what feeds the client behind a singleplayer world.
        SyncedRecipesImpl.initialize();
    }
}