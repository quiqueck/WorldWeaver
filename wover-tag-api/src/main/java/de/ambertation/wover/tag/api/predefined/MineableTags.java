package de.ambertation.wover.tag.api.predefined;

import de.ambertation.wover.tag.api.TagManager;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

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
     * {@code wover:mineable/shears}
     */
    public static final TagKey<Block> SHEARS = TagManager.BLOCKS.makeWorldWeaverTag("mineable/shears");
    /**
     * {@code minecraft:mineable/shovel}
     */
    public static final TagKey<Block> SHOVEL = BlockTags.MINEABLE_WITH_SHOVEL;
    /**
     * {@code wover:mineable/sword}
     */
    public static final TagKey<Block> SWORD = TagManager.BLOCKS.makeWorldWeaverTag("mineable/sword");
    /**
     * {@code wover:mineable/hammer}
     */
    public static final TagKey<Block> HAMMER = TagManager.BLOCKS.makeWorldWeaverTag("mineable/hammer");

    /**
     * {@code wover:needs_netherite_tool}
     */
    public static final TagKey<Block> NEEDS_NETHERITE_TOOL = TagManager.BLOCKS.makeWorldWeaverTag("needs_netherite_tool");
    /**
     * {@code wover:needs_gold_tool}
     */
    public static final TagKey<Block> NEEDS_GOLD_TOOL = TagManager.BLOCKS.makeWorldWeaverTag("needs_gold_tool");
    /**
     * {@code wover:needs_wood_tool}
     */
    public static final TagKey<Block> NEEDS_WOOD_TOOL = TagManager.BLOCKS.makeWorldWeaverTag("needs_wood_tool");

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
