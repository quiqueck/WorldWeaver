package org.betterx.wover.testmod.sets;

import org.betterx.wover.sets.api.blocks.SlotMap;
import org.betterx.wover.sets.api.blocks.WoodenBlockSet;
import org.betterx.wover.sets.api.blocks.types.Bark;
import org.betterx.wover.sets.api.blocks.types.Log;
import org.betterx.wover.testmod.entrypoint.TestModWoverSets;

import net.minecraft.world.level.material.MapColor;

public class TestWoodSet extends WoodenBlockSet<TestWoodSet> {
    public TestWoodSet() {
        super(TestModWoverSets.C, "wooden", MapColor.COLOR_MAGENTA);
    }

    @Override
    protected SlotMap createDefaultSlots() {
        SlotMap map = super.createDefaultSlots();
        map.replace(new Log(true, true, "_mossy"));
        map.replace(new Bark(true, true, "_mossy"));
        return map;
    }
}
