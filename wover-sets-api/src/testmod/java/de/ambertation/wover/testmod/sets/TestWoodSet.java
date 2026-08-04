package de.ambertation.wover.testmod.sets;

import de.ambertation.wover.sets.api.blocks.SlotMap;
import de.ambertation.wover.sets.api.blocks.WoodenBlockSet;
import de.ambertation.wover.sets.api.blocks.slots.WoodSlots;
import de.ambertation.wover.sets.api.blocks.types.Bark;
import de.ambertation.wover.sets.api.blocks.types.Log;
import de.ambertation.wover.testmod.entrypoint.TestModWoverSets;

import net.minecraft.world.level.material.MapColor;

public class TestWoodSet extends WoodenBlockSet<TestWoodSet> {
    public TestWoodSet() {
        super(TestModWoverSets.C, "wooden", MapColor.COLOR_MAGENTA);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        SlotMap map = super.createDefaultDefinitions();
        map.replace(new Log(true, true, "_mossy"));
        map.replace(new Bark(true, true, "_mossy"));
        map.add(WoodSlots.WALL);
        return map;
    }
}
