package de.ambertation.wover.common.item.api;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

/**
 * If an Item implements this interface, the {@link #setupItemStack(ItemStack, HolderLookup.Provider)} method will be
 * called when a new ItemStack is created.
 * <p>
 * This method is applied, when an Item is added to a Creative Tab (using the Wover API) or
 * if it is created by a command (like /give)
 */
public interface ItemWithCustomStack {
    void setupItemStack(ItemStack stack, HolderLookup.Provider provider);
}
