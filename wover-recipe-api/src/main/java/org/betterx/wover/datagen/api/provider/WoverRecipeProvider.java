package org.betterx.wover.datagen.api.provider;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverDataProvider;
import org.betterx.wover.recipe.api.RecipeBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;

import java.util.concurrent.CompletableFuture;

public abstract class WoverRecipeProvider implements WoverDataProvider<FabricRecipeProvider> {
    /**
     * The title of the provider. Mainly used for logging.
     */
    public final String title;

    /**
     * The ModCore instance of the Mod that is providing this instance.
     */
    protected final ModCore modCore;

    public WoverRecipeProvider(
            ModCore modCore,
            String title
    ) {
        this.title = title;
        this.modCore = modCore;
    }


    /**
     * Called, when the Recipes need to be created and added
     *
     * @param ctx The context to add the elements to.
     */
    protected abstract void bootstrap(
            RecipeBuilder.Context ctx
    );

    @Override
    public FabricRecipeProvider getProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new FabricRecipeProvider(output, registriesFuture) {
            @Override
            protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
                return new RecipeProvider(registryLookup, exporter) {
                    @Override
                    public void buildRecipes() {
                        bootstrap(new RecipeBuilder.Context(registryLookup, this, exporter));
                    }
                };
            }

            @Override
            public String getName() {
                return title;
            }
        };
    }
}
