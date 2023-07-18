package org.betterx.wover.tag.api.predefined;

import org.betterx.wover.tag.api.TagManager;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import org.jetbrains.annotations.ApiStatus;


/**
 * Tags that are used to mark the type of a tool.
 */
public class ToolTags {
    /**
     * {@code fabric:axes}
     */
    public static final TagKey<Item> FABRIC_AXES = TagManager.ITEMS.makeFabricTag("axes");
    /**
     * {@code fabric:hoes}
     */
    public static final TagKey<Item> FABRIC_HOES = TagManager.ITEMS.makeFabricTag("hoes");
    /**
     * {@code fabric:pickaxes}
     */
    public static final TagKey<Item> FABRIC_PICKAXES = TagManager.ITEMS.makeFabricTag("pickaxes");
    /**
     * {@code fabric:shears}
     */
    public static final TagKey<Item> FABRIC_SHEARS = TagManager.ITEMS.makeFabricTag("shears");
    /**
     * {@code fabric:shovels}
     */
    public static final TagKey<Item> FABRIC_SHOVELS = TagManager.ITEMS.makeFabricTag("shovels");
    /**
     * {@code fabric:swords}
     */
    public static final TagKey<Item> FABRIC_SWORDS = TagManager.ITEMS.makeFabricTag("swords");

    /**
     * Called internally to ensure that the tags are created.
     */
    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        // NO-OP
    }

    private ToolTags() {
    }
}
