package de.ambertation.wover.tag.api.predefined;

import de.ambertation.wover.tag.api.TagManager;

import net.minecraft.tags.BlockTags;
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
    public static final TagKey<Block> BARREL = TagManager.BLOCKS.makeCommonTag("barrels");
    /**
     * {@code c:bookshelves}
     */
    public static final TagKey<Block> BOOKSHELVES = TagManager.BLOCKS.makeCommonTag("bookshelves");
    /**
     * {@code c:chest}
     */
    public static final TagKey<Block> CHEST = TagManager.BLOCKS.makeCommonTag("chests");
    /**
     * {@code wover:composters}
     */
    public static final TagKey<Block> COMPOSTER = TagManager.BLOCKS.makeWorldWeaverTag("composters");

    /**
     * {@code wover:cauldrons}
     */
    public static final TagKey<Block> CAULDRONS = TagManager.BLOCKS.makeWorldWeaverTag("cauldrons");

    /**
     * {@code wover:beds}
     */
    public static final TagKey<Block> BEDS = TagManager.BLOCKS.makeWorldWeaverTag("beds");


    /**
     * {@code c:bookshelves}
     */
    public static final TagKey<Block> ENCHANTING_MAGIC_SOURCE = TagManager.BLOCKS.makeWorldWeaverTag("magic_source");
    /**
     * {@code wover:surfaces/end/stones}
     */
    public static final TagKey<Block> END_STONES = TagManager.BLOCKS.makeWorldWeaverTag("surfaces/end/stones");
    /**
     * {@code c:immobile}
     */
    public static final TagKey<Block> IMMOBILE = TagManager.BLOCKS.makeWorldWeaverTag("immobile");
    /**
     * {@code c:leaves}
     */
    public static final TagKey<Block> LEAVES = TagManager.BLOCKS.makeWorldWeaverTag("vegetation/leaves");
    /**
     * {@code wover:surfaces/nether/netherrack}
     */
    public static final TagKey<Block> NETHERRACK = TagManager.BLOCKS.makeWorldWeaverTag("surfaces/nether/netherrack");
    /**
     * {@code wover:surfaces/mycelium}
     */
    public static final TagKey<Block> MYCELIUM = TagManager.BLOCKS.makeWorldWeaverTag("surfaces/mycelium");
    /**
     * {@code wover:surfaces/nether/mycelium}
     */
    public static final TagKey<Block> NETHER_MYCELIUM = TagManager.BLOCKS.makeWorldWeaverTag("surfaces/nether/mycelium");
    /**
     * {@code wover:nether_pframe}
     */
    public static final TagKey<Block> NETHER_PORTAL_FRAME = TagManager.BLOCKS.makeWorldWeaverTag("nether_pframe");
    /**
     * {@code wover:surfaces/nether/stones}
     */
    public static final TagKey<Block> NETHER_STONES = TagManager.BLOCKS.makeWorldWeaverTag("surfaces/nether/stones");
    /**
     * {@code wover:ores/nether}
     */
    public static final TagKey<Block> NETHER_ORES = TagManager.BLOCKS.makeWorldWeaverTag("ores/nether");
    /**
     * {@code c:ores}
     */
    public static final TagKey<Block> ORES = TagManager.BLOCKS.makeCommonTag("ores");
    /**
     * {@code wover:ores/end}
     */
    public static final TagKey<Block> END_ORES = TagManager.BLOCKS.makeWorldWeaverTag("ores/end");
    /**
     * {@code wover:vegetation/saplings}
     */
    public static final TagKey<Block> SAPLINGS = TagManager.BLOCKS.makeWorldWeaverTag("vegetation/saplings");
    /**
     * {@code wover:vegetation/seeds}
     */
    public static final TagKey<Block> SEEDS = TagManager.BLOCKS.makeWorldWeaverTag("vegetation/seeds");
    /**
     * {@code wover:surfaces/soul_ground}
     */
    public static final TagKey<Block> SOUL_GROUND = TagManager.BLOCKS.makeWorldWeaverTag("surfaces/soul_ground");
    /**
     * {@code wover:surfaces/sculk_like}
     */
    public static final TagKey<Block> SCULK_LIKE = TagManager.BLOCKS.makeWorldWeaverTag("surfaces/sculk_like");
    /**
     * {@code c:barrels/wooden}
     */
    public static final TagKey<Block> WOODEN_BARREL = TagManager.BLOCKS.makeCommonTag("barrels/wooden");
    /**
     * {@code c:chests/wooden}
     */
    public static final TagKey<Block> WOODEN_CHEST = TagManager.BLOCKS.makeCommonTag("chests/wooden");
    /**
     * {@code wover:composters/wooden}
     */
    public static final TagKey<Block> WOODEN_COMPOSTER = TagManager.BLOCKS.makeWorldWeaverTag("composters/wooden");
    /**
     * {@code wover:workbench}
     */
    public static final TagKey<Block> WORKBENCHES = TagManager.BLOCKS.makeWorldWeaverTag("workbench");
    /**
     * {@code minecraft:dragon_immune}
     */
    public static final TagKey<Block> DRAGON_IMMUNE = BlockTags.DRAGON_IMMUNE;
    /**
     * {@code wover:is_obsidian}
     */
    public static final TagKey<Block> IS_OBSIDIAN = TagManager.BLOCKS.makeWorldWeaverTag("is_obsidian");
    /**
     * {@code wover:surfaces/terrain}
     */
    public static final TagKey<Block> TERRAIN = TagManager.BLOCKS.makeWorldWeaverTag("surfaces/terrain");

    /**
     * {@code wover:surfaces/soil} - plantable ground: dirt, farmland, grass and mod soils
     * (moss, mycelium, ...). The kind of block a small plant can be planted on.
     */
    public static final TagKey<Block> SOIL = TagManager.BLOCKS.makeWorldWeaverTag("surfaces/soil");
    /**
     * {@code wover:surfaces/soil_or_logs}
     */
    public static final TagKey<Block> SOIL_OR_LOGS = TagManager.BLOCKS.makeWorldWeaverTag("surfaces/soil_or_logs");
    /**
     * {@code wover:surfaces/nether/terrain}
     */
    public static final TagKey<Block> NETHER_TERRAIN = TagManager.BLOCKS.makeWorldWeaverTag("surfaces/nether/terrain");
    /**
     * {@code c:budding_blocks}
     */
    public static final TagKey<Block> BUDDING_BLOCKS = TagManager.BLOCKS.makeCommonTag("budding_blocks");
    /**
     * {@code wover:vegetation/water_plant}
     */
    public static final TagKey<Block> WATER_PLANT = TagManager.BLOCKS.makeWorldWeaverTag("vegetation/water_plant");
    /**
     * {@code wover:vegetation/plant}
     */
    public static final TagKey<Block> PLANT = TagManager.BLOCKS.makeWorldWeaverTag("vegetation/plant");
    /**
     * {@code wover:vegetation/vine}
     */
    public static final TagKey<Block> VINE = TagManager.BLOCKS.makeWorldWeaverTag("vegetation/vine");


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
