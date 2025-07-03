package org.betterx.wover.tag.api.predefined;

import org.betterx.wover.tag.api.TagManager;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import org.jetbrains.annotations.ApiStatus;

/**
 * Community item tags.
 */
public class CommonItemTags {
    /**
     * {@code wover:tools/hammers}
     */
    public final static TagKey<Item> HAMMERS = TagManager.ITEMS.makeWorldWeaverTag("tools/hammers");
    /**
     * {@code c:barrels}
     */
    public static final TagKey<Item> BARREL = TagManager.ITEMS.makeCommonTag("barrels");
    /**
     * {@code c:boats}
     */
    public static final TagKey<Item> BOAT = ItemTags.BOATS;
    /**
     * {@code c:chests}
     */
    public static final TagKey<Item> CHEST = TagManager.ITEMS.makeCommonTag("chests");
    /**
     * {@code c:tools/shears}
     */
    public static final TagKey<Item> SHEARS = TagManager.ITEMS.makeCommonTag("tools/shears");
    /**
     * {@code wover:furnaces}
     */
    public static final TagKey<Item> FURNACES = TagManager.ITEMS.makeWorldWeaverTag("furnaces");
    /**
     * {@code c:ingots/iron}
     */
    public static final TagKey<Item> IRON_INGOTS = TagManager.ITEMS.makeCommonTag("ingots/iron");
    /**
     * {@code c:ores}
     */
    public static final TagKey<Item> ORES = TagManager.ITEMS.makeCommonTag("ores");

    /**
     * {@code wover:vegetation/vines}
     */
    public static final TagKey<Item> VINES = TagManager.ITEMS.makeWorldWeaverTag("vegetation/vines");
    /**
     * {@code wover:vegetation/leaves}
     */
    public static final TagKey<Item> LEAVES = TagManager.ITEMS.makeWorldWeaverTag("vegetation/leaves");
    /**
     * {@code wover:vegetation/saplings}
     */
    public static final TagKey<Item> SAPLINGS = TagManager.ITEMS.makeWorldWeaverTag("vegetation/saplings");
    /**
     * {@code wover:vegetation/seeds}
     */
    public static final TagKey<Item> SEEDS = TagManager.ITEMS.makeWorldWeaverTag("vegetation/seeds");
    /**
     * {@code wover:surfaces/soul_ground}
     */
    public static final TagKey<Item> SOUL_GROUND = TagManager.ITEMS.makeWorldWeaverTag("surfaces/soul_ground");
    /**
     * {@code c:barrels/wooden}
     */
    public static final TagKey<Item> WOODEN_BARREL = TagManager.ITEMS.makeCommonTag("barrels/wooden");
    /**
     * {@code c:chests/wooden}
     */
    public static final TagKey<Item> WOODEN_CHEST = TagManager.ITEMS.makeCommonTag("chests/wooden");
    /**
     * {@code wover:workbench}
     */
    public static final TagKey<Item> WORKBENCHES = TagManager.ITEMS.makeWorldWeaverTag("workbench");

    /**
     * {@code wover:water_bottles}
     */
    public static final TagKey<Item> WATER_BOTTLES = TagManager.ITEMS.makeWorldWeaverTag("water_bottles");
    /**
     * {@code wover:compostable}
     */
    public static final TagKey<Item> COMPOSTABLE = TagManager.ITEMS.makeWorldWeaverTag("compostable");

    /**
     * {@code c:music_discs}
     */
    public static final TagKey<Item> MUSIC_DISCS = TagManager.ITEMS.makeCommonTag("music_discs");

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
