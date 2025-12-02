package com.betterxlib.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/**
 * A base rotated pillar block for logs and pillars.
 */
public class BaseRotatedPillarBlock extends RotatedPillarBlock {

    private final boolean isFlammable;

    public BaseRotatedPillarBlock(Properties properties) {
        this(properties, true);
    }

    public BaseRotatedPillarBlock(Properties properties, boolean isFlammable) {
        super(properties);
        this.isFlammable = isFlammable;
    }

    /**
     * Create default wood log properties.
     *
     * @param mapColor the map color
     * @return properties suitable for logs
     */
    public static Properties woodProperties(MapColor mapColor) {
        return Properties.of()
            .mapColor(mapColor)
            .strength(2.0f)
            .sound(SoundType.WOOD);
    }

    /**
     * Create a wood log block.
     *
     * @param mapColor the map color
     * @return a new BaseRotatedPillarBlock
     */
    public static BaseRotatedPillarBlock log(MapColor mapColor) {
        return new BaseRotatedPillarBlock(woodProperties(mapColor), true);
    }

    /**
     * Create a stone pillar block.
     *
     * @param mapColor the map color
     * @return a new BaseRotatedPillarBlock
     */
    public static BaseRotatedPillarBlock stone(MapColor mapColor) {
        return new BaseRotatedPillarBlock(
            Properties.of()
                .mapColor(mapColor)
                .strength(1.5f, 6.0f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.STONE),
            false
        );
    }

    /**
     * Create from an existing block.
     *
     * @param source the source block
     * @return a new BaseRotatedPillarBlock
     */
    public static BaseRotatedPillarBlock from(Block source) {
        return new BaseRotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(source));
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return isFlammable ? 5 : 0;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return isFlammable ? 5 : 0;
    }
}
