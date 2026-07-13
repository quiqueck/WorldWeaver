package org.betterx.wover.block.api.trait.behaviour;

import org.betterx.wover.block.api.trait.GenericBlockTrait;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

/**
 * A {@link GenericBlockTrait} that adds a vanilla {@code minecraft:mineable/...}-style tool tag to a block,
 * marking which tool category is required (or the correct-tool tier needed) to get full drops.
 */
public interface MineableWithTagTrait extends GenericBlockTrait {
    /**
     * Builds {@link MineableWithTagTrait} instances for the standard vanilla tool/tier tags.
     */
    interface Builder extends GenericBlockTrait.Builder {
        /** @return a trait requiring a pickaxe */
        @Nullable MineableWithTagTrait needsPickAxe();
        /** @return a trait requiring an axe */
        @Nullable MineableWithTagTrait needsAxe();
        /** @return a trait requiring a hoe */
        @Nullable MineableWithTagTrait needsHoe();
        /** @return a trait requiring a shovel */
        @Nullable MineableWithTagTrait needsShovel();
        /** @return a trait requiring shears */
        @Nullable MineableWithTagTrait needsShears();
        /** @return a trait requiring a sword */
        @Nullable MineableWithTagTrait needsSword();
        /** @return a trait requiring a hammer */
        @Nullable MineableWithTagTrait needsHammer();
        /** @return a trait requiring at least a netherite tool tier */
        @Nullable MineableWithTagTrait needsNetheriteTool();
        /** @return a trait requiring at least a gold tool tier */
        @Nullable MineableWithTagTrait needsGoldTool();
        /** @return a trait requiring at least a wood tool tier */
        @Nullable MineableWithTagTrait needsWoodTool();
    }

    /**
     * @return the block tag added by this trait
     */
    TagKey<Block> mineableTag();
}
