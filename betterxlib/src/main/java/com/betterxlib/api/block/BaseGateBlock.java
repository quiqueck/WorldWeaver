package com.betterxlib.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;

/**
 * A base fence gate block that can be created from a source block.
 */
public class BaseGateBlock extends FenceGateBlock {

    private final boolean isFlammable;

    public BaseGateBlock(Properties properties, WoodType woodType) {
        this(properties, woodType, true);
    }

    public BaseGateBlock(Properties properties, WoodType woodType, boolean isFlammable) {
        super(woodType, properties);
        this.isFlammable = isFlammable;
    }

    /**
     * Create default wood gate properties.
     *
     * @param mapColor the map color
     * @return properties suitable for wood gates
     */
    public static Properties woodProperties(MapColor mapColor) {
        return Properties.of()
            .mapColor(mapColor)
            .strength(2.0f, 3.0f)
            .sound(SoundType.WOOD);
    }

    /**
     * Create a wood gate block.
     *
     * @param mapColor the map color
     * @param woodType the wood type for sounds
     * @return a new BaseGateBlock
     */
    public static BaseGateBlock wood(MapColor mapColor, WoodType woodType) {
        return new BaseGateBlock(woodProperties(mapColor), woodType, true);
    }

    /**
     * Create from an existing block.
     *
     * @param source the source block
     * @param woodType the wood type for sounds
     * @return a new BaseGateBlock
     */
    public static BaseGateBlock from(Block source, WoodType woodType) {
        return new BaseGateBlock(BlockBehaviour.Properties.ofFullCopy(source), woodType);
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
