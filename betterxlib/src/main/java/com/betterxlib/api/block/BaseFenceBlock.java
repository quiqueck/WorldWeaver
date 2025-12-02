package com.betterxlib.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/**
 * A base fence block that can be created from a source block.
 */
public class BaseFenceBlock extends FenceBlock {

    private final boolean isFlammable;

    public BaseFenceBlock(Properties properties) {
        this(properties, true);
    }

    public BaseFenceBlock(Properties properties, boolean isFlammable) {
        super(properties);
        this.isFlammable = isFlammable;
    }

    /**
     * Create default wood fence properties.
     *
     * @param mapColor the map color
     * @return properties suitable for wood fences
     */
    public static Properties woodProperties(MapColor mapColor) {
        return Properties.of()
            .mapColor(mapColor)
            .strength(2.0f, 3.0f)
            .sound(SoundType.WOOD);
    }

    /**
     * Create a wood fence block.
     *
     * @param mapColor the map color
     * @return a new BaseFenceBlock
     */
    public static BaseFenceBlock wood(MapColor mapColor) {
        return new BaseFenceBlock(woodProperties(mapColor), true);
    }

    /**
     * Create a nether brick fence block.
     *
     * @return a new non-flammable BaseFenceBlock
     */
    public static BaseFenceBlock netherBrick() {
        return new BaseFenceBlock(
            Properties.of()
                .mapColor(MapColor.NETHER)
                .strength(2.0f, 6.0f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.NETHER_BRICKS),
            false
        );
    }

    /**
     * Create from an existing block.
     *
     * @param source the source block
     * @return a new BaseFenceBlock
     */
    public static BaseFenceBlock from(Block source) {
        return new BaseFenceBlock(BlockBehaviour.Properties.ofFullCopy(source));
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
