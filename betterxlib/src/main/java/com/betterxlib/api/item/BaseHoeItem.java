package com.betterxlib.api.item;

import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Tier;

/**
 * A base hoe item with configurable properties.
 */
public class BaseHoeItem extends HoeItem {

    public BaseHoeItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    /**
     * Create a hoe with default attack damage and speed modifiers.
     *
     * @param tier the tool tier
     * @param attackDamage bonus attack damage
     * @param attackSpeed attack speed modifier
     * @return a new BaseHoeItem
     */
    public static BaseHoeItem create(Tier tier, int attackDamage, float attackSpeed) {
        return new BaseHoeItem(tier, new Properties()
            .attributes(HoeItem.createAttributes(tier, attackDamage, attackSpeed)));
    }

    /**
     * Create a fireproof hoe.
     *
     * @param tier the tool tier
     * @param attackDamage bonus attack damage
     * @param attackSpeed attack speed modifier
     * @return a new fireproof BaseHoeItem
     */
    public static BaseHoeItem fireproof(Tier tier, int attackDamage, float attackSpeed) {
        return new BaseHoeItem(tier, new Properties()
            .attributes(HoeItem.createAttributes(tier, attackDamage, attackSpeed))
            .fireResistant());
    }
}
