package org.betterx.wover.sets.api.blocks;

import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

public record SlotData(@NotNull SlotType slot, @NotNull Block block) {
}
