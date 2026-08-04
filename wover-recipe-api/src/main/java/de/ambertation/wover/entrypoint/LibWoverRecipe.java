package de.ambertation.wover.entrypoint;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover.datagen.impl.provider.AutoRecipeProvider;

import net.fabricmc.api.ModInitializer;

public class LibWoverRecipe implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-recipe", "wover");

    @Override
    public void onInitialize() {
        WoverDataGenEntryPoint.registerAutoProvider(AutoRecipeProvider::new);
    }
}