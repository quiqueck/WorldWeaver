package de.ambertation.wover.sets.api.blocks;

import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

/**
 * The block registered for a single {@link SlotType} within a {@link BlockSet}, as stored internally after
 * {@link BlockSet#buildAndRegister()}.
 *
 * @param slot  the slot this block was registered for
 * @param block the registered block
 */
public record SlotData(@NotNull SlotType slot, @NotNull Block block) {
}
