package org.betterx.wover.tag.api.predefined;

import org.betterx.wover.tag.api.TagManager;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import org.jetbrains.annotations.ApiStatus;

/**
 * Community item tags.
 */
public class CommonItemTags {
    /**
     * {@code c:hammers}
     */
    public final static TagKey<Item> HAMMERS = TagManager.ITEMS.makeCommonTag("hammers");
    /**
     * {@code c:barrel}
     */
    public static final TagKey<Item> BARREL = TagManager.ITEMS.makeCommonTag("barrel");
    /**
     * {@code c:chest}
     */
    public static final TagKey<Item> CHEST = TagManager.ITEMS.makeCommonTag("chest");
    /**
     * {@code c:shears}
     */
    public static final TagKey<Item> SHEARS = TagManager.ITEMS.makeCommonTag("shears");
    /**
     * {@code c:furnaces}
     */
    public static final TagKey<Item> FURNACES = TagManager.ITEMS.makeCommonTag("furnaces");
    /**
     * {@code c:iron_ingots}
     */
    public static final TagKey<Item> IRON_INGOTS = TagManager.ITEMS.makeCommonTag("iron_ingots");
    /**
     * {@code c:leaves}
     */
    public static final TagKey<Item> LEAVES = TagManager.ITEMS.makeCommonTag("leaves");
    /**
     * {@code c:saplings}
     */
    public static final TagKey<Item> SAPLINGS = TagManager.ITEMS.makeCommonTag("saplings");
    /**
     * {@code c:seeds}
     */
    public static final TagKey<Item> SEEDS = TagManager.ITEMS.makeCommonTag("seeds");
    /**
     * {@code c:soul_ground}
     */
    public static final TagKey<Item> SOUL_GROUND = TagManager.ITEMS.makeCommonTag("soul_ground");
    /**
     * {@code c:wooden_barrels}
     */
    public static final TagKey<Item> WOODEN_BARREL = TagManager.ITEMS.makeCommonTag("wooden_barrels");
    /**
     * {@code c:wooden_chests}
     */
    public static final TagKey<Item> WOODEN_CHEST = TagManager.ITEMS.makeCommonTag("wooden_chests");
    /**
     * {@code c:workbench}
     */
    public static final TagKey<Item> WORKBENCHES = TagManager.ITEMS.makeCommonTag("workbench");

    /**
     * {@code c:water_bottles}
     */
    public static final TagKey<Item> WATER_BOTTLES = TagManager.ITEMS.makeCommonTag("water_bottles");
    /**
     * {@code c:compostable}
     */
    public static final TagKey<Item> COMPOSTABLE = TagManager.ITEMS.makeCommonTag("compostable");

    /**
     * Called internally to ensure that the tags are created.
     */
    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        // NO-OP
    }

    private CommonItemTags() {
    }
}
