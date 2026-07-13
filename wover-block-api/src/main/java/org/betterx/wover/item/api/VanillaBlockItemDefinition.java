package org.betterx.wover.item.api;

import org.betterx.wover.block.api.BlockDefinition;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

/**
 * The default {@link BlockItemDefinition}, creating a plain {@link BlockItem} for the block. Used
 * automatically by {@link BlockDefinition#buildAndRegister()} unless the definition was configured with a
 * custom {@link BlockDefinition.BlockItemDefinitionFactory} via {@link BlockDefinition#withBlockItem} or
 * {@link BlockDefinition#noBlockItem()}.
 */
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
