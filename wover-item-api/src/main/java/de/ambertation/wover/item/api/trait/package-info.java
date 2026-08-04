/**
 * A trait system for attaching reusable, composable behavior to item definitions.
 *
 * <p>An {@link de.ambertation.wover.item.api.trait.ItemTrait} is a build-time configuration object that can be
 * added to an {@link de.ambertation.wover.item.api.ItemDefinition} via
 * {@link de.ambertation.wover.item.api.ItemDefinition#addTrait(de.ambertation.wover.item.api.trait.ItemTrait) addTrait(...)}.
 * When the item is built, every added trait gets a chance to configure the definition (add tags, properties, etc.)
 * and may optionally contribute a {@link de.ambertation.wover.item.api.trait.RuntimeItemTrait} that stays attached to
 * the built item (if it implements {@link de.ambertation.wover.item.api.trait.ItemWithTraits}), so the item's traits
 * can be inspected at runtime.
 *
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link de.ambertation.wover.item.api.trait.ItemTrait} - build-time trait configuration</li>
 *   <li>{@link de.ambertation.wover.item.api.trait.RuntimeItemTrait} - the runtime-visible part of a trait</li>
 *   <li>{@link de.ambertation.wover.item.api.trait.ItemTraitKey} - the unique identifier shared by a trait kind</li>
 *   <li>{@link de.ambertation.wover.item.api.trait.ItemTraitBuilder} - a factory for a specific kind of trait</li>
 *   <li>{@link de.ambertation.wover.item.api.trait.AbstractItemTraitBuilder} - base class for trait builders</li>
 *   <li>{@link de.ambertation.wover.item.api.trait.ItemWithTraits} - implemented by items that carry runtime traits</li>
 *   <li>{@link de.ambertation.wover.item.api.trait.ItemTraitLookup} - queries whether a definition already has a trait</li>
 *   <li>{@link de.ambertation.wover.item.api.trait.GenericItemTrait} - convenience trait type for any {@link net.minecraft.world.item.Item}</li>
 * </ul>
 *
 * <p>Other Wover modules (e.g. {@code wover-sets-api}, {@code wover-recipe-api}) add concrete trait kinds to this
 * package, such as boat rendering, elytra behavior, fireproofing, or recipe-driven crafting behavior.
 *
 * @see de.ambertation.wover.item.api.ItemDefinition
 */
package de.ambertation.wover.item.api.trait;
