package com.betterxlib.api.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/**
 * A base slab block that can be created from a source block.
 */
public class BaseSlabBlock extends SlabBlock {

    public BaseSlabBlock(Properties properties) {
        super(properties);
    }

    /**
     * Create a slab block with properties copied from a source block.
     *
     * @param source the source block to copy properties from
     * @return a new BaseSlabBlock
     */
    public static BaseSlabBlock from(Block source) {
        return new BaseSlabBlock(BlockBehaviour.Properties.ofFullCopy(source));
    }
}
