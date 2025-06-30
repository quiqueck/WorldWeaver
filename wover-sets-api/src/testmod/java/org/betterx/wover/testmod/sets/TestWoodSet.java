package org.betterx.wover.testmod.sets;

import org.betterx.wover.sets.api.blocks.WoodenBlockSet;
import org.betterx.wover.testmod.entrypoint.TestModWoverSets;

import net.minecraft.world.level.material.MapColor;

public class TestWoodSet extends WoodenBlockSet<TestWoodSet> {
    public TestWoodSet() {
        super(TestModWoverSets.C, "wooden", MapColor.COLOR_MAGENTA);
    }
}
