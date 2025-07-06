package org.betterx.wover.sets.api.blocks.slots;

import org.betterx.wover.sets.api.blocks.SlotFromDefinition;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.sets.api.blocks.types.Slab;
import org.betterx.wover.sets.api.blocks.types.Source;
import org.betterx.wover.sets.api.blocks.types.Stairs;
import org.betterx.wover.sets.api.blocks.types.Wall;

public class StoneSlots {
    public static final SlotFromDefinition SOURCE = new Source();
    public static final SlotFromDefinition SLAB = new Slab();
    public static final SlotFromDefinition STAIRS = new Stairs();
    public static final SlotFromDefinition WALL = new Wall();

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
