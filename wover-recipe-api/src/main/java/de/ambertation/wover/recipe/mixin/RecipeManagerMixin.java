package de.ambertation.wover.recipe.mixin;

import de.ambertation.wover.entrypoint.LibWoverRecipe;
import de.ambertation.wover.recipe.impl.RecipeRuntimeProviderImpl;

import net.minecraft.core.HolderLookup;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;

import com.google.common.base.Stopwatch;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects the recipes contributed through {@code RecipeBuilder.BOOTSTRAP_RECIPES} into the recipes the
 * datapacks provided.
 * <p>
 * The unlock advancements the same run produces are added by {@link ServerAdvancementManagerMixin}, which is
 * applied later in the very same reload.
 */
@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Shadow
    private RecipeMap recipes;

    // The provider this RecipeManager was built with; handed on so the advancement half of the contribution
    // can be matched to this exact reload. See RecipeRuntimeProviderImpl#pendingAdvancements.
    @Shadow
    @Final
    private HolderLookup.Provider registries;

    @Inject(method = "apply(Lnet/minecraft/world/item/crafting/RecipeMap;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At("TAIL"))
    void wover_apply(
            RecipeMap recipeMap, ResourceManager resourceManager, ProfilerFiller profilerFiller, CallbackInfo ci
    ) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        final int count = recipeMap.values().size();
        this.recipes = RecipeRuntimeProviderImpl.loadedRecipes(recipeMap, this.registries);

        LibWoverRecipe.C.LOG.info(
                "Added {} recipes in {}ms", this.recipes.values().size() - count, stopwatch
                        .stop()
                        .elapsed()
                        .toMillis()
        );
    }
}
