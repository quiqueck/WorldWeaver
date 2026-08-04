package de.ambertation.wover.sets.api.items;

import de.ambertation.wover.sets.api.blocks.SlotType;

import net.minecraft.world.item.Item;

import org.jetbrains.annotations.NotNull;

/**
 * The item registered for a single {@link SlotType} within a
 * {@link de.ambertation.wover.sets.api.blocks.BlockSet} (built through an
 * {@link de.ambertation.wover.sets.api.blocks.ItemSlotFromDefinition}), as stored internally after
 * {@link de.ambertation.wover.sets.api.blocks.BlockSet#buildAndRegister()}.
 *
 * @param slot the slot this item was registered for
 * @param item the registered item
 */
public record ItemSlotData(@NotNull SlotType slot, @NotNull Item item) {
}
