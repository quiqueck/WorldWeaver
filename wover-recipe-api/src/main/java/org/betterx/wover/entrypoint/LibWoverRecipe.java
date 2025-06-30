package org.betterx.wover.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;
import org.betterx.wover.datagen.impl.provider.AutoRecipeProvider;

import net.fabricmc.api.ModInitializer;

public class LibWoverRecipe implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-recipe", "wover");

    @Override
    public void onInitialize() {
        WoverDataGenEntryPoint.registerAutoProvider(AutoRecipeProvider::new);
    }
}