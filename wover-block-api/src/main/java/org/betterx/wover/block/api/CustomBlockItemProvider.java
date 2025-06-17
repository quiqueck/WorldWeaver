package org.betterx.wover.block.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

/**
 * Will be deprecated in after Wover 21.6.x
 */
@Deprecated(forRemoval = true)
public interface CustomBlockItemProvider {
    /**
     * Used to generate a custom Block Item when a block is registered to the {@link BlockRegistry}.
     * <p>
     * Will be deprecated in after Wover 21.6.x
     *
     * @return {@link BlockItem}
     */
    @Deprecated(forRemoval = true)
    BlockItem getCustomBlockItem(ResourceLocation blockID, Item.Properties settings);
}
