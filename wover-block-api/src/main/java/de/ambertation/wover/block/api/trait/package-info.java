/**
 * Reusable "trait" system for attaching optional behavior/properties to a
 * {@link de.ambertation.wover.block.api.BlockDefinition} without subclassing.
 * <p>
 * A {@link de.ambertation.wover.block.api.trait.BlockTrait} is added via
 * {@link de.ambertation.wover.block.api.BlockDefinition#addTrait}, configures the definition while it is being
 * built, and optionally leaves a {@link de.ambertation.wover.block.api.trait.RuntimeBlockTrait} behind on the
 * finished block (see {@link de.ambertation.wover.block.api.trait.BlockWithTraits}). Traits are identified and
 * created through a matching {@link de.ambertation.wover.block.api.trait.BlockTraitBuilder}, usually exposed as
 * a static constant.
 */
package de.ambertation.wover.block.api.trait;
