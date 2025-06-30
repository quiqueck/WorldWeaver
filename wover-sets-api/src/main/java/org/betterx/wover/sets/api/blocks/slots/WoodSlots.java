package org.betterx.wover.sets.api.blocks.slots;

import org.betterx.wover.sets.api.blocks.SlotDefinition;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.sets.api.blocks.types.Plank;
import org.betterx.wover.sets.api.blocks.types.Slab;

public class WoodSlots {
    public static final SlotDefinition PLANKS = new Plank(SlotType.PLANKS);
    public static final SlotDefinition SLAB = new Slab(SlotType.SLAB);
}
