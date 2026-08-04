package de.ambertation.wover.item.api;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumables;

/**
 * Specialized configuration class for creating drink items with drink-specific behavior.
 * This class extends {@link FoodItemDefinition} to provide a convenient way to create consumable
 * items that behave like drinks rather than solid food.
 *
 * <p>This class automatically configures the item with {@link Consumables#defaultDrink()}
 * behavior, which sets appropriate animations, sounds, and effects for liquid consumption.</p>
 *
 * @param <I> The type of drink item being created, must extend {@link Item}
 * @author Quiqueck
 * @since 21.6.0
 */
public class DrinkItemDefinition<I extends Item> extends FoodItemDefinition<I> {
    /**
     * Creates a new drink item configuration with default drink consumption behavior.
     * This constructor automatically applies drink-specific consumable behavior including
     * the DRINK animation, appropriate sounds, and particle effects.
     *
     * @param registry    The item registry to use for registration
     * @param drinkName   The name identifier for the drink item
     * @param itemFactory The factory used to create the drink item instance
     */
    protected DrinkItemDefinition(
            ItemRegistry registry,
            String drinkName,
            ItemDefinition.ItemFactory<I, FoodItemDefinition<I>> itemFactory
    ) {
        super(registry, drinkName, itemFactory, Consumables.defaultDrink());
    }
}
