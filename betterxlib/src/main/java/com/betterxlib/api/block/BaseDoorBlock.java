package com.betterxlib.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * A base door block with configurable properties.
 */
public class BaseDoorBlock extends DoorBlock {

    private final boolean isFlammable;

    public BaseDoorBlock(Properties properties, BlockSetType blockSetType) {
        this(properties, blockSetType, true);
    }

    public BaseDoorBlock(Properties properties, BlockSetType blockSetType, boolean isFlammable) {
        super(blockSetType, properties);
        this.isFlammable = isFlammable;
    }

    /**
     * Create default wood door properties.
     *
     * @param mapColor the map color
     * @return properties suitable for wood doors
     */
    public static Properties woodProperties(MapColor mapColor) {
        return Properties.of()
            .mapColor(mapColor)
            .strength(3.0f)
            .noOcclusion()
            .sound(SoundType.WOOD)
            .pushReaction(PushReaction.DESTROY);
    }

    /**
     * Create default metal door properties.
     *
     * @param mapColor the map color
     * @return properties suitable for metal doors
     */
    public static Properties metalProperties(MapColor mapColor) {
        return Properties.of()
            .mapColor(mapColor)
            .strength(5.0f)
            .requiresCorrectToolForDrops()
            .noOcclusion()
            .sound(SoundType.METAL)
            .pushReaction(PushReaction.DESTROY);
    }

    /**
     * Create a wood door block.
     *
     * @param mapColor the map color
     * @param blockSetType the block set type for sounds
     * @return a new BaseDoorBlock
     */
    public static BaseDoorBlock wood(MapColor mapColor, BlockSetType blockSetType) {
        return new BaseDoorBlock(woodProperties(mapColor), blockSetType, true);
    }

    /**
     * Create a metal door block.
     *
     * @param mapColor the map color
     * @param blockSetType the block set type for sounds
     * @return a new BaseDoorBlock
     */
    public static BaseDoorBlock metal(MapColor mapColor, BlockSetType blockSetType) {
        return new BaseDoorBlock(metalProperties(mapColor), blockSetType, false);
    }

    /**
     * Create from an existing block.
     *
     * @param source the source block
     * @param blockSetType the block set type for sounds
     * @return a new BaseDoorBlock
     */
    public static BaseDoorBlock from(Block source, BlockSetType blockSetType) {
        return new BaseDoorBlock(BlockBehaviour.Properties.ofFullCopy(source), blockSetType);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return isFlammable ? 20 : 0;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return isFlammable ? 5 : 0;
    }
}
