package org.betterx.wover.tag.api.predefined;

import org.betterx.wover.tag.api.TagManager;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import net.fabricmc.fabric.api.mininglevel.v1.FabricMineableTags;

import org.jetbrains.annotations.ApiStatus;


/**
 * Mineable tags for blocks.
 * <p>
 * This class contains tags for blocks that can be mined with a specific tool.
 */
public class MineableTags {
    /**
     * {@code minecraft:mineable/axe}
     */
    public static final TagKey<Block> AXE = BlockTags.MINEABLE_WITH_AXE;
    /**
     * {@code minecraft:mineable/hoe}
     */
    public static final TagKey<Block> HOE = BlockTags.MINEABLE_WITH_HOE;
    /**
     * {@code minecraft:mineable/pickaxe}
     */
    public static final TagKey<Block> PICKAXE = BlockTags.MINEABLE_WITH_PICKAXE;
    /**
     * {@code fabric:mineable/shears}
     */
    public static final TagKey<Block> SHEARS = FabricMineableTags.SHEARS_MINEABLE;
    /**
     * {@code minecraft:mineable/shovel}
     */
    public static final TagKey<Block> SHOVEL = BlockTags.MINEABLE_WITH_SHOVEL;
    /**
     * {@code fabric:mineable/sword}
     */
    public static final TagKey<Block> SWORD = FabricMineableTags.SWORD_MINEABLE;
    /**
     * {@code c:mineable/hammer}
     */
    public static final TagKey<Block> HAMMER = TagManager.BLOCKS.makeCommonTag("mineable/hammer");

    /**
     * {@code c:needs_netherite_tool}
     */
    public static final TagKey<Block> NEEDS_NETHERITE_TOOL = TagManager.BLOCKS.makeCommonTag("needs_netherite_tool");

    /**
     * Called internally to ensure that the tags are created.
     */
    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        // NO-OP
    }

    private MineableTags() {
    }
}
