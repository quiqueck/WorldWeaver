package org.betterx.wover.testmod.sets;

import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotMap;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.sets.api.blocks.slots.StoneSlots;
import org.betterx.wover.testmod.entrypoint.TestModWoverSets;

public class TestStoneSet extends BlockSet<TestStoneSet> {
    public TestStoneSet() {
        super(TestModWoverSets.C, "stony", SlotType.SOURCE);
    }

    @Override
    protected SlotMap createDefaultDefinitions() {
        SlotMap map = SlotMap.of(
                StoneSlots.SOURCE,
                StoneSlots.SLAB,
                StoneSlots.STAIRS,
                StoneSlots.WALL,

                StoneSlots.BRICK_SOURCE,
                StoneSlots.BRICK_SLAB,
                StoneSlots.BRICK_STAIRS,
                StoneSlots.BRICK_WALL,

                StoneSlots.WEATHERED_BRICK_SOURCE,
                StoneSlots.WEATHERED_SLAB,
                StoneSlots.WEATHERED_STAIRS,
                StoneSlots.WEATHERED_WALL
        );
        return map;
    }
}

