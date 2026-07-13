package org.betterx.wover.potions.api;

import org.betterx.wover.events.api.Subscriber;

import net.minecraft.world.item.alchemy.PotionBrewing;

/**
 * Used when bootstrapping brewing (potion mix/container/ingredient) recipes.
 * <p>
 * Subscribers are invoked while vanilla builds its {@link PotionBrewing} instance, letting mods register
 * additional brewing stand recipes (potion mixes, container conversions, and brewing ingredients) on top of the
 * vanilla ones. Subscribe via {@link org.betterx.wover.potions.api.PotionManager#BOOTSTRAP_POTIONS}.
 */
public interface OnBootstrapPotions extends Subscriber {
    /**
     * Called while the vanilla {@link PotionBrewing} is being built.
     *
     * @param builder The brewing builder to add mixes/containers/ingredients to.
     */
    void bootstrap(PotionBrewing.Builder builder);
}
