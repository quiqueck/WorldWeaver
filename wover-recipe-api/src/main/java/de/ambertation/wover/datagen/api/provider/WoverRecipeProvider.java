package de.ambertation.wover.datagen.api.provider;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverDataProvider;
import de.ambertation.wover.recipe.api.RecipeBuilder;

import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.crafting.Recipe;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;

import java.util.concurrent.CompletableFuture;

/**
 * Base class for datagen providers that generate recipe JSON files with {@link RecipeBuilder}.
 * <p>
 * Subclass this, implement {@link #bootstrap(RecipeBuilder.Context)} to build and write the desired recipes,
 * and register the subclass with a {@link de.ambertation.wover.datagen.api.PackBuilder} (typically from
 * {@code onInitializeProviders} in a {@link de.ambertation.wover.datagen.api.WoverDataGenEntryPoint}).
 */
public abstract class WoverRecipeProvider implements WoverDataProvider<FabricRecipeProvider> {
    /**
     * The title of the provider. Mainly used for logging.
     */
    public final String title;

    /**
     * The ModCore instance of the Mod that is providing this instance.
     */
    public final ModCore modCore;

    /**
     * Creates a new provider.
     *
     * @param modCore The mod this provider generates recipes for.
     * @param title   The title of the provider, mainly used for logging.
     */
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
            FabricPackOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new FabricRecipeProvider(output, registriesFuture) {
            // 26.3: recipes AND advancements are datapack registries now, so Fabric no longer hands us a
            // RecipeOutput to export into - it hands us the BootstrapContext of each registry and writes
            // whatever ends up in them. RecipeProvider builds the matching RecipeOutput itself (it splits
            // every accept(key, recipe, advancement) call across the two contexts) and exposes it as its
            // protected `output` field, so that is what RecipeBuilder.Context is fed. Going through the
            // provider's own output rather than wrapping the contexts by hand keeps the unlock advancements
            // that RecipeUnlockAdvancementBuilder produces flowing into minecraft:advancement, exactly as
            // vanilla's own providers do.
            @Override
            protected RecipeProvider createRecipeProvider(
                    HolderLookup.Provider registryLookup,
                    BootstrapContext<Recipe<?>> recipeOutput,
                    BootstrapContext<Advancement> advancementOutput
            ) {
                return new RecipeProvider(recipeOutput, advancementOutput) {
                    @Override
                    public void buildRecipes() {
                        bootstrap(new RecipeBuilder.Context(registryLookup, this, this.output));
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
