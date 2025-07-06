package org.betterx.wover.sets.api.blocks.slots;

import org.betterx.wover.sets.api.blocks.SlotFromDefinition;
import org.betterx.wover.sets.api.blocks.types.*;

public class WoodSlots {
    public static final SlotFromDefinition BARK = new Bark(true);
    public static final SlotFromDefinition BARREL = new Barrel();
    public static final SlotFromDefinition BOAT = new Boat(false);
    public static final SlotFromDefinition BOOKSHELF = new Bookshelf();
    public static final SlotFromDefinition BUTTON = new Button();
    public static final SlotFromDefinition CHEST = new Chest();
    public static final SlotFromDefinition CHEST_BOAT = new Boat(true);
    public static final SlotFromDefinition COMPOSTER = new Composter();
    public static final SlotFromDefinition CRAFTING_TABLE = new CraftingTable();
    public static final SlotFromDefinition DOOR = new Door();
    public static final SlotFromDefinition FENCE = new Fence();
    public static final SlotFromDefinition FENCE_GATE = new Gate();
    public static final SlotFromDefinition HANGING_SIGN = new HangingSign();
    public static final SlotFromDefinition LADDER = new Ladder();
    public static final SlotFromDefinition PRESSURE_PLATE = new PressurePlate();
    public static final SlotFromDefinition STAIRS = new Stairs();
    public static final SlotFromDefinition LOG = new Log(true);
    public static final SlotFromDefinition PLANKS = new Planks();
    public static final SlotFromDefinition SIGN = new Sign();
    public static final SlotFromDefinition SLAB = new Slab();
    public static final SlotFromDefinition STRIPPED_BARK = new Bark(false);
    public static final SlotFromDefinition STRIPPED_LOG = new Log(false);
    public static final SlotFromDefinition TRAPDOOR = new Trapdoor();
    public static final SlotFromDefinition WALL = new Wall();

}
