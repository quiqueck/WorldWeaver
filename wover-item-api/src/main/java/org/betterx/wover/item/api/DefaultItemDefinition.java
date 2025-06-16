package org.betterx.wover.item.api;

import net.minecraft.world.item.Item;

/**
 * Default implementation of {@link ItemDefinition} for creating standard items.
 * This class provides a concrete implementation of the abstract ItemDefinition base class
 * without any specialized behavior, making it suitable for basic item creation.
 *
 * <p>This is the most commonly used configuration class for simple items that don't
 * require specialized functionality like armor or tools. It inherits all the standard
 * item configuration methods from the base ItemDefinition class.</p>
 *
 * @param <I> The type of item being created, must extend {@link Item}
 * @author Quiqueck
 * @since 21.6.0
 */
public final class DefaultItemDefinition<I extends Item> extends ItemDefinition<I, DefaultItemDefinition<I>> {
    /**
     * Factory interface for creating default items from configuration objects.
     * Extends the base ItemFactory to work specifically with DefaultItemDefinition.
     *
     * @param <I> The type of item to create
     */
    public interface ItemFactory<I extends Item> extends ItemDefinition.ItemFactory<I, DefaultItemDefinition<I>> {
    }

    /**
     * Creates a new default item configuration.
     * Package-private constructor to ensure controlled creation through the item registry.
     *
     * @param registry    The item registry to use for registration
     * @param itemName    The name identifier for the item
     * @param itemFactory The factory used to create the item instance
     */
    DefaultItemDefinition(
            ItemRegistry registry,
            String itemName,
            ItemDefinition.ItemFactory<I, DefaultItemDefinition<I>> itemFactory
    ) {
        super(registry, itemName, itemFactory);
    }

    /**
     * Called before the item is built to allow for any final configuration.
     * This default implementation is empty since standard items typically don't
     * require special pre-build setup. Subclasses can override this method
     * if custom initialization is needed.
     */
    @Override
    protected void beforeBuild() {

    }

    /**
     * Called before the item is registered to allow for any final modifications.
     * This default implementation returns the item unchanged since standard items
     * typically don't require post-creation modifications. Subclasses can override
     * this method if custom setup is needed before registration.
     *
     * @param item The built item instance
     * @return The item instance (potentially modified) that should be registered
     */
    @Override
    protected I beforeRegister(I item) {
        return item;
    }
}
