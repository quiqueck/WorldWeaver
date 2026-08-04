/**
 * Client-only block traits (see {@link de.ambertation.wover.block.api.trait}) for model generation and rendering.
 *
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link de.ambertation.wover.block.api.client.trait.BlockModelTrait} - the client escape hatch: attaches a
 *   code-driven client model factory to a block, generated automatically during model datagen</li>
 *   <li>{@link de.ambertation.wover.block.api.client.trait.ClientBlockTraits} - client-side aliases of the common
 *   render builders ({@link de.ambertation.wover.block.api.render.BlockRenderTraits}), plus the model escape hatch</li>
 * </ul>
 * The render-layer / chest-renderer / boat-renderer traits are now common bindings (see
 * {@link de.ambertation.wover.block.api.render} and {@link de.ambertation.wover.item.api.render}), applied at client
 * init by their appliers.
 *
 * @see de.ambertation.wover.block.api.client.model
 */
package de.ambertation.wover.block.api.client.trait;
