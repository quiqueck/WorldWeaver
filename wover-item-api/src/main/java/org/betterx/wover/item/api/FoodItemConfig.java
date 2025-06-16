package org.betterx.wover.item.api;

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

public class FoodItemConfig<I extends Item> extends ItemConfig<I, FoodItemConfig<I>> {
    public interface ItemFactory<I extends Item> extends ItemConfig.ItemFactory<I, FoodItemConfig<I>> {
    }

    private final FoodProperties.Builder foodProperties;
    private final Consumable.Builder consumable;

    protected FoodItemConfig(
            ItemRegistry registry,
            String name,
            ItemConfig.ItemFactory<I, FoodItemConfig<I>> itemFactory
    ) {
        this(registry, name, itemFactory, Consumables.defaultFood());
    }

    protected FoodItemConfig(
            ItemRegistry registry,
            String name,
            ItemConfig.ItemFactory<I, FoodItemConfig<I>> itemFactory,
            Consumable.Builder baseConsumable
    ) {
        super(registry, name, itemFactory);
        this.foodProperties = new FoodProperties.Builder();
        this.consumable = baseConsumable;
    }

    @Override
    protected void beforeBuild() {
        this.food(this.foodProperties.build(), consumable.build());
    }

    public FoodItemConfig<I> setEffects(MobEffectInstance... effects) {
        for (MobEffectInstance effect : effects) {
            this.consumable.onConsume(new ApplyStatusEffectsConsumeEffect(
                    effect,
                    1F
            ));
        }
        return this;
    }


    public FoodItemConfig<I> consumeSeconds(float seconds) {
        this.consumable.consumeSeconds(seconds);
        return this;
    }

    public FoodItemConfig<I> animation(ItemUseAnimation itemUseAnimation) {
        this.consumable.animation(itemUseAnimation);
        return this;
    }

    public FoodItemConfig<I> sound(Holder<SoundEvent> holder) {
        this.consumable.sound(holder);
        return this;
    }

    public FoodItemConfig<I> soundAfterConsume(Holder<SoundEvent> holder) {
        this.consumable.soundAfterConsume(holder);
        return this;
    }

    public FoodItemConfig<I> hasConsumeParticles(boolean bl) {
        this.consumable.hasConsumeParticles(bl);
        return this;
    }

    public FoodItemConfig<I> onConsume(ConsumeEffect consumeEffect) {
        this.consumable.onConsume(consumeEffect);
        return this;
    }


    public FoodItemConfig<I> nutrition(int hunger) {
        this.foodProperties.nutrition(hunger);
        return this;
    }

    public FoodItemConfig<I> saturationModifier(float saturation) {
        this.foodProperties.saturationModifier(saturation);
        return this;
    }

    public FoodItemConfig<I> alwaysEdible() {
        this.foodProperties.alwaysEdible();
        return this;
    }

    // **********************************************************************
    // Redirect all (but setId) Item.Properties methods to this.properties

    protected FoodItemConfig<I> food(FoodProperties foodProperties) {
        this.properties.food(foodProperties);
        return this;
    }

    protected FoodItemConfig<I> food(FoodProperties foodProperties, Consumable consumable) {
        this.properties.food(foodProperties, consumable);
        return this;
    }
}
