package org.betterx.wover.recipe.api;

import org.betterx.wover.events.api.Subscriber;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;

/**
 * Used when bootstrapping recipes.
 */
public interface OnBootstrapRecipes extends Subscriber {
    /**
     * Called when recipes are loaded
     *
     * @param items    The holder getter for items.
     * @param provider The recipe provider.
     * @param context  The bootstrap context.
     */
    void bootstrap(HolderGetter<Item> items, RecipeProvider provider, RecipeOutput context);

}
