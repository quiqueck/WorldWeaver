package com.betterxlib.api.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.PushReaction;

/**
 * A base button block with configurable properties.
 */
public class BaseButtonBlock extends ButtonBlock {

    public BaseButtonBlock(Properties properties, BlockSetType blockSetType, int ticksToStayPressed, boolean arrowsCanPress) {
        super(blockSetType, ticksToStayPressed, properties);
    }

    /**
     * Create default wood button properties.
     *
     * @return properties suitable for wood buttons
     */
    public static Properties woodProperties() {
        return Properties.of()
            .noCollission()
            .strength(0.5f)
            .sound(SoundType.WOOD)
            .pushReaction(PushReaction.DESTROY);
    }

    /**
     * Create default stone button properties.
     *
     * @return properties suitable for stone buttons
     */
    public static Properties stoneProperties() {
        return Properties.of()
            .noCollission()
            .strength(0.5f)
            .sound(SoundType.STONE)
            .pushReaction(PushReaction.DESTROY);
    }

    /**
     * Create a wood button block.
     *
     * @param blockSetType the block set type for sounds
     * @return a new BaseButtonBlock
     */
    public static BaseButtonBlock wood(BlockSetType blockSetType) {
        return new BaseButtonBlock(woodProperties(), blockSetType, 30, true);
    }

    /**
     * Create a stone button block.
     *
     * @param blockSetType the block set type for sounds
     * @return a new BaseButtonBlock
     */
    public static BaseButtonBlock stone(BlockSetType blockSetType) {
        return new BaseButtonBlock(stoneProperties(), blockSetType, 20, false);
    }

    /**
     * Create from an existing block.
     *
     * @param source the source block
     * @param blockSetType the block set type for sounds
     * @param ticksToStayPressed how long the button stays pressed
     * @param arrowsCanPress whether arrows can press the button
     * @return a new BaseButtonBlock
     */
    public static BaseButtonBlock from(Block source, BlockSetType blockSetType, int ticksToStayPressed, boolean arrowsCanPress) {
        return new BaseButtonBlock(BlockBehaviour.Properties.ofFullCopy(source), blockSetType, ticksToStayPressed, arrowsCanPress);
    }
}
