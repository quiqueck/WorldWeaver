package org.betterx.wover.testmod.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class TestBlock extends Block {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 2);

    public TestBlock(Properties properties) {
        super(properties);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        super.createBlockStateDefinition(stateManager);
        stateManager.add(AGE);
    }
}
