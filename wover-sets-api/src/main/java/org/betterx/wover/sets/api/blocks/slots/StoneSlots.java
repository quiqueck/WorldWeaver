package org.betterx.wover.sets.api.blocks.slots;

import org.betterx.wover.sets.api.blocks.SlotDefinition;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.sets.api.blocks.types.Slab;
import org.betterx.wover.sets.api.blocks.types.Source;
import org.betterx.wover.sets.api.blocks.types.Stairs;
import org.betterx.wover.sets.api.blocks.types.Wall;

public class StoneSlots {
    public static final SlotDefinition SOURCE = new Source();
    public static final SlotDefinition SLAB = new Slab();
    public static final SlotDefinition STAIRS = new Stairs();
    public static final SlotDefinition WALL = new Wall();

    public static final SlotDefinition BRICK_SOURCE = new Source(SlotType.SOURCE, SlotType.BRICK);
    public static final SlotDefinition BRICK_SLAB = new Slab(SlotType.BRICK, SlotType.BRICK_SLAB);
    public static final SlotDefinition BRICK_STAIRS = new Stairs(SlotType.BRICK, SlotType.BRICK_STAIRS);
    public static final SlotDefinition BRICK_WALL = new Wall(SlotType.BRICK, SlotType.BRICK_WALL);

    public static final SlotDefinition WEATHERED_BRICK_SOURCE = new Source(SlotType.BRICK, SlotType.WEATHERED);
    public static final SlotDefinition WEATHERED_SOURCE = new Source(SlotType.SOURCE, SlotType.WEATHERED);
    public static final SlotDefinition WEATHERED_SLAB = new Slab(SlotType.WEATHERED, SlotType.WEATHERED_SLAB);
    public static final SlotDefinition WEATHERED_STAIRS = new Stairs(SlotType.WEATHERED, SlotType.WEATHERED_STAIRS);
    public static final SlotDefinition WEATHERED_WALL = new Wall(SlotType.WEATHERED, SlotType.WEATHERED_WALL);

    public static final SlotDefinition CRACKED_BRICK_SOURCE = new Source(SlotType.BRICK, SlotType.CRACKED);
    public static final SlotDefinition CRACKED_SOURCE = new Source(SlotType.SOURCE, SlotType.CRACKED);
    public static final SlotDefinition CRACKED_SLAB = new Slab(SlotType.CRACKED, SlotType.CRACKED_SLAB);
    public static final SlotDefinition CRACKED_STAIRS = new Stairs(SlotType.CRACKED, SlotType.CRACKED_STAIRS);
    public static final SlotDefinition CRACKED_WALL = new Wall(SlotType.CRACKED, SlotType.CRACKED_WALL);

    public static final SlotDefinition CHISELED_BRICK_SOURCE = new Source(SlotType.BRICK, SlotType.CHISELED);
    public static final SlotDefinition CHISELED_SOURCE = new Source(SlotType.SOURCE, SlotType.CHISELED);
    public static final SlotDefinition CHISELED_SLAB = new Slab(SlotType.CHISELED, SlotType.CHISELED_SLAB);
    public static final SlotDefinition CHISELED_STAIRS = new Stairs(SlotType.CHISELED, SlotType.CHISELED_STAIRS);
    public static final SlotDefinition CHISELED_WALL = new Wall(SlotType.CHISELED, SlotType.CHISELED_WALL);

    public static final SlotDefinition POLISHED_BRICK_SOURCE = new Source(SlotType.BRICK, SlotType.POLISHED);
    public static final SlotDefinition POLISHED_SOURCE = new Source(SlotType.SOURCE, SlotType.POLISHED);
    public static final SlotDefinition POLISHED_SLAB = new Slab(SlotType.POLISHED, SlotType.POLISHED_SLAB);
    public static final SlotDefinition POLISHED_STAIRS = new Stairs(SlotType.POLISHED, SlotType.POLISHED_STAIRS);
    public static final SlotDefinition POLISHED_WALL = new Wall(SlotType.POLISHED, SlotType.POLISHED_WALL);
}
