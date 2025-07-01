package org.betterx.wover.block.api.trait.behaviour;

import org.betterx.wover.block.api.trait.GenericBlockTrait;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

public interface MineableWithTagTrait extends GenericBlockTrait {
    interface Builder extends GenericBlockTrait.Builder {
        @Nullable MineableWithTagTrait needsPickAxe();
        @Nullable MineableWithTagTrait needsAxe();
        @Nullable MineableWithTagTrait needsHoe();
        @Nullable MineableWithTagTrait needsShovel();
        @Nullable MineableWithTagTrait needsShears();
        @Nullable MineableWithTagTrait needsSword();
        @Nullable MineableWithTagTrait needsHammer();
        @Nullable MineableWithTagTrait needsNetheriteTool();
        @Nullable MineableWithTagTrait needsGoldTool();
        @Nullable MineableWithTagTrait needsWoodTool();
    }

    TagKey<Block> mineableTag();
}
