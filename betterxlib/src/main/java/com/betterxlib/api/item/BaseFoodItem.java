package com.betterxlib.api.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * A base edible item with configurable food properties.
 */
public class BaseFoodItem extends Item {

    public BaseFoodItem(Properties properties) {
        super(properties);
    }

    /**
     * Create a food item with nutrition and saturation.
     *
     * @param nutrition the nutrition value (hunger restored)
     * @param saturation the saturation modifier
     * @return a new BaseFoodItem
     */
    public static BaseFoodItem create(int nutrition, float saturation) {
        FoodProperties food = new FoodProperties.Builder()
            .nutrition(nutrition)
            .saturationModifier(saturation)
            .build();
        return new BaseFoodItem(new Properties().food(food));
    }

    /**
     * Create a fast food item (eats quickly).
     *
     * @param nutrition the nutrition value
     * @param saturation the saturation modifier
     * @return a new fast-eating BaseFoodItem
     */
    public static BaseFoodItem fast(int nutrition, float saturation) {
        FoodProperties food = new FoodProperties.Builder()
            .nutrition(nutrition)
            .saturationModifier(saturation)
            .fast()
            .build();
        return new BaseFoodItem(new Properties().food(food));
    }

    /**
     * Create a meat food item.
     *
     * @param nutrition the nutrition value
     * @param saturation the saturation modifier
     * @return a new meat BaseFoodItem
     */
    public static BaseFoodItem meat(int nutrition, float saturation) {
        FoodProperties food = new FoodProperties.Builder()
            .nutrition(nutrition)
            .saturationModifier(saturation)
            .meat()
            .build();
        return new BaseFoodItem(new Properties().food(food));
    }

    /**
     * Create a food item that can always be eaten.
     *
     * @param nutrition the nutrition value
     * @param saturation the saturation modifier
     * @return a new always-edible BaseFoodItem
     */
    public static BaseFoodItem alwaysEdible(int nutrition, float saturation) {
        FoodProperties food = new FoodProperties.Builder()
            .nutrition(nutrition)
            .saturationModifier(saturation)
            .alwaysEdible()
            .build();
        return new BaseFoodItem(new Properties().food(food));
    }

    /**
     * Builder for creating custom food items.
     */
    public static class Builder {
        private final FoodProperties.Builder foodBuilder = new FoodProperties.Builder();
        private Rarity rarity = Rarity.COMMON;
        private int maxStackSize = 64;
        private boolean fireResistant = false;

        public Builder nutrition(int nutrition) {
            foodBuilder.nutrition(nutrition);
            return this;
        }

        public Builder saturation(float saturation) {
            foodBuilder.saturationModifier(saturation);
            return this;
        }

        public Builder fast() {
            foodBuilder.fast();
            return this;
        }

        public Builder meat() {
            foodBuilder.meat();
            return this;
        }

        public Builder alwaysEdible() {
            foodBuilder.alwaysEdible();
            return this;
        }

        public Builder effect(MobEffectInstance effect, float probability) {
            foodBuilder.effect(effect, probability);
            return this;
        }

        public Builder rarity(Rarity rarity) {
            this.rarity = rarity;
            return this;
        }

        public Builder stacksTo(int maxStackSize) {
            this.maxStackSize = maxStackSize;
            return this;
        }

        public Builder fireResistant() {
            this.fireResistant = true;
            return this;
        }

        public BaseFoodItem build() {
            Properties props = new Properties()
                .food(foodBuilder.build())
                .rarity(rarity)
                .stacksTo(maxStackSize);
            if (fireResistant) {
                props = props.fireResistant();
            }
            return new BaseFoodItem(props);
        }
    }

    /**
     * Create a new food builder.
     *
     * @return a new Builder
     */
    public static Builder builder() {
        return new Builder();
    }
}
