package com.betterxlib.api.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * A base wall block that can be created from a source block.
 */
public class BaseWallBlock extends WallBlock {

    public BaseWallBlock(Properties properties) {
        super(properties);
    }

    /**
     * Create a wall block with properties copied from a source block.
     *
     * @param source the source block to copy properties from
     * @return a new BaseWallBlock
     */
    public static BaseWallBlock from(Block source) {
        return new BaseWallBlock(BlockBehaviour.Properties.ofFullCopy(source));
    }
}
