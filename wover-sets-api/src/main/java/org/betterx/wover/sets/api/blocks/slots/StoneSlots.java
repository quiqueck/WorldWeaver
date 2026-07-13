package org.betterx.wover.sets.api.blocks.slots;

import org.betterx.wover.sets.api.blocks.SlotFromDefinition;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.sets.api.blocks.types.*;

/**
 * Ready-made slots for a stone-like material family: the base {@code SOURCE}/{@code SLAB}/{@code STAIRS}/
 * {@code WALL}/{@code PILLAR}/{@code BUTTON}/{@code PRESSURE_PLATE} set, plus source/slab/stairs/wall variants
 * for the {@code TILES}, {@code BRICK}, {@code WEATHERED}, {@code CRACKED}, {@code CHISELED}, and
 * {@code POLISHED} sub-families (each named {@code <FAMILY>_<ROLE>}, e.g. {@link #BRICK_STAIRS}). Unlike
 * {@link WoodSlots}, {@link org.betterx.wover.sets.api.blocks.BlockSet} does not build any of these by default -
 * a stone-like set picks the constants it wants and combines them with
 * {@link org.betterx.wover.sets.api.blocks.SlotMap#of} (see {@code TestStoneSet} in this module's test mod).
 */
public class StoneSlots {
    public static final SlotFromDefinition SOURCE = new Source();
    public static final SlotFromDefinition SLAB = new Slab();
    public static final SlotFromDefinition STAIRS = new Stairs();
    public static final SlotFromDefinition WALL = new Wall();
    public static final SlotFromDefinition PILLAR = new Pillar();

    public static final SlotFromDefinition BUTTON = new Button();
    public static final SlotFromDefinition PRESSURE_PLATE = new PressurePlate();

    public static final SlotFromDefinition TILES_SOURCE = new Source(SlotType.SOURCE, SlotType.TILES);
    public static final SlotFromDefinition TILES_SLAB = new Slab(SlotType.TILES, SlotType.TILES_SLAB);
    public static final SlotFromDefinition TILES_STAIRS = new Stairs(SlotType.TILES, SlotType.TILES_STAIRS);
    public static final SlotFromDefinition TILES_WALL = new Wall(SlotType.TILES, SlotType.TILES_WALL);

    public static final SlotFromDefinition BRICK_SOURCE = new Source(SlotType.SOURCE, SlotType.BRICK);
    public static final SlotFromDefinition BRICK_SLAB = new Slab(SlotType.BRICK, SlotType.BRICK_SLAB);
    public static final SlotFromDefinition BRICK_STAIRS = new Stairs(SlotType.BRICK, SlotType.BRICK_STAIRS);
    public static final SlotFromDefinition BRICK_WALL = new Wall(SlotType.BRICK, SlotType.BRICK_WALL);

    public static final SlotFromDefinition WEATHERED_BRICK_SOURCE = new Source(SlotType.BRICK, SlotType.WEATHERED);
    public static final SlotFromDefinition WEATHERED_SOURCE = new Source(SlotType.SOURCE, SlotType.WEATHERED);
    public static final SlotFromDefinition WEATHERED_SLAB = new Slab(SlotType.WEATHERED, SlotType.WEATHERED_SLAB);
    public static final SlotFromDefinition WEATHERED_STAIRS = new Stairs(SlotType.WEATHERED, SlotType.WEATHERED_STAIRS);
    public static final SlotFromDefinition WEATHERED_WALL = new Wall(SlotType.WEATHERED, SlotType.WEATHERED_WALL);

    public static final SlotFromDefinition CRACKED_BRICK_SOURCE = new Source(SlotType.BRICK, SlotType.CRACKED);
    public static final SlotFromDefinition CRACKED_SOURCE = new Source(SlotType.SOURCE, SlotType.CRACKED);
    public static final SlotFromDefinition CRACKED_SLAB = new Slab(SlotType.CRACKED, SlotType.CRACKED_SLAB);
    public static final SlotFromDefinition CRACKED_STAIRS = new Stairs(SlotType.CRACKED, SlotType.CRACKED_STAIRS);
    public static final SlotFromDefinition CRACKED_WALL = new Wall(SlotType.CRACKED, SlotType.CRACKED_WALL);

    public static final SlotFromDefinition CHISELED_BRICK_SOURCE = new Source(SlotType.BRICK, SlotType.CHISELED);
    public static final SlotFromDefinition CHISELED_SOURCE = new Source(SlotType.SOURCE, SlotType.CHISELED);
    public static final SlotFromDefinition CHISELED_SLAB = new Slab(SlotType.CHISELED, SlotType.CHISELED_SLAB);
    public static final SlotFromDefinition CHISELED_STAIRS = new Stairs(SlotType.CHISELED, SlotType.CHISELED_STAIRS);
    public static final SlotFromDefinition CHISELED_WALL = new Wall(SlotType.CHISELED, SlotType.CHISELED_WALL);

    public static final SlotFromDefinition POLISHED_BRICK_SOURCE = new Source(SlotType.BRICK, SlotType.POLISHED);
    public static final SlotFromDefinition POLISHED_SOURCE = new Source(SlotType.SOURCE, SlotType.POLISHED);
    public static final SlotFromDefinition POLISHED_SLAB = new Slab(SlotType.POLISHED, SlotType.POLISHED_SLAB);
    public static final SlotFromDefinition POLISHED_STAIRS = new Stairs(SlotType.POLISHED, SlotType.POLISHED_STAIRS);
    public static final SlotFromDefinition POLISHED_WALL = new Wall(SlotType.POLISHED, SlotType.POLISHED_WALL);
}
