package de.ambertation.wover.recipe.mixin;

import de.ambertation.wover.entrypoint.LibWoverRecipe;
import de.ambertation.wover.recipe.impl.RecipeRuntimeProviderImpl;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;

import com.google.common.base.Stopwatch;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * 26.3 turned recipes into a datapack registry. {@code RecipeManager} is no longer a
 * {@code PreparableReloadListener} with a {@code prepare}/{@code apply} pair - it is constructed once per
 * world load from a {@link HolderLookup.Provider} and fills its {@link RecipeMap} right there, from
 * {@code registries.lookupOrThrow(Registries.RECIPE)}.
 * <p>
 * So the old {@code apply(RecipeMap, ResourceManager, ProfilerFiller)} target no longer exists. The hook is
 * the lookup on its way into {@code RecipeMap.create}, which is still early enough for everything that reads
 * recipes later: {@code MinecraftServer} only calls {@code finalizeRecipeLoading} (which derives the property
 * sets, stonecutter set and recipe displays) afterwards, so recipes injected here are picked up by all of it
 * exactly as datapack recipes are.
 * <p>
 * Deliberately <em>not</em> the tail of the constructor with a reassignment of {@code recipes}: Fabric's
 * recipe synchronisation attaches its serializer index to whatever {@code RecipeMap.create} returns, and a
 * map installed over the top of that carries no index, which kills player connect outright. See
 * {@link de.ambertation.wover.recipe.impl.RecipeLookups}.
 * <p>
 * The unlock advancements the same run produces are added by {@link ServerAdvancementManagerMixin}, which is
 * constructed a few instructions later in the very same {@code ReloadableServerResources} constructor.
 * <p>
 * Only the {@code RecipeManager} of that constructor is extended ({@link ReloadableServerResourcesMixin} marks
 * it). Any other one - Polymer's {@code FakeWorld} builds its own over a minimal registry set - keeps exactly
 * its datapack recipes.
 */
@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/crafting/RecipeMap;create(Lnet/minecraft/core/HolderLookup;)Lnet/minecraft/world/item/crafting/RecipeMap;"
            )
    )
    private HolderLookup<Recipe<?>> wover_addRuntimeRecipes(
            HolderLookup<Recipe<?>> datapackRecipes,
            @Local(argsOnly = true) HolderLookup.Provider registries
    ) {
        // Only the server's own RecipeManager gets the runtime recipes. Other mods build RecipeManagers for
        // their own purposes (Polymer's FakeWorld, over a registry set without e.g. minecraft:trim_pattern);
        // running the contributors there crashed the server and re-ran every BOOTSTRAP_RECIPES subscriber.
        if (!RecipeRuntimeProviderImpl.isServerReload(registries)) return datapackRecipes;

        Stopwatch stopwatch = Stopwatch.createStarted();
        final long count = datapackRecipes.listElements().count();
        final HolderLookup<Recipe<?>> withRuntime =
                RecipeRuntimeProviderImpl.loadedRecipes(datapackRecipes, registries);

        LibWoverRecipe.C.LOG.info(
                "Added {} recipes in {}ms", withRuntime.listElements().count() - count, stopwatch
                        .stop()
                        .elapsed()
                        .toMillis()
        );
        return withRuntime;
    }
}
