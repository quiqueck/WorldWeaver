/**
 * Client-only item traits (see {@link de.ambertation.wover.item.api.trait}) for rendering-related behavior.
 *
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link de.ambertation.wover.item.api.client.trait.ItemModelTrait} - the client escape hatch: attaches a
 *   code-driven client item-model factory, generated automatically during model datagen</li>
 * </ul>
 * The boat-renderer trait is now a common binding (see {@link de.ambertation.wover.item.api.render}), applied at
 * client init by its applier.
 *
 * @see de.ambertation.wover.item.api.trait
 */
package de.ambertation.wover.item.api.client.trait;
