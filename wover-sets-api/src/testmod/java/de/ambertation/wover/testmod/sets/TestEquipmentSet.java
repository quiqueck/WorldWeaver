package de.ambertation.wover.testmod.sets;

import de.ambertation.wover.complex.api.equipment.*;
import de.ambertation.wover.testmod.entrypoint.TestModWoverSets;

import net.minecraft.world.item.Items;

import org.jetbrains.annotations.ApiStatus;

public class TestEquipmentSet extends EquipmentSet {
    public static final TestEquipmentSet INSTANCE = new TestEquipmentSet();

    public TestEquipmentSet() {
        super(TestModWoverSets.C, "test_equipment_set", ToolTiers.DIAMOND_TOOL, ArmorTiers.TURTLE_ARMOR, Items.STONE);

        add(ToolSlot.PICKAXE_SLOT);
        add(ToolSlot.AXE_SLOT);
        add(ToolSlot.SHOVEL_SLOT);
        add(ToolSlot.HOE_SLOT);
        add(ToolSlot.SWORD_SLOT);

        add(ArmorSlot.HELMET_SLOT);
        add(ArmorSlot.CHESTPLATE_SLOT);
        add(ArmorSlot.LEGGINGS_SLOT);
        add(ArmorSlot.BOOTS_SLOT);
    }

    @ApiStatus.Internal
    public static void ensureStaticInit() {
        // NO-OP
    }
}
