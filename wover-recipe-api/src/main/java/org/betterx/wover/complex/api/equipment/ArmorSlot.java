package org.betterx.wover.complex.api.equipment;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;

public enum ArmorSlot {
    HELMET_SLOT(0, "helmet", ArmorType.HELMET, RecipeCategory.COMBAT),
    CHESTPLATE_SLOT(1, "chestplate", ArmorType.CHESTPLATE, RecipeCategory.COMBAT),
    LEGGINGS_SLOT(2, "leggings", ArmorType.LEGGINGS, RecipeCategory.COMBAT),
    BOOTS_SLOT(3, "boots", ArmorType.BOOTS, RecipeCategory.COMBAT);


    public interface PropertiesBuilder {
        Item.Properties build(ArmorSlot slot, ArmorTier tier);
    }

    public final RecipeCategory category;
    public final String name;
    public final int slotIndex;
    public final ArmorType armorType;
    private final PropertiesBuilder propertiesBuilder;

    ArmorSlot(
            int slotIndex,
            String name,
            ArmorType armorType,
            RecipeCategory category
    ) {
        this.name = name;
        this.category = category;
        this.slotIndex = slotIndex;
        this.armorType = armorType;
        this.propertiesBuilder = (slot, material) -> {
            var values = material.getValues(slot);
            if (values == null)
                throw new IllegalArgumentException("No values for slot " + slot + " in tier " + material);
            return new Item.Properties().humanoidArmor(material.armorMaterial, slot.armorType);
        };
    }

    public Item.Properties buildProperties(ArmorTier tier) {
        return propertiesBuilder.build(this, tier);
    }
}
