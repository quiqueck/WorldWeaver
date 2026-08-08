package de.ambertation.wover.recipe.api;

import de.ambertation.wover.recipe.impl.SyncedRecipesImpl;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Collection;

/**
 * Reads the recipes of a custom recipe type from either side of the connection.
 * <p>
 * Vanilla stopped handing the client a full recipe list: {@code Level.recipeAccess()} resolves to a
 * {@code RecipeManager} on the server but to recipe-book display data on the client, from which a
 * modded recipe type cannot be read at all. Anything that wants to show a custom recipe in a GUI -
 * a recipe-viewer plugin, an in-world recipe book - therefore has to have those recipes sent to it
 * first. Reaching into the integrated server's {@code RecipeManager} instead is not a substitute; it
 * works in singleplayer and silently shows nothing on a dedicated server.
 * <p>
 * Two calls, both made from mod initialisation on <em>both</em> sides:
 * <ol>
 *     <li>{@link #register(RecipeSerializer)} once per custom recipe serializer, and</li>
 *     <li>{@link #allOfType(Level, RecipeType)} wherever the recipes are needed.</li>
 * </ol>
 * <p>
 * This is a query API on purpose - there is no "recipes arrived" event to subscribe to. The recipes
 * are in place before a player can interact with anything, so callers can simply ask when they need
 * them. It is unrelated to {@link OnBootstrapRecipes}, which is about <em>contributing</em> recipes
 * into a running server, not about who can read them.
 * <p>
 * No loader types appear in this class; everything loader-specific lives in
 * {@link SyncedRecipesImpl}, which is the single file a port has to replace.
 */
public final class SyncedRecipes {
    private SyncedRecipes() {
    }

    /**
     * Declares that recipes using {@code serializer} should be readable on the client.
     * <p>
     * Call this from common mod initialisation, so that it runs on the client and the server alike -
     * the two negotiate which serializers they both know while the connection is being configured,
     * and a serializer registered later than that misses the exchange.
     *
     * @param serializer the serializer of the custom recipe type
     */
    public static void register(RecipeSerializer<?> serializer) {
        SyncedRecipesImpl.register(serializer);
    }

    /**
     * Every recipe of {@code type} that {@code level} knows about.
     * <p>
     * On a server that is every loaded recipe of the type. On a client it is the recipes that were
     * sent over, which means the type's serializer must have been passed to
     * {@link #register(RecipeSerializer)}; without that the result is empty rather than an error.
     *
     * @param level the level to read from - either side
     * @param type  the recipe type to collect
     * @return the matching recipes, empty if the type is unknown here
     */
    public static <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> allOfType(
            Level level,
            RecipeType<T> type
    ) {
        return SyncedRecipesImpl.allOfType(level, type);
    }
}
