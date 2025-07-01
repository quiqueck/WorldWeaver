package org.betterx.wover.sets.api.blocks.slots;

import org.betterx.wover.sets.api.blocks.SlotDefinition;
import org.betterx.wover.sets.api.blocks.types.*;

public class WoodSlots {
    public static final SlotDefinition BARK = new Bark(true);
    public static final SlotDefinition BARREL = new Barrel();
    public static final SlotDefinition LOG = new Log(true);
    public static final SlotDefinition PLANKS = new Planks();
    public static final SlotDefinition SLAB = new Slab();
    public static final SlotDefinition STRIPPED_BARK = new Bark(false);
    public static final SlotDefinition STRIPPED_LOG = new Log(false);
}
