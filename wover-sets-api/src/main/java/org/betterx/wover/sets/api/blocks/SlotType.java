package org.betterx.wover.sets.api.blocks;

public record SlotType(String suffix) {
    public static final SlotType SOURCE = new SlotType("source");

    // Common Slot Types
    public static final SlotType SLAB = new SlotType("slab");
    public static final SlotType STAIRS = new SlotType("stairs");
    public static final SlotType WALL = new SlotType("wall");

    // Stone Specific Slot Types
    public static final SlotType CRACKED = new SlotType("cracked");
    public static final SlotType CRACKED_SLAB = new SlotType("cracked_slab");
    public static final SlotType CRACKED_STAIRS = new SlotType("cracked_stairs");
    public static final SlotType CRACKED_WALL = new SlotType("cracked_wall");

    public static final SlotType WEATHERED = new SlotType("weathered");
    public static final SlotType WEATHERED_SLAB = new SlotType("weathered_slab");
    public static final SlotType WEATHERED_STAIRS = new SlotType("weathered_stairs");
    public static final SlotType WEATHERED_WALL = new SlotType("weathered_wall");

    public static final SlotType BRICK = new SlotType("brick");
    public static final SlotType BRICK_SLAB = new SlotType("brick_slab");
    public static final SlotType BRICK_STAIRS = new SlotType("brick_stairs");
    public static final SlotType BRICK_WALL = new SlotType("brick_wall");

    public static final SlotType CHISELED = new SlotType("chiseled");
    public static final SlotType CHISELED_SLAB = new SlotType("chiseled_slab");
    public static final SlotType CHISELED_STAIRS = new SlotType("chiseled_stairs");
    public static final SlotType CHISELED_WALL = new SlotType("chiseled_wall");

    public static final SlotType POLISHED = new SlotType("polished");
    public static final SlotType POLISHED_SLAB = new SlotType("polished_slab");
    public static final SlotType POLISHED_STAIRS = new SlotType("polished_stairs");
    public static final SlotType POLISHED_WALL = new SlotType("polished_wall");


    // Wood Specific Slot Types
    public static final SlotType STRIPPED_LOG = new SlotType("stripped_log");
    public static final SlotType STRIPPED_BARK = new SlotType("stripped_bark");
    public static final SlotType LOG = new SlotType("log");
    public static final SlotType BARK = new SlotType("bark");
    public static final SlotType PLANKS = new SlotType("planks");
    public static final SlotType FENCE = new SlotType("fence");
    public static final SlotType GATE = new SlotType("gate");
    public static final SlotType BUTTON = new SlotType("button");
    public static final SlotType PRESSURE_PLATE = new SlotType("plate");
    public static final SlotType TRAPDOOR = new SlotType("trapdoor");
    public static final SlotType DOOR = new SlotType("door");
    public static final SlotType LADDER = new SlotType("ladder");
    public static final SlotType SIGN = new SlotType("sign");
    public static final SlotType HANGING_SIGN = new SlotType("hanging_sign");
    public static final SlotType CHEST = new SlotType("chest");
    public static final SlotType BARREL = new SlotType("barrel");
    public static final SlotType CRAFTING_TABLE = new SlotType("crafting_table");
    public static final SlotType BOOKSHELF = new SlotType("bookshelf");
    public static final SlotType COMPOSTER = new SlotType("composter");
    public static final SlotType BOAT = new SlotType("boat");
    public static final SlotType CHEST_BOAT = new SlotType("chest_boat");
    public static final SlotType TABURET = new SlotType("taburet");
    public static final SlotType CHAIR = new SlotType("chair");
    public static final SlotType BAR_STOOL = new SlotType("bar_stool");
}
