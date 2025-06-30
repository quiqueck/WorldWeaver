package org.betterx.wover.item.api;

import org.betterx.wover.block.api.BlockDefinition;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class VanillaBlockItemDefinition extends BlockItemDefinition<BlockItem, VanillaBlockItemDefinition> {
    /**
     * Creates a new block-item definition for a standard BlockItem with default properties.
     *
     * @param sourceDefinition The block definition that this item is created for
     * @param sourceBlock      The block that this item is created for
     */
    public VanillaBlockItemDefinition(
            BlockDefinition<?, ?> sourceDefinition,
            Block sourceBlock
    ) {
        super(
                sourceDefinition,
                definition -> new BlockItem(sourceBlock, definition.getProperties())
        );
    }
}
