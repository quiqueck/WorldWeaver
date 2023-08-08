package org.betterx.wover.block.api.predicate;

import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.material.Fluids;

/**
 * Additional block predicates
 */
public class BlockPredicates {
    /**
     * Tests if the block has the {@link BlockTags#NYLIUM} tag
     */
    public static final BlockPredicate ONLY_NYLIUM = BlockPredicate.matchesTag(BlockTags.NYLIUM);

    /**
     * Tests if the block has the {@link CommonBlockTags#MYCELIUM} tag
     */
    public static final BlockPredicate ONLY_MYCELIUM = BlockPredicate.matchesTag(CommonBlockTags.MYCELIUM);

    /**
     * Tests if the block is either {@link Blocks#GRAVEL}, {@link Blocks#SAND} or {@link Blocks#RED_SAND}
     */
    public static final BlockPredicate ONLY_GRAVEL_OR_SAND = BlockPredicate.matchesBlocks(
            Blocks.GRAVEL,
            Blocks.SAND,
            Blocks.RED_SAND
    );

    /**
     * Tests id the block has the {@link CommonBlockTags#SOUL_GROUND} tag
     */
    public static final BlockPredicate ONLY_SOUL_GROUND = BlockPredicate.matchesTag(CommonBlockTags.SOUL_GROUND);

    /**
     * Tests if the block has the {@link CommonBlockTags#NETHER_TERRAIN} tag
     */
    public static final BlockPredicate ONLY_NETHER_GROUND = BlockPredicate.matchesTag(CommonBlockTags.NETHER_TERRAIN);

    /**
     * Tests if the block is either {@link #ONLY_NETHER_GROUND} or {@link Blocks#BASALT}
     */
    public static final BlockPredicate ONLY_NETHER_GROUND_AND_BASALT = BlockPredicate.anyOf(
            ONLY_NETHER_GROUND,
            BlockPredicate.matchesBlocks(Blocks.BASALT)
    );

    /**
     * Tests if the block has the {@link CommonBlockTags#TERRAIN} tag
     */
    public static final BlockPredicate ONLY_GROUND = BlockPredicate.matchesTag(CommonBlockTags.TERRAIN);

    /**
     * Tests if the block is a lava fluid
     */
    public static final BlockPredicate ONLY_LAVA = BlockPredicate.matchesFluids(Fluids.LAVA);

    /**
     * Tests if the block is either {@link #ONLY_GROUND} or {@link #ONLY_LAVA}
     */
    public static final BlockPredicate ONLY_GROUND_OR_LAVA = BlockPredicate.anyOf(
            ONLY_GROUND,
            ONLY_LAVA
    );

    public static final BlockPredicate IS_FULL_BLOCK = IsFullShape.HERE;
}
