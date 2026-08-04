package de.ambertation.wover.recipe.api;

import de.ambertation.wover.events.api.Subscriber;

/**
 * Used when bootstrapping recipes.
 */
public interface OnBootstrapRecipes extends Subscriber {
    /**
     * Called when recipes are loaded
     *
     * @param context The bootstrap context.
     */
    void bootstrap(RecipeBuilder.Context context);

}
