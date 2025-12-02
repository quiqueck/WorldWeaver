package com.betterxlib.api.item;

import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;

/**
 * A base pickaxe item with configurable properties.
 */
public class BasePickaxeItem extends PickaxeItem {

    public BasePickaxeItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    /**
     * Create a pickaxe with default attack damage and speed modifiers.
     *
     * @param tier the tool tier
     * @param attackDamage bonus attack damage
     * @param attackSpeed attack speed modifier
     * @return a new BasePickaxeItem
     */
    public static BasePickaxeItem create(Tier tier, int attackDamage, float attackSpeed) {
        return new BasePickaxeItem(tier, new Properties()
            .attributes(PickaxeItem.createAttributes(tier, attackDamage, attackSpeed)));
    }

    /**
     * Create a fireproof pickaxe.
     *
     * @param tier the tool tier
     * @param attackDamage bonus attack damage
     * @param attackSpeed attack speed modifier
     * @return a new fireproof BasePickaxeItem
     */
    public static BasePickaxeItem fireproof(Tier tier, int attackDamage, float attackSpeed) {
        return new BasePickaxeItem(tier, new Properties()
            .attributes(PickaxeItem.createAttributes(tier, attackDamage, attackSpeed))
            .fireResistant());
    }
}
