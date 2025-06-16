package org.betterx.wover.item.api;

import org.betterx.wover.common.item.api.ItemWithCustomStack;
import org.betterx.wover.state.api.WorldState;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

/**
 * Utility class for ItemStack operations and custom item integration.
 *
 * <p>This helper class provides utilities for working with ItemStacks, particularly
 * for items that implement custom setup logic through the {@link ItemWithCustomStack} interface.
 * It handles the automatic setup of ItemStacks when appropriate registry access is available.
 *
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Automatic custom item stack setup when supported</li>
 *   <li>Integration with world state registry access</li>
 *   <li>Safe handling of null registry providers</li>
 *   <li>Seamless fallback for standard items</li>
 * </ul>
 *
 * @see ItemWithCustomStack
 * @see WorldState
 * @see HolderLookup.Provider
 */
public class ItemStackHelper {
    /**
     * Calls the item stack setup method if the item supports it and registry access is available.
     *
     * <p>This method uses the world state's registry access to provide the necessary
     * lookup context for item setup. It's a convenience method that automatically
     * retrieves the current registry access.
     *
     * @param itemStack The item stack to potentially set up
     * @return The item stack, potentially modified by custom setup logic
     * @see #callItemStackSetupIfPossible(ItemStack, HolderLookup.Provider)
     */
    public static ItemStack callItemStackSetupIfPossible(ItemStack itemStack) {
        return callItemStackSetupIfPossible(itemStack, WorldState.registryAccess());
    }

    /**
     * Calls the item stack setup method if the item supports it and the registry provider is available.
     *
     * <p>This method checks if the item in the stack implements {@link ItemWithCustomStack}
     * and if a valid registry provider is available. If both conditions are met, it calls
     * the item's setup method to initialize any custom data or properties.
     *
     * <p>This method is safe to call on any ItemStack - it will only perform setup if
     * the item specifically supports it and the necessary registry context is available.
     *
     * @param itemStack        The item stack to potentially set up
     * @param registryProvider The registry lookup provider, or null if not available
     * @return The item stack, potentially modified by custom setup logic
     */
    public static ItemStack callItemStackSetupIfPossible(ItemStack itemStack, HolderLookup.Provider registryProvider) {
        if (itemStack.getItem() instanceof ItemWithCustomStack customStackItem && registryProvider != null) {
            customStackItem.setupItemStack(itemStack, registryProvider);
        }
        return itemStack;
    }
}
