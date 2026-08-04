/**
 * Java API for defining, configuring and registering {@link net.minecraft.world.level.block.Block}s.
 * <p>
 * The main entry point is {@link de.ambertation.wover.block.api.BlockRegistry}, which produces
 * {@link de.ambertation.wover.block.api.BlockDefinition} builders ({@link de.ambertation.wover.block.api.DefaultBlockDefinition}
 * for custom block classes, {@link de.ambertation.wover.block.api.VanillaBlockDefinition} for plain vanilla
 * blocks) that configure a block's properties, tags and {@link de.ambertation.wover.block.api.trait.BlockTrait}s
 * before building and registering both the block and its item.
 */
package de.ambertation.wover.block.api;
