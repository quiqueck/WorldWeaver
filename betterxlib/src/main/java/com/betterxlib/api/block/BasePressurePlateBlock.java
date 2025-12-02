package com.betterxlib.api.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * A base pressure plate block with configurable properties.
 */
public class BasePressurePlateBlock extends PressurePlateBlock {

    public BasePressurePlateBlock(Properties properties, BlockSetType blockSetType) {
        super(blockSetType, properties);
    }

    /**
     * Create default wood pressure plate properties.
     *
     * @param mapColor the map color
     * @return properties suitable for wood pressure plates
     */
    public static Properties woodProperties(MapColor mapColor) {
        return Properties.of()
            .mapColor(mapColor)
            .noCollission()
            .strength(0.5f)
            .sound(SoundType.WOOD)
            .pushReaction(PushReaction.DESTROY);
    }

    /**
     * Create default stone pressure plate properties.
     *
     * @param mapColor the map color
     * @return properties suitable for stone pressure plates
     */
    public static Properties stoneProperties(MapColor mapColor) {
        return Properties.of()
            .mapColor(mapColor)
            .noCollission()
            .strength(0.5f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE)
            .pushReaction(PushReaction.DESTROY);
    }

    /**
     * Create a wood pressure plate block.
     *
     * @param mapColor the map color
     * @param blockSetType the block set type for sounds
     * @return a new BasePressurePlateBlock
     */
    public static BasePressurePlateBlock wood(MapColor mapColor, BlockSetType blockSetType) {
        return new BasePressurePlateBlock(woodProperties(mapColor), blockSetType);
    }

    /**
     * Create a stone pressure plate block.
     *
     * @param mapColor the map color
     * @param blockSetType the block set type for sounds
     * @return a new BasePressurePlateBlock
     */
    public static BasePressurePlateBlock stone(MapColor mapColor, BlockSetType blockSetType) {
        return new BasePressurePlateBlock(stoneProperties(mapColor), blockSetType);
    }

    /**
     * Create from an existing block.
     *
     * @param source the source block
     * @param blockSetType the block set type for sounds
     * @return a new BasePressurePlateBlock
     */
    public static BasePressurePlateBlock from(Block source, BlockSetType blockSetType) {
        return new BasePressurePlateBlock(BlockBehaviour.Properties.ofFullCopy(source), blockSetType);
    }
}
