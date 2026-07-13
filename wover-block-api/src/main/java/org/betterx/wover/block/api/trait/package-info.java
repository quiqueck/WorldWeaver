/**
 * Reusable "trait" system for attaching optional behavior/properties to a
 * {@link org.betterx.wover.block.api.BlockDefinition} without subclassing.
 * <p>
 * A {@link org.betterx.wover.block.api.trait.BlockTrait} is added via
 * {@link org.betterx.wover.block.api.BlockDefinition#addTrait}, configures the definition while it is being
 * built, and optionally leaves a {@link org.betterx.wover.block.api.trait.RuntimeBlockTrait} behind on the
 * finished block (see {@link org.betterx.wover.block.api.trait.BlockWithTraits}). Traits are identified and
 * created through a matching {@link org.betterx.wover.block.api.trait.BlockTraitBuilder}, usually exposed as
 * a static constant.
 */
package org.betterx.wover.block.api.trait;
