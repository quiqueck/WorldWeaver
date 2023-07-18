package org.betterx.wover.tag.api.predefined;

import org.betterx.wover.tag.api.TagManager;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.ApiStatus;

/**
 * Community POI tags for villager workstations.
 */
public class CommonPoiTags {
    /**
     * {@code c:workstation/fisherman}
     * <p>
     * This tag is used to identify blocks that can be used as a workstation for a fisherman villager.
     */
    public static final TagKey<Block> FISHERMAN_WORKSTATION = TagManager.BLOCKS.makeCommonTag("workstation/fisherman");
    /**
     * {@code c:workstation/farmer}
     * <p>
     * This tag is used to identify blocks that can be used as a workstation for a farmer villager.
     */

    public static final TagKey<Block> FARMER_WORKSTATION = TagManager.BLOCKS.makeCommonTag("workstation/farmer");

    /**
     * Called internally to ensure that the tags are created.
     */
    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        // NO-OP
    }

    private CommonPoiTags() {
    }
}
