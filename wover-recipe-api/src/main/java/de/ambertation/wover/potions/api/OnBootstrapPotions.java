package de.ambertation.wover.potions.api;

import de.ambertation.wover.events.api.Subscriber;

/**
 * Used when bootstrapping brewing (potion mix/container/ingredient) recipes.
 * <p>
 * Subscribers are invoked while the brewing recipes for the running world are being assembled, letting mods
 * register additional brewing stand recipes (potion mixes, container conversions, and brewing ingredients) on
 * top of the vanilla ones. Subscribe via {@link de.ambertation.wover.potions.api.PotionManager#BOOTSTRAP_POTIONS}.
 * <p>
 * <b>26.3:</b> the parameter changed from {@code PotionBrewing.Builder} to {@link BrewingBuilder} because
 * {@code PotionBrewing} no longer exists - brewing became part of the recipe system. {@link BrewingBuilder}
 * offers the same four operations under the same names, so subscribers only need to change the parameter type.
 */
public interface OnBootstrapPotions extends Subscriber {
    /**
     * Called while the brewing recipes are being assembled.
     *
     * @param builder The brewing builder to add mixes/containers/ingredients to.
     */
    void bootstrap(BrewingBuilder builder);
}
