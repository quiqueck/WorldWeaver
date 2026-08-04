package de.ambertation.wover.sets.api.blocks.slots;

import de.ambertation.wover.sets.api.blocks.SlotFromDefinition;
import de.ambertation.wover.sets.api.blocks.types.*;

/**
 * Every slot {@link de.ambertation.wover.sets.api.blocks.WoodenBlockSet#createDefaultDefinitions()} builds by
 * default: planks, log/stripped log, bark/stripped bark, and the usual wood-family blocks (slab, stairs, fence,
 * fence gate, door, trapdoor, button, pressure plate, ladder, sign, hanging sign, chest, barrel, bookshelf,
 * composter, crafting table). Pass individual constants to
 * {@link de.ambertation.wover.sets.api.blocks.SlotMap#of}/{@link de.ambertation.wover.sets.api.blocks.SlotMap#add} to
 * build a custom subset, or {@link de.ambertation.wover.sets.api.blocks.SlotMap#replace} to override one of them
 * (e.g. with a different {@link de.ambertation.wover.sets.api.blocks.SlotType}). Note there is no {@code WALL}
 * constant here (vanilla has no wooden walls); add
 * {@code de.ambertation.wover.sets.api.blocks.types.Wall} explicitly if the mod wants one (it will use
 * {@link de.ambertation.wover.recipe.api.RecipeTraitLibrary#woodWall}). Likewise {@link #RAFT}/{@link #CHEST_RAFT}
 * are not part of the default set - use {@link de.ambertation.wover.sets.api.blocks.SlotMap#remove} on
 * {@link #BOAT}/{@link #CHEST_BOAT} and {@link de.ambertation.wover.sets.api.blocks.SlotMap#add} them instead for a
 * set whose boat should be a raft (e.g. vanilla's bamboo raft), like a plant-based "wood" set.
 */
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
    public static final SlotFromDefinition RAFT = new Boat(false, true);
    public static final SlotFromDefinition CHEST_RAFT = new Boat(true, true);
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
