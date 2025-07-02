package org.betterx.wover.item.api;

import org.betterx.wover.block.api.BlockDefinition;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class BlockItemDefinition<BI extends BlockItem, D extends BlockItemDefinition<BI, D>> extends ItemDefinition<BI, D> {
    /**
     * Creates a new block-item configuration.
     * This is used in {@link BlockDefinition#getBlockItemDefinition(Block)} to create a block item for a
     * specific block definition
     *
     * @param sourceDefinition The block definition that this item is created for
     * @param itemFactory      The factory used to create the item instance
     */
    public BlockItemDefinition(
            BlockDefinition<?, ?> sourceDefinition,
            ItemFactory<BI, D> itemFactory
    ) {
        super(sourceDefinition.registry.itemRegistry(), sourceDefinition.itemKey(), itemFactory);
    }

    @Override
    protected void beforeBuild() {
    }

    @Override
    protected BI beforeRegister(BI item) {
        return item;
    }
}
