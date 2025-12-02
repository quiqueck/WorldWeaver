package com.betterxlib.api.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

/**
 * A base stairs block that can be created from a source block.
 */
public class BaseStairsBlock extends StairBlock {

    public BaseStairsBlock(Supplier<BlockState> state, Properties properties) {
        super(state.get(), properties);
    }

    public BaseStairsBlock(BlockState state, Properties properties) {
        super(state, properties);
    }

    /**
     * Create a stairs block with properties and base state from a source block.
     *
     * @param source the source block to copy properties from
     * @return a new BaseStairsBlock
     */
    public static BaseStairsBlock from(Block source) {
        return new BaseStairsBlock(
            source.defaultBlockState(),
            BlockBehaviour.Properties.ofFullCopy(source)
        );
    }

    /**
     * Create a stairs block with a supplier for lazy initialization.
     *
     * @param source supplier for the source block
     * @return a new BaseStairsBlock
     */
    public static BaseStairsBlock from(Supplier<? extends Block> source) {
        return new BaseStairsBlock(
            () -> source.get().defaultBlockState(),
            BlockBehaviour.Properties.ofFullCopy(source.get())
        );
    }
}
