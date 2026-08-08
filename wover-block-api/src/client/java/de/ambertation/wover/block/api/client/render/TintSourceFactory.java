package de.ambertation.wover.block.api.client.render;

import de.ambertation.wover.block.api.render.TinterKey;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.Block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Builds the {@code BlockColor} for a {@link TinterKey}, from that key's common-safe payload.
 * <p>
 * The returned source is what actually produces colour, and it drives both halves: {@code BlockColors} calls it
 * per state/position while rendering the world, and the datagen walk calls {@link BlockColor#getColor} once to
 * bake a constant tint into the item model. Implementations that vary by position read the {@code pos} argument
 * and must tolerate it being {@code null}, since that is the call the item model gets.
 * <p>
 * <b>Divergence from 26.x:</b> 1.21.6 has no {@code BlockTintSource} - that type replaced {@code BlockColor} in
 * 26.1. The factory therefore returns {@code net.minecraft.client.color.block.BlockColor} here, which is a
 * single {@code getColor(state, level, pos, tintIndex)} method rather than the {@code color(state)} /
 * {@code colorInWorld(state, level, pos)} pair. Class name, package, method name and payload/block parameters
 * are unchanged, so declaring a tinter is source-compatible across branches; only the returned MC interface
 * differs, because the MC version forces it.
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
    BlockColor create(P payload, Block block);
}
