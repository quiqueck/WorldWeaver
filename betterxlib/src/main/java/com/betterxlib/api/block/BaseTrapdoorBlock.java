package com.betterxlib.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.MapColor;

/**
 * A base trapdoor block with configurable properties.
 */
public class BaseTrapdoorBlock extends TrapDoorBlock {

    private final boolean isFlammable;

    public BaseTrapdoorBlock(Properties properties, BlockSetType blockSetType) {
        this(properties, blockSetType, true);
    }

    public BaseTrapdoorBlock(Properties properties, BlockSetType blockSetType, boolean isFlammable) {
        super(blockSetType, properties);
        this.isFlammable = isFlammable;
    }

    /**
     * Create default wood trapdoor properties.
     *
     * @param mapColor the map color
     * @return properties suitable for wood trapdoors
     */
    public static Properties woodProperties(MapColor mapColor) {
        return Properties.of()
            .mapColor(mapColor)
            .strength(3.0f)
            .noOcclusion()
            .isValidSpawn((state, level, pos, type) -> false)
            .sound(SoundType.WOOD);
    }

    /**
     * Create default metal trapdoor properties.
     *
     * @param mapColor the map color
     * @return properties suitable for metal trapdoors
     */
    public static Properties metalProperties(MapColor mapColor) {
        return Properties.of()
            .mapColor(mapColor)
            .strength(5.0f)
            .requiresCorrectToolForDrops()
            .noOcclusion()
            .isValidSpawn((state, level, pos, type) -> false)
            .sound(SoundType.METAL);
    }

    /**
     * Create a wood trapdoor block.
     *
     * @param mapColor the map color
     * @param blockSetType the block set type for sounds
     * @return a new BaseTrapdoorBlock
     */
    public static BaseTrapdoorBlock wood(MapColor mapColor, BlockSetType blockSetType) {
        return new BaseTrapdoorBlock(woodProperties(mapColor), blockSetType, true);
    }

    /**
     * Create a metal trapdoor block.
     *
     * @param mapColor the map color
     * @param blockSetType the block set type for sounds
     * @return a new BaseTrapdoorBlock
     */
    public static BaseTrapdoorBlock metal(MapColor mapColor, BlockSetType blockSetType) {
        return new BaseTrapdoorBlock(metalProperties(mapColor), blockSetType, false);
    }

    /**
     * Create from an existing block.
     *
     * @param source the source block
     * @param blockSetType the block set type for sounds
     * @return a new BaseTrapdoorBlock
     */
    public static BaseTrapdoorBlock from(Block source, BlockSetType blockSetType) {
        return new BaseTrapdoorBlock(BlockBehaviour.Properties.ofFullCopy(source), blockSetType);
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
