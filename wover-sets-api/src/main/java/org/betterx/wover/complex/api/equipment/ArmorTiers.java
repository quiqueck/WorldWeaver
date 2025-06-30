package org.betterx.wover.complex.api.equipment;

import net.minecraft.world.item.equipment.ArmorMaterials;

public class ArmorTiers {
    public static ArmorTier LEATHER_ARMOR = ArmorTier
            .builder("leather")
            .armorMaterialWithValues(ArmorMaterials.LEATHER)
            .build();

    public static ArmorTier GOLDEN_ARMOR = ArmorTier
            .builder("golden")
            .armorMaterialWithValues(ArmorMaterials.GOLD)
            .build();

    public static ArmorTier CHAINMAIL_ARMOR = ArmorTier
            .builder("chainmail")
            .armorMaterialWithValues(ArmorMaterials.CHAINMAIL)
            .build();

    public static ArmorTier IRON_ARMOR = ArmorTier
            .builder("iron")
            .armorMaterialWithValues(ArmorMaterials.IRON)
            .build();

    public static ArmorTier DIAMOND_ARMOR = ArmorTier
            .builder("diamond")
            .armorMaterialWithValues(ArmorMaterials.DIAMOND)
            .build();

    public static ArmorTier NETHERITE_ARMOR = ArmorTier
            .builder("netherite")
            .armorMaterialWithValues(ArmorMaterials.NETHERITE)
            .build();

    public static ArmorTier TURTLE_ARMOR = ArmorTier
            .builder("turtle")
            .armorMaterialWithValues(ArmorMaterials.TURTLE_SCUTE)
            .build();

    public static ArmorTier ARMADILLO_ARMOR = ArmorTier
            .builder("armadillo")
            .armorMaterialWithValues(ArmorMaterials.ARMADILLO_SCUTE)
            .build();
}
