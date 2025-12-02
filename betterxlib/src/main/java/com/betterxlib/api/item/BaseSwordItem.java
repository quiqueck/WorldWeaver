package com.betterxlib.api.item;

import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

/**
 * A base sword item with configurable properties.
 */
public class BaseSwordItem extends SwordItem {

    public BaseSwordItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    /**
     * Create a sword with default attack damage and speed modifiers.
     *
     * @param tier the tool tier
     * @param attackDamage bonus attack damage
     * @param attackSpeed attack speed modifier
     * @return a new BaseSwordItem
     */
    public static BaseSwordItem create(Tier tier, int attackDamage, float attackSpeed) {
        return new BaseSwordItem(tier, new Properties()
            .attributes(SwordItem.createAttributes(tier, attackDamage, attackSpeed)));
    }

    /**
     * Create a fireproof sword.
     *
     * @param tier the tool tier
     * @param attackDamage bonus attack damage
     * @param attackSpeed attack speed modifier
     * @return a new fireproof BaseSwordItem
     */
    public static BaseSwordItem fireproof(Tier tier, int attackDamage, float attackSpeed) {
        return new BaseSwordItem(tier, new Properties()
            .attributes(SwordItem.createAttributes(tier, attackDamage, attackSpeed))
            .fireResistant());
    }
}
