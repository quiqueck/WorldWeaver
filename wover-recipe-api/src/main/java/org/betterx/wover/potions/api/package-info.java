/**
 * Helpers for registering potions and for hooking into vanilla's brewing-stand recipe bootstrap.
 * <p>
 * {@link org.betterx.wover.potions.api.PotionManager} registers new {@link net.minecraft.world.item.alchemy.Potion}
 * entries, and exposes {@link org.betterx.wover.potions.api.PotionManager#BOOTSTRAP_POTIONS}, an
 * {@link org.betterx.wover.events.api.Event} that mods can subscribe to (via
 * {@link org.betterx.wover.potions.api.OnBootstrapPotions}) to add custom potion mixes, container conversions,
 * and brewing ingredients to the vanilla brewing stand.
 */
package org.betterx.wover.potions.api;
