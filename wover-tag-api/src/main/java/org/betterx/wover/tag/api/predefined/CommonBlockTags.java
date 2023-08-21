package org.betterx.wover.tag.api.predefined;

import org.betterx.wover.tag.api.TagManager;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.ApiStatus;

/**
 * Community block tags.
 */
public class CommonBlockTags {
    /**
     * {@code c:barrel}
     */
    public static final TagKey<Block> BARREL = TagManager.BLOCKS.makeCommonTag("barrel");
    /**
     * {@code c:bookshelves}
     */
    public static final TagKey<Block> BOOKSHELVES = TagManager.BLOCKS.makeCommonTag("bookshelves");
    /**
     * {@code c:chest}
     */
    public static final TagKey<Block> CHEST = TagManager.BLOCKS.makeCommonTag("chest");
    /**
     * {@code c:composter}
     */
    public static final TagKey<Block> COMPOSTER = TagManager.BLOCKS.makeCommonTag("composter");
    /**
     * {@code c:end_stones}
     */
    public static final TagKey<Block> END_STONES = TagManager.BLOCKS.makeCommonTag("end_stones");
    /**
     * {@code c:immobile}
     */
    public static final TagKey<Block> IMMOBILE = TagManager.BLOCKS.makeCommonTag("immobile");
    /**
     * {@code c:leaves}
     */
    public static final TagKey<Block> LEAVES = TagManager.BLOCKS.makeCommonTag("leaves");
    /**
     * {@code c:netherrack}
     */
    public static final TagKey<Block> NETHERRACK = TagManager.BLOCKS.makeCommonTag("netherrack");
    /**
     * {@code c:mycelium}
     */
    public static final TagKey<Block> MYCELIUM = TagManager.BLOCKS.makeCommonTag("mycelium");
    /**
     * {@code c:nether_mycelium}
     */
    public static final TagKey<Block> NETHER_MYCELIUM = TagManager.BLOCKS.makeCommonTag("nether_mycelium");
    /**
     * {@code c:nether_pframe}
     */
    public static final TagKey<Block> NETHER_PORTAL_FRAME = TagManager.BLOCKS.makeCommonTag("nether_pframe");
    /**
     * {@code c:nether_stones}
     */
    public static final TagKey<Block> NETHER_STONES = TagManager.BLOCKS.makeCommonTag("nether_stones");
    /**
     * {@code c:nether_ores}
     */
    public static final TagKey<Block> NETHER_ORES = TagManager.BLOCKS.makeCommonTag("nether_ores");
    /**
     * {@code c:ores}
     */
    public static final TagKey<Block> ORES = TagManager.BLOCKS.makeCommonTag("ores");
    /**
     * {@code c:end_ores}
     */
    public static final TagKey<Block> END_ORES = TagManager.BLOCKS.makeCommonTag("end_ores");
    /**
     * {@code c:saplings}
     */
    public static final TagKey<Block> SAPLINGS = TagManager.BLOCKS.makeCommonTag("saplings");
    /**
     * {@code c:seeds}
     */
    public static final TagKey<Block> SEEDS = TagManager.BLOCKS.makeCommonTag("seeds");
    /**
     * {@code c:soul_ground}
     */
    public static final TagKey<Block> SOUL_GROUND = TagManager.BLOCKS.makeCommonTag("soul_ground");
    /**
     * {@code c:sculk_like}
     */
    public static final TagKey<Block> SCULK_LIKE = TagManager.BLOCKS.makeCommonTag("sculk_like");
    /**
     * {@code c:wooden_barrels}
     */
    public static final TagKey<Block> WOODEN_BARREL = TagManager.BLOCKS.makeCommonTag("wooden_barrels");
    /**
     * {@code c:wooden_chests}
     */
    public static final TagKey<Block> WOODEN_CHEST = TagManager.BLOCKS.makeCommonTag("wooden_chests");
    /**
     * {@code c:wooden_composter}
     */
    public static final TagKey<Block> WOODEN_COMPOSTER = TagManager.BLOCKS.makeCommonTag("wooden_composter");
    /**
     * {@code c:workbench}
     */
    public static final TagKey<Block> WORKBENCHES = TagManager.BLOCKS.makeCommonTag("workbench");
    /**
     * {@code c:dragon_immune}
     */
    public static final TagKey<Block> DRAGON_IMMUNE = TagManager.BLOCKS.makeCommonTag("dragon_immune");
    /**
     * {@code c:is_obsidian}
     */
    public static final TagKey<Block> IS_OBSIDIAN = TagManager.BLOCKS.makeCommonTag("is_obsidian");
    /**
     * {@code c:terrain}
     */
    public static final TagKey<Block> TERRAIN = TagManager.BLOCKS.makeCommonTag("terrain");

    /**
     * {@code c:grows_grass}
     */
    public static final TagKey<Block> GROWS_GRASS = TagManager.BLOCKS.makeCommonTag("grows_grass");
    /**
     * {@code c:nether_terrain}
     */
    public static final TagKey<Block> NETHER_TERRAIN = TagManager.BLOCKS.makeCommonTag("nether_terrain");
    /**
     * {@code c:budding_blocks}
     */
    public static final TagKey<Block> BUDDING_BLOCKS = TagManager.BLOCKS.makeCommonTag("budding_blocks");
    /**
     * {@code c:water_plant}
     */
    public static final TagKey<Block> WATER_PLANT = TagManager.BLOCKS.makeCommonTag("water_plant");
    /**
     * {@code c:plant}
     */
    public static final TagKey<Block> PLANT = TagManager.BLOCKS.makeCommonTag("plant");

    /**
     * Called internally to ensure that the tags are created.
     */
    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        // NO-OP
    }

    private CommonBlockTags() {
    }
}
