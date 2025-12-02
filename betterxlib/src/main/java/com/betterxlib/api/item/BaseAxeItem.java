package com.betterxlib.api.item;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Tier;

/**
 * A base axe item with configurable properties.
 */
public class BaseAxeItem extends AxeItem {

    public BaseAxeItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    /**
     * Create an axe with default attack damage and speed modifiers.
     *
     * @param tier the tool tier
     * @param attackDamage bonus attack damage
     * @param attackSpeed attack speed modifier
     * @return a new BaseAxeItem
     */
    public static BaseAxeItem create(Tier tier, float attackDamage, float attackSpeed) {
        return new BaseAxeItem(tier, new Properties()
            .attributes(AxeItem.createAttributes(tier, attackDamage, attackSpeed)));
    }

    /**
     * Create a fireproof axe.
     *
     * @param tier the tool tier
     * @param attackDamage bonus attack damage
     * @param attackSpeed attack speed modifier
     * @return a new fireproof BaseAxeItem
     */
    public static BaseAxeItem fireproof(Tier tier, float attackDamage, float attackSpeed) {
        return new BaseAxeItem(tier, new Properties()
            .attributes(AxeItem.createAttributes(tier, attackDamage, attackSpeed))
            .fireResistant());
    }
}
