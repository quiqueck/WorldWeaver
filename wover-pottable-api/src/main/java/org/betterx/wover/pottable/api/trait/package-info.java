/**
 * Block traits that mark a block as pottable, so it is picked up automatically by this module's datagen
 * providers instead of requiring manual {@link org.betterx.wover.pottable.api.PottablePlantRegistry}/
 * {@link org.betterx.wover.pottable.api.PottableSoilRegistry} calls.
 * <p>
 * Attach {@link org.betterx.wover.pottable.api.trait.PottablePlantBlockTrait} (via {@code any()} or
 * {@code withSoils(TagKey)}) to a plant block, or
 * {@link org.betterx.wover.pottable.api.trait.PottableSoilBlockTrait#DEFAULT} to a soil block, at the block's
 * normal registration site (e.g. {@code BlockDefinition#addTrait(...)} from {@code wover-block-api}).
 *
 * @see org.betterx.wover.pottable.api.datagen
 */
package org.betterx.wover.pottable.api.trait;
