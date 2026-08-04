package de.ambertation.wover.item.api;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;

/**
 * Specialized configuration class for creating food items with food-specific properties.
 * This class extends {@link ItemDefinition} to provide additional methods for configuring
 * nutrition values, saturation, consumption effects, animations, and sounds.
 *
 * <p>Food items in Minecraft have complex behavior including:</p>
 * <ul>
 *   <li>Nutrition and saturation values that affect player hunger</li>
 *   <li>Status effects that are applied when consumed</li>
 *   <li>Custom consumption animations and sounds</li>
 *   <li>Consumption duration and particle effects</li>
 * </ul>
 *
 * @param <I> The type of food item being created, must extend {@link Item}
 * @author Quiqueck
 * @since 21.6.0
 */
public class FoodItemDefinition<I extends Item> extends ItemDefinition<I, FoodItemDefinition<I>> {
    /**
     * Factory interface for creating food items from configuration objects.
     * Extends the base ItemFactory to work specifically with FoodItemDefinition.
     *
     * @param <I> The type of food item to create
     */
    public interface ItemFactory<I extends Item> extends ItemDefinition.ItemFactory<I, FoodItemDefinition<I>> {
    }

    /**
     * Builder for configuring food properties like nutrition and saturation
     */
    private final FoodProperties.Builder foodProperties;

    /**
     * Builder for configuring consumption behavior like animations and effects
     */
    private final Consumable.Builder consumable;

    /**
     * Creates a new food item configuration with default food consumption behavior.
     *
     * @param registry    The item registry to use for registration
     * @param foodName    The name identifier for the food item
     * @param itemFactory The factory used to create the food item instance
     */
    protected FoodItemDefinition(
            ItemRegistry registry,
            String foodName,
            ItemDefinition.ItemFactory<I, FoodItemDefinition<I>> itemFactory
    ) {
        this(registry, foodName, itemFactory, Consumables.defaultFood());
    }

    /**
     * Creates a new food item configuration with custom consumption behavior.
     *
     * @param registry       The item registry to use for registration
     * @param foodName       The name identifier for the food item
     * @param itemFactory    The factory used to create the food item instance
     * @param baseConsumable The base consumable configuration to build upon
     */
    protected FoodItemDefinition(
            ItemRegistry registry,
            String foodName,
            ItemDefinition.ItemFactory<I, FoodItemDefinition<I>> itemFactory,
            Consumable.Builder baseConsumable
    ) {
        super(registry, foodName, itemFactory);
        this.foodProperties = new FoodProperties.Builder();
        this.consumable = baseConsumable;
    }

    /**
     * Called before the food item is built to finalize food and consumable properties.
     * This method automatically applies the configured food properties and consumable
     * behavior to the item properties.
     */
    @Override
    protected void beforeBuild() {
        this.food(this.foodProperties.build(), consumable.build());
    }

    /**
     * Called before the food item is registered to allow for any final modifications.
     * This default implementation returns the item unchanged, but subclasses can override
     * this method to perform custom post-creation setup before registration.
     *
     * @param item The built food item instance
     * @return The food item instance (potentially modified) that should be registered
     */
    @Override
    protected I beforeRegister(I item) {
        return item;
    }

    /**
     * Sets the status effects that are applied when this food is consumed.
     * Each effect is applied with a 100% probability when the food is eaten.
     *
     * @param statusEffects The mob effect instances to apply when consumed
     * @return This configuration instance for method chaining
     */
    public FoodItemDefinition<I> setEffects(MobEffectInstance... statusEffects) {
        for (MobEffectInstance effect : statusEffects) {
            this.consumable.onConsume(new ApplyStatusEffectsConsumeEffect(
                    effect,
                    1F
            ));
        }
        return this;
    }


    /**
     * Sets the time in seconds it takes to consume this food item.
     *
     * @param consumeDuration The consumption duration in seconds
     * @return This configuration instance for method chaining
     */
    public FoodItemDefinition<I> consumeSeconds(float consumeDuration) {
        this.consumable.consumeSeconds(consumeDuration);
        return this;
    }

    /**
     * Sets the animation played when consuming this food item.
     *
     * @param useAnimation The animation to play during consumption (EAT, DRINK, etc.)
     * @return This configuration instance for method chaining
     */
    public FoodItemDefinition<I> animation(ItemUseAnimation useAnimation) {
        this.consumable.animation(useAnimation);
        return this;
    }

    /**
     * Sets the sound played while consuming this food item.
     *
     * @param soundEvent The sound event to play during consumption
     * @return This configuration instance for method chaining
     */
    public FoodItemDefinition<I> sound(Holder<SoundEvent> soundEvent) {
        this.consumable.sound(soundEvent);
        return this;
    }

    /**
     * Sets the sound played after this food item has been consumed.
     *
     * @param soundEvent The sound event to play after consumption completes
     * @return This configuration instance for method chaining
     */
    public FoodItemDefinition<I> soundAfterConsume(Holder<SoundEvent> soundEvent) {
        this.consumable.soundAfterConsume(soundEvent);
        return this;
    }

    /**
     * Sets whether particles are displayed when consuming this food item.
     *
     * @param showParticles Whether to show consumption particles
     * @return This configuration instance for method chaining
     */
    public FoodItemDefinition<I> hasConsumeParticles(boolean showParticles) {
        this.consumable.hasConsumeParticles(showParticles);
        return this;
    }

    /**
     * Adds a custom consume effect that is triggered when this food is eaten.
     *
     * @param effect The consume effect to add
     * @return This configuration instance for method chaining
     */
    public FoodItemDefinition<I> onConsume(ConsumeEffect effect) {
        this.consumable.onConsume(effect);
        return this;
    }


    /**
     * Sets the nutrition value (hunger points) restored when consuming this food.
     * Each point restores half a hunger bar in the player's hunger meter.
     *
     * @param hungerPoints The number of hunger points to restore (typically 1-20)
     * @return This configuration instance for method chaining
     */
    public FoodItemDefinition<I> nutrition(int hungerPoints) {
        this.foodProperties.nutrition(hungerPoints);
        return this;
    }

    /**
     * Sets the saturation modifier for this food item.
     * Saturation affects how long the player stays full after eating.
     * Higher values provide longer-lasting hunger satisfaction.
     *
     * @param saturationValue The saturation modifier (typically 0.1 to 2.0)
     * @return This configuration instance for method chaining
     */
    public FoodItemDefinition<I> saturationModifier(float saturationValue) {
        this.foodProperties.saturationModifier(saturationValue);
        return this;
    }

    /**
     * Makes this food item consumable even when the player's hunger bar is full.
     * This is useful for foods that provide beneficial effects rather than just nutrition.
     *
     * @return This configuration instance for method chaining
     */
    public FoodItemDefinition<I> alwaysEdible() {
        this.foodProperties.alwaysEdible();
        return this;
    }

    // **********************************************************************
    // Redirect food-related Item.Properties methods to this.properties

    /**
     * Internal method to set food properties on the item.
     *
     * @param foodProps The built food properties
     * @return This configuration instance for method chaining
     */
    public FoodItemDefinition<I> food(FoodProperties foodProps) {
        propertySetters.add((properties) -> properties.food(foodProps));
        return this;
    }

    /**
     * Internal method to set both food properties and consumable behavior on the item.
     * This method is called automatically by {@link #beforeBuild()}.
     *
     * @param foodProps          The built food properties
     * @param consumableBehavior The built consumable behavior
     * @return This configuration instance for method chaining
     */
    public FoodItemDefinition<I> food(FoodProperties foodProps, Consumable consumableBehavior) {
        propertySetters.add((properties) -> properties.food(foodProps, consumableBehavior));
        return this;
    }
}
