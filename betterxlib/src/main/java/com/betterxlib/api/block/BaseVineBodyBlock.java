package com.betterxlib.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

/**
 * The body portion of a growing vine block.
 */
public class BaseVineBodyBlock extends GrowingPlantBodyBlock {
    protected static final VoxelShape DEFAULT_SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

    private final VoxelShape shape;
    private final Supplier<GrowingPlantHeadBlock> head;

    public BaseVineBodyBlock(Properties properties, Supplier<GrowingPlantHeadBlock> headBlock) {
        this(properties, headBlock, DEFAULT_SHAPE);
    }

    public BaseVineBodyBlock(Properties properties, Supplier<GrowingPlantHeadBlock> headBlock, VoxelShape shape) {
        super(properties, Direction.DOWN, shape, false);
        this.shape = shape;
        this.head = headBlock;
    }

    /**
     * Create default properties for vine bodies.
     *
     * @return properties suitable for vine bodies
     */
    public static Properties defaultProperties() {
        return Properties.of()
            .mapColor(MapColor.PLANT)
            .noCollission()
            .instabreak()
            .sound(SoundType.WEEPING_VINES)
            .pushReaction(PushReaction.DESTROY);
    }

    @Override
    protected GrowingPlantHeadBlock getHeadBlock() {
        return head.get();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape;
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
