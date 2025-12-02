package com.betterxlib.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.NetherVines;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

/**
 * A base vine block that grows downward like weeping vines.
 */
public class BaseVineBlock extends GrowingPlantHeadBlock {
    protected static final VoxelShape DEFAULT_SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

    private final VoxelShape shape;
    private final Supplier<Block> body;
    private final int maxGrowthLength;

    public BaseVineBlock(Properties properties, Supplier<Block> bodyBlock, int maxGrowthLength) {
        this(properties, bodyBlock, maxGrowthLength, DEFAULT_SHAPE);
    }

    public BaseVineBlock(Properties properties, Supplier<Block> bodyBlock, int maxGrowthLength, VoxelShape shape) {
        super(properties, Direction.DOWN, shape, false, 0.1);
        this.shape = shape;
        this.body = bodyBlock;
        this.maxGrowthLength = maxGrowthLength;
    }

    /**
     * Create default properties for vines.
     *
     * @return properties suitable for vines
     */
    public static Properties defaultProperties() {
        return Properties.of()
            .mapColor(MapColor.PLANT)
            .randomTicks()
            .noCollission()
            .instabreak()
            .sound(SoundType.WEEPING_VINES)
            .pushReaction(PushReaction.DESTROY);
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(RandomSource random) {
        return NetherVines.getBlocksToGrowWhenBonemealed(random);
    }

    @Override
    protected boolean canGrowInto(BlockState state) {
        return state.isAir();
    }

    @Override
    protected Block getBodyBlock() {
        return body.get();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return shape;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Check growth length
        int length = 1;
        BlockPos checkPos = pos.above();
        while (level.getBlockState(checkPos).is(getBodyBlock()) || level.getBlockState(checkPos).is(this)) {
            length++;
            checkPos = checkPos.above();
            if (length >= maxGrowthLength) {
                return; // Stop growing
            }
        }
        super.randomTick(state, level, pos, random);
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
