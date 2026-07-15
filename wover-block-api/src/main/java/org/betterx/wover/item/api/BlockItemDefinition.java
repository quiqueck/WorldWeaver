package org.betterx.wover.item.api;

import org.betterx.wover.block.api.BlockDefinition;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

/**
 * {@link org.betterx.wover.item.api.ItemDefinition} specialization for {@link BlockItem}s created from a
 * {@link BlockDefinition}. Instances are usually not created directly - {@link BlockDefinition#buildAndRegister()}
 * uses {@link VanillaBlockItemDefinition} by default, unless the definition was configured with a custom
 * {@link BlockDefinition.BlockItemDefinitionFactory} via {@link BlockDefinition#withBlockItem}.
 *
 * @param <BI> The type of {@link BlockItem} being created
 * @param <D>  The concrete configuration class type for method chaining
 */
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
        // Block items share their block's translation key by default (matching pre-1.21.2 BlockItem
        // behaviour, where getDescriptionId() always delegated to the block). Without this, Item.Properties
        // defaults to an "item."-prefixed key that no block-item ever has an entry for. Callers that want a
        // distinct item-only key can still opt back out via useItemDescriptionPrefix().
        this.useBlockDescriptionPrefix();
    }

    @Override
    protected void beforeBuild() {
    }

    @Override
    protected BI beforeRegister(BI item) {
        return item;
    }
}
