/**
 * Ready-made {@link org.betterx.wover.block.api.client.trait.BlockModelTrait}/
 * {@link org.betterx.wover.item.api.client.trait.ItemModelTrait} factories for every block/item type in
 * {@code org.betterx.wover.sets.api.blocks.types}.
 *
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link org.betterx.wover.block.api.client.model.ModelTraitLibrary} - one factory method per
 *   block/item shape (slab, stairs, door, chest, log, elytra, ...), matching vanilla's own datagen model
 *   generators</li>
 * </ul>
 *
 * <p>Every method returns {@code null} outside of a datagen environment, matching the pattern used by
 * {@link org.betterx.wover.block.api.client.trait.BlockModelTrait}/{@link org.betterx.wover.item.api.client.trait.ItemModelTrait}
 * themselves, so the returned trait can be added unconditionally from common (non-client-only) registration
 * code.
 *
 * @see org.betterx.wover.block.api.client.trait
 */
package org.betterx.wover.block.api.client.model;
