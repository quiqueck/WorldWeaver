package org.betterx.wover.complex.api.equipment;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.equipment.ArmorType;

public enum ArmorSlot {
    HELMET_SLOT(0, "helmet", ArmorType.HELMET, RecipeCategory.COMBAT),
    CHESTPLATE_SLOT(1, "chestplate", ArmorType.CHESTPLATE, RecipeCategory.COMBAT),
    LEGGINGS_SLOT(2, "leggings", ArmorType.LEGGINGS, RecipeCategory.COMBAT),
    BOOTS_SLOT(3, "boots", ArmorType.BOOTS, RecipeCategory.COMBAT),
    BODY_SLOT(4, "body", ArmorType.BODY, RecipeCategory.COMBAT);

    public static EquipmentSlot toEquipmentSlot(ArmorSlot slot) {
        return switch (slot) {
            case HELMET_SLOT -> EquipmentSlot.HEAD;
            case CHESTPLATE_SLOT -> EquipmentSlot.CHEST;
            case LEGGINGS_SLOT -> EquipmentSlot.LEGS;
            case BOOTS_SLOT -> EquipmentSlot.FEET;
            case BODY_SLOT -> EquipmentSlot.BODY;
            default -> throw new IllegalArgumentException("Invalid ArmorSlot: " + slot);
        };
    }

    public static ArmorSlot fromEquipmentSlot(EquipmentSlot slot) {
        return switch (slot) {
            case HEAD -> HELMET_SLOT;
            case CHEST -> CHESTPLATE_SLOT;
            case LEGS -> LEGGINGS_SLOT;
            case FEET -> BOOTS_SLOT;
            case BODY -> BODY_SLOT;
            default -> throw new IllegalArgumentException("Invalid EquipmentSlot: " + slot);
        };
    }

    public final RecipeCategory category;
    public final String name;
    public final int slotIndex;
    public final ArmorType armorType;

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
    }
}
