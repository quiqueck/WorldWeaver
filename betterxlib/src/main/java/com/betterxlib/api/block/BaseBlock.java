package com.betterxlib.api.block;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.List;

/**
 * A simple base block with configurable properties.
 * <p>
 * Provides a foundation for custom blocks with:
 * <ul>
 *     <li>Simplified construction</li>
 *     <li>Override hooks for common functionality</li>
 * </ul>
 */
public class BaseBlock extends Block {

    public BaseBlock(Properties properties) {
        super(properties);
    }

    /**
     * Create a base block with default stone-like properties.
     *
     * @return a new BaseBlock with default properties
     */
    public static BaseBlock stone() {
        return new BaseBlock(Properties.of().strength(1.5f, 6.0f).requiresCorrectToolForDrops());
    }

    /**
     * Create a base block with default wood-like properties.
     *
     * @return a new BaseBlock with default properties
     */
    public static BaseBlock wood() {
        return new BaseBlock(Properties.of().strength(2.0f, 3.0f));
    }

    /**
     * Create a base block with default metal-like properties.
     *
     * @return a new BaseBlock with default properties
     */
    public static BaseBlock metal() {
        return new BaseBlock(Properties.of().strength(5.0f, 6.0f).requiresCorrectToolForDrops());
    }
}
