package org.betterx.wover.sets.api.blocks.slots;

import org.betterx.wover.sets.api.blocks.SlotDefinition;
import org.betterx.wover.sets.api.blocks.types.*;

public class WoodSlots {
    public static final SlotDefinition BARK = new Bark(true);
    public static final SlotDefinition BARREL = new Barrel();
    public static final SlotDefinition BOAT = new Boat(false);
    public static final SlotDefinition BOOKSHELF = new Bookshelf();
    public static final SlotDefinition BUTTON = new Button();
    public static final SlotDefinition CHEST = new Chest();
    public static final SlotDefinition CHEST_BOAT = new Boat(true);
    public static final SlotDefinition COMPOSTER = new Composter();
    public static final SlotDefinition CRAFTING_TABLE = new CraftingTable();
    public static final SlotDefinition DOOR = new Door();
    public static final SlotDefinition FENCE = new Fence();
    public static final SlotDefinition FENCE_GATE = new Gate();
    public static final SlotDefinition HANGING_SIGN = new HangingSign();
    public static final SlotDefinition LADDER = new Ladder();
    public static final SlotDefinition PRESSURE_PLATE = new PressurePlate();
    public static final SlotDefinition STAIRS = new Stairs();
    public static final SlotDefinition LOG = new Log(true);
    public static final SlotDefinition PLANKS = new Planks();
    public static final SlotDefinition SIGN = new Sign();
    public static final SlotDefinition SLAB = new Slab();
    public static final SlotDefinition STRIPPED_BARK = new Bark(false);
    public static final SlotDefinition STRIPPED_LOG = new Log(false);
    public static final SlotDefinition TRAPDOOR = new Trapdoor();

}
