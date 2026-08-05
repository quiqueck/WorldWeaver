package de.ambertation.wover.recipe.impl;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;

import java.util.Collection;
import org.jetbrains.annotations.ApiStatus;

/**
 * The loader-specific half of {@link de.ambertation.wover.recipe.api.SyncedRecipes}, and the only
 * place in this module that knows recipe synchronisation is a Fabric API feature.
 * <p>
 * Both calls delegate to {@code fabric-recipe-api-v1}, which already solves the awkward parts: the
 * client advertises the serializers it knows during the configuration phase, and the server answers
 * with the matching recipes, chunked so a large recipe set does not overflow a single payload.
 * Re-implementing that over a custom packet would buy nothing today - every other network path in
 * this stack is Fabric-backed too - so the portability is kept where it is cheap instead: in the
 * public API's signatures, which name no loader types.
 * <p>
 * Note the asymmetry in {@link #allOfType}: on the server Fabric's view is backed by the full
 * {@code RecipeMap}, so it answers for <em>any</em> type, registered for sync or not. Only the
 * client half is limited to what {@link #register} declared.
 */
@ApiStatus.Internal
public final class SyncedRecipesImpl {
    private SyncedRecipesImpl() {
    }

    public static void register(RecipeSerializer<?> serializer) {
        RecipeSynchronization.synchronizeRecipeSerializer(serializer);
    }

    public static <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> allOfType(
            Level level,
            RecipeType<T> type
    ) {
        return level.recipeAccess().getSynchronizedRecipes().getAllOfType(type);
    }
}
