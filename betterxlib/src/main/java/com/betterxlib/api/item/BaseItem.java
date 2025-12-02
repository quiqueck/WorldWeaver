package com.betterxlib.api.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * A simple base item with configurable properties.
 */
public class BaseItem extends Item {

    public BaseItem(Properties properties) {
        super(properties);
    }

    /**
     * Create a base item with default properties.
     *
     * @return a new BaseItem
     */
    public static BaseItem create() {
        return new BaseItem(new Properties());
    }

    /**
     * Create a base item with a max stack size.
     *
     * @param maxStackSize the maximum stack size
     * @return a new BaseItem
     */
    public static BaseItem create(int maxStackSize) {
        return new BaseItem(new Properties().stacksTo(maxStackSize));
    }

    /**
     * Create a base item with custom rarity.
     *
     * @param rarity the item rarity
     * @return a new BaseItem
     */
    public static BaseItem create(Rarity rarity) {
        return new BaseItem(new Properties().rarity(rarity));
    }

    /**
     * Create a fireproof base item (survives in lava).
     *
     * @return a new fireproof BaseItem
     */
    public static BaseItem fireproof() {
        return new BaseItem(new Properties().fireResistant());
    }

    /**
     * Create a non-stackable base item.
     *
     * @return a new non-stackable BaseItem
     */
    public static BaseItem unstackable() {
        return new BaseItem(new Properties().stacksTo(1));
    }
}
