/**
 * Client-only block traits (see {@link org.betterx.wover.block.api.trait}) for model generation and rendering.
 *
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link org.betterx.wover.block.api.client.trait.BlockModelTrait} - attaches a code-driven client model
 *   factory to a block, generated automatically during model datagen</li>
 *   <li>{@link org.betterx.wover.block.api.client.trait.ChestRenderTrait} - attaches the render materials a
 *   custom chest block needs</li>
 *   <li>{@link org.betterx.wover.block.api.client.trait.RenderLayerTrait} - selects a non-solid render layer
 *   (cutout/translucent) for a block</li>
 *   <li>{@link org.betterx.wover.block.api.client.trait.ClientBlockTraits} - central registry of the ready-made
 *   builders above, plus the boat renderer trait</li>
 * </ul>
 *
 * @see org.betterx.wover.block.api.client.model
 */
package org.betterx.wover.block.api.client.trait;
