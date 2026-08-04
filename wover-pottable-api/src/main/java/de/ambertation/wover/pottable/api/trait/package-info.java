/**
 * Block traits that mark a block as pottable, so it is picked up automatically by this module's datagen
 * providers instead of requiring manual {@link de.ambertation.wover.pottable.api.PottablePlantRegistry}/
 * {@link de.ambertation.wover.pottable.api.PottableSoilRegistry} calls.
 * <p>
 * Attach {@link de.ambertation.wover.pottable.api.trait.PottablePlantBlockTrait} (via {@code any()} or
 * {@code withSoils(TagKey)}) to a plant block, or
 * {@link de.ambertation.wover.pottable.api.trait.PottableSoilBlockTrait#DEFAULT} to a soil block, at the block's
 * normal registration site (e.g. {@code BlockDefinition#addTrait(...)} from {@code wover-block-api}).
 *
 * @see de.ambertation.wover.pottable.api.datagen
 */
package de.ambertation.wover.pottable.api.trait;
