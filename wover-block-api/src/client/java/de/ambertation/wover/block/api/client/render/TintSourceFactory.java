package de.ambertation.wover.block.api.client.render;

import de.ambertation.wover.block.api.render.TinterKey;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.world.level.block.Block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Builds the {@code BlockTintSource} for a {@link TinterKey}, from that key's common-safe payload.
 * <p>
 * The returned source is what actually produces colour, and it drives both halves: {@code BlockColors} calls it
 * per state/position while rendering the world, and the datagen walk calls {@link BlockTintSource#color} once to
 * bake a constant tint into the item model. Implementations that vary by position should override
 * {@link BlockTintSource#colorInWorld} and let {@code color(state)} fall back to a representative value, since
 * that is the one the item model gets.
 *
 * @param <P> the payload type the key carries
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface TintSourceFactory<P> {
    /**
     * Creates the tint source for a block.
     *
     * @param payload the binding's payload
     * @param block   the block being tinted
     * @return the tint source driving this block's colour
     */
    BlockTintSource create(P payload, Block block);
}
