/**
 * Java API for defining, configuring and registering {@link net.minecraft.world.level.block.Block}s.
 * <p>
 * The main entry point is {@link org.betterx.wover.block.api.BlockRegistry}, which produces
 * {@link org.betterx.wover.block.api.BlockDefinition} builders ({@link org.betterx.wover.block.api.DefaultBlockDefinition}
 * for custom block classes, {@link org.betterx.wover.block.api.VanillaBlockDefinition} for plain vanilla
 * blocks) that configure a block's properties, tags and {@link org.betterx.wover.block.api.trait.BlockTrait}s
 * before building and registering both the block and its item.
 */
package org.betterx.wover.block.api;
