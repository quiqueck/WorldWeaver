package com.betterxlib.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * A base leaves block with decay and flammability.
 */
public class BaseLeavesBlock extends LeavesBlock {

    private final int lightLevel;

    public BaseLeavesBlock(Properties properties) {
        this(properties, 0);
    }

    public BaseLeavesBlock(Properties properties, int lightLevel) {
        super(properties);
        this.lightLevel = lightLevel;
    }

    /**
     * Create default leaves properties.
     *
     * @return properties suitable for leaves
     */
    public static Properties defaultProperties() {
        return defaultProperties(MapColor.PLANT);
    }

    /**
     * Create default leaves properties with a custom map color.
     *
     * @param mapColor the map color
     * @return properties suitable for leaves
     */
    public static Properties defaultProperties(MapColor mapColor) {
        return Properties.of()
            .mapColor(mapColor)
            .strength(0.2f)
            .randomTicks()
            .sound(SoundType.GRASS)
            .noOcclusion()
            .isValidSpawn((state, level, pos, type) -> false)
            .isSuffocating((state, level, pos) -> false)
            .isViewBlocking((state, level, pos) -> false)
            .pushReaction(PushReaction.DESTROY)
            .ignitedByLava();
    }

    /**
     * Create a leaves block with default properties.
     *
     * @return a new BaseLeavesBlock
     */
    public static BaseLeavesBlock create() {
        return new BaseLeavesBlock(defaultProperties());
    }

    /**
     * Create a leaves block with a custom map color.
     *
     * @param mapColor the map color
     * @return a new BaseLeavesBlock
     */
    public static BaseLeavesBlock create(MapColor mapColor) {
        return new BaseLeavesBlock(defaultProperties(mapColor));
    }

    /**
     * Create a glowing leaves block.
     *
     * @param lightLevel the light level (0-15)
     * @return a new glowing BaseLeavesBlock
     */
    public static BaseLeavesBlock glowing(int lightLevel) {
        return new BaseLeavesBlock(defaultProperties().lightLevel(state -> lightLevel), lightLevel);
    }

    /**
     * Create from an existing block.
     *
     * @param source the source block
     * @return a new BaseLeavesBlock
     */
    public static BaseLeavesBlock from(Block source) {
        return new BaseLeavesBlock(BlockBehaviour.Properties.ofFullCopy(source));
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 60;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 30;
    }

    /**
     * Check if this leaves block emits light.
     *
     * @return true if this block glows
     */
    public boolean isGlowing() {
        return lightLevel > 0;
    }
}
