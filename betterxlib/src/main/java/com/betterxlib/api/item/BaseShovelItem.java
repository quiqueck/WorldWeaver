package com.betterxlib.api.item;

import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;

/**
 * A base shovel item with configurable properties.
 */
public class BaseShovelItem extends ShovelItem {

    public BaseShovelItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    /**
     * Create a shovel with default attack damage and speed modifiers.
     *
     * @param tier the tool tier
     * @param attackDamage bonus attack damage
     * @param attackSpeed attack speed modifier
     * @return a new BaseShovelItem
     */
    public static BaseShovelItem create(Tier tier, float attackDamage, float attackSpeed) {
        return new BaseShovelItem(tier, new Properties()
            .attributes(ShovelItem.createAttributes(tier, attackDamage, attackSpeed)));
    }

    /**
     * Create a fireproof shovel.
     *
     * @param tier the tool tier
     * @param attackDamage bonus attack damage
     * @param attackSpeed attack speed modifier
     * @return a new fireproof BaseShovelItem
     */
    public static BaseShovelItem fireproof(Tier tier, float attackDamage, float attackSpeed) {
        return new BaseShovelItem(tier, new Properties()
            .attributes(ShovelItem.createAttributes(tier, attackDamage, attackSpeed))
            .fireResistant());
    }
}
