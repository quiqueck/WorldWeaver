package com.betterxlib.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A base plant block with configurable placement rules and shape.
 */
public class BasePlantBlock extends BushBlock {
    protected static final VoxelShape DEFAULT_SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 14.0, 12.0);

    private final VoxelShape shape;

    public BasePlantBlock(Properties properties) {
        this(properties, DEFAULT_SHAPE);
    }

    public BasePlantBlock(Properties properties, VoxelShape shape) {
        super(properties);
        this.shape = shape;
    }

    /**
     * Create default plant properties.
     *
     * @return properties suitable for plants
     */
    public static Properties defaultProperties() {
        return Properties.of()
            .mapColor(MapColor.PLANT)
            .noCollission()
            .instabreak()
            .sound(SoundType.GRASS)
            .pushReaction(PushReaction.DESTROY);
    }

    /**
     * Create a plant block with default properties.
     *
     * @return a new BasePlantBlock
     */
    public static BasePlantBlock create() {
        return new BasePlantBlock(defaultProperties());
    }

    /**
     * Create a plant block with a custom shape.
     *
     * @param shape the voxel shape for the plant
     * @return a new BasePlantBlock
     */
    public static BasePlantBlock create(VoxelShape shape) {
        return new BasePlantBlock(defaultProperties(), shape);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockTags.DIRT) ||
               state.is(BlockTags.SAND) ||
               state.is(BlockTags.NYLIUM) ||
               super.mayPlaceOn(state, level, pos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return mayPlaceOn(level.getBlockState(below), level, below);
    }

    /**
     * Check if this plant emits light (for glow plants).
     *
     * @return true if this plant glows
     */
    public boolean isGlowing() {
        return false;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 100;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 60;
    }
}
