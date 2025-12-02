package com.betterxlib.api.item;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

/**
 * A base armor item with configurable properties.
 */
public class BaseArmorItem extends ArmorItem {

    public BaseArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    /**
     * Create an armor item with default properties.
     *
     * @param material the armor material
     * @param type the armor type (helmet, chestplate, leggings, boots)
     * @return a new BaseArmorItem
     */
    public static BaseArmorItem create(Holder<ArmorMaterial> material, Type type) {
        return new BaseArmorItem(material, type, new Properties());
    }

    /**
     * Create a fireproof armor item.
     *
     * @param material the armor material
     * @param type the armor type
     * @return a new fireproof BaseArmorItem
     */
    public static BaseArmorItem fireproof(Holder<ArmorMaterial> material, Type type) {
        return new BaseArmorItem(material, type, new Properties().fireResistant());
    }

    /**
     * Create an armor item with custom durability.
     *
     * @param material the armor material
     * @param type the armor type
     * @param durability the custom durability
     * @return a new BaseArmorItem
     */
    public static BaseArmorItem withDurability(Holder<ArmorMaterial> material, Type type, int durability) {
        return new BaseArmorItem(material, type, new Properties().durability(durability));
    }
}
