package org.betterx.wover.complex.api.equipment;

import net.minecraft.world.item.equipment.ArmorMaterials;

/**
 * Ready-made {@link ArmorTier}s for every vanilla {@link ArmorMaterials} constant, for use with
 * {@link EquipmentSet} or as a base for {@link ArmorTier#copyWithOffset}.
 */
public class ArmorTiers {
    /** The vanilla leather armor tier. */
    public static ArmorTier LEATHER_ARMOR = ArmorTier
            .builder("leather")
            .armorMaterialWithValues(ArmorMaterials.LEATHER)
            .build();

    /** The vanilla golden armor tier. */
    public static ArmorTier GOLDEN_ARMOR = ArmorTier
            .builder("golden")
            .armorMaterialWithValues(ArmorMaterials.GOLD)
            .build();

    /** The vanilla chainmail armor tier. */
    public static ArmorTier CHAINMAIL_ARMOR = ArmorTier
            .builder("chainmail")
            .armorMaterialWithValues(ArmorMaterials.CHAINMAIL)
            .build();

    /** The vanilla iron armor tier. */
    public static ArmorTier IRON_ARMOR = ArmorTier
            .builder("iron")
            .armorMaterialWithValues(ArmorMaterials.IRON)
            .build();

    /** The vanilla diamond armor tier. */
    public static ArmorTier DIAMOND_ARMOR = ArmorTier
            .builder("diamond")
            .armorMaterialWithValues(ArmorMaterials.DIAMOND)
            .build();

    /** The vanilla netherite armor tier. */
    public static ArmorTier NETHERITE_ARMOR = ArmorTier
            .builder("netherite")
            .armorMaterialWithValues(ArmorMaterials.NETHERITE)
            .build();

    /** The vanilla turtle scute armor tier. */
    public static ArmorTier TURTLE_ARMOR = ArmorTier
            .builder("turtle")
            .armorMaterialWithValues(ArmorMaterials.TURTLE_SCUTE)
            .build();

    /** The vanilla armadillo scute (wolf) armor tier. */
    public static ArmorTier ARMADILLO_ARMOR = ArmorTier
            .builder("armadillo")
            .armorMaterialWithValues(ArmorMaterials.ARMADILLO_SCUTE)
            .build();
}
