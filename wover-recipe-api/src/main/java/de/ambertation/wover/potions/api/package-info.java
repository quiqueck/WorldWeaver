/**
 * Helpers for registering potions and for hooking into vanilla's brewing-stand recipe bootstrap.
 * <p>
 * {@link de.ambertation.wover.potions.api.PotionManager} registers new {@link net.minecraft.world.item.alchemy.Potion}
 * entries, and exposes {@link de.ambertation.wover.potions.api.PotionManager#BOOTSTRAP_POTIONS}, an
 * {@link de.ambertation.wover.events.api.Event} that mods can subscribe to (via
 * {@link de.ambertation.wover.potions.api.OnBootstrapPotions}) to add custom potion mixes, container conversions,
 * and brewing ingredients to the vanilla brewing stand.
 * <p>
 * As of 26.3 brewing is part of the recipe system rather than a separate {@code PotionBrewing} table, so the
 * event hands subscribers a {@link de.ambertation.wover.potions.api.BrewingBuilder} and its contributions are
 * injected as {@code minecraft:brewing} recipes.
 */
package de.ambertation.wover.potions.api;
