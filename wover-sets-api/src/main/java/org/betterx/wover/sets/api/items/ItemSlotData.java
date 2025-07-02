package org.betterx.wover.sets.api.items;

import org.betterx.wover.sets.api.blocks.SlotType;

import net.minecraft.world.item.Item;

import org.jetbrains.annotations.NotNull;

public record ItemSlotData(@NotNull SlotType slot, @NotNull Item item) {
}
