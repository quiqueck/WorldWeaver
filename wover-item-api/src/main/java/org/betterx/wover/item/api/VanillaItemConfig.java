package org.betterx.wover.item.api;

import net.minecraft.world.item.Item;

/**
 * Configuration class specifically for creating vanilla Minecraft {@link Item} instances.
 * This class is a specialized version of {@link ItemConfig} that creates standard Minecraft
 * items without any custom behavior or additional functionality.
 * 
 * <p>This class differs from {@link DefaultItemConfig} in that it:</p>
 * <ul>
 *   <li>Uses a concrete {@link Item} type rather than a generic type parameter</li>
 *   <li>Provides a built-in factory for creating vanilla items</li>
 *   <li>Is designed for creating items identical to vanilla Minecraft items</li>
 *   <li>Has a simpler constructor that doesn't require a factory parameter</li>
 * </ul>
 * 
 * <p>Use this class when you want to create items that behave exactly like vanilla Minecraft
 * items, with only custom properties like stack size, durability, or rarity.</p>
 * 
 * @author Quiqueck
 * @since 21.6.0
 */
public final class VanillaItemConfig extends ItemConfig<Item, VanillaItemConfig> {
    /**
     * Default factory for creating vanilla Minecraft items.
     * This factory simply creates a new {@link Item} instance with the configured properties,
     * resulting in an item that behaves identically to vanilla Minecraft items.
     */
    public static final ItemFactory<Item, VanillaItemConfig> DEFAULT_FACTORY = config -> new Item(
            config.properties);

    /**
     * Creates a new vanilla item configuration.
     * Package-private constructor to ensure controlled creation through the item registry.
     * Uses the {@link #DEFAULT_FACTORY} to create standard vanilla items.
     * 
     * @param registry The item registry to use for registration
     * @param itemName The name identifier for the vanilla item
     */
    VanillaItemConfig(ItemRegistry registry, String itemName) {
        super(registry, itemName, DEFAULT_FACTORY);
    }

    /**
     * Called before the vanilla item is built to allow for any final configuration.
     * This default implementation is empty since vanilla items typically don't
     * require special pre-build setup. The simplicity is intentional to match
     * vanilla Minecraft item behavior.
     */
    @Override
    protected void beforeBuild() {
        
    }
}
