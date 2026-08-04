/**
 * Block traits describing a block's role within a wood set (see {@code de.ambertation.wover.sets.api.blocks.types}).
 *
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link de.ambertation.wover.block.api.trait.type.LogBlockTrait} - marks a block as a log/stripped log</li>
 *   <li>{@link de.ambertation.wover.block.api.trait.type.BarkBlockTrait} - marks a block as bark/stripped bark</li>
 * </ul>
 *
 * <p>Both traits are data-less markers combined with a
 * {@link de.ambertation.wover.block.api.trait.behaviour.StripableBlockTrait} by their respective builders (see
 * {@link de.ambertation.wover.block.api.trait.BlockTraits#LOG_BLOCK}/{@link de.ambertation.wover.block.api.trait.BlockTraits#BARK_BLOCK}),
 * so a single {@code addTrait(...)} call both marks the block's type and configures how it strips.
 *
 * @see de.ambertation.wover.block.api.trait
 */
package de.ambertation.wover.block.api.trait.type;
