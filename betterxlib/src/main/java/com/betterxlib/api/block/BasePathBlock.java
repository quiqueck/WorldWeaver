package com.betterxlib.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

/**
 * A base path block with a lowered height like dirt paths.
 */
public class BasePathBlock extends Block {
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);

    private final Supplier<Block> baseBlock;

    public BasePathBlock(Properties properties, Supplier<Block> baseBlock) {
        super(properties);
        this.baseBlock = baseBlock;
    }

    /**
     * Create default path properties.
     *
     * @param mapColor the map color
     * @return properties suitable for paths
     */
    public static Properties defaultProperties(MapColor mapColor) {
        return Properties.of()
            .mapColor(mapColor)
            .strength(0.65f)
            .sound(SoundType.GRASS)
            .isViewBlocking((state, level, pos) -> true)
            .isSuffocating((state, level, pos) -> true);
    }

    /**
     * Create a path block.
     *
     * @param baseBlock the block to revert to when invalid
     * @param mapColor the map color
     * @return a new BasePathBlock
     */
    public static BasePathBlock create(Supplier<Block> baseBlock, MapColor mapColor) {
        return new BasePathBlock(defaultProperties(mapColor), baseBlock);
    }

    /**
     * Create from an existing block.
     *
     * @param source the source block
     * @param baseBlock the block to revert to
     * @return a new BasePathBlock
     */
    public static BasePathBlock from(Block source, Supplier<Block> baseBlock) {
        return new BasePathBlock(BlockBehaviour.Properties.ofFullCopy(source), baseBlock);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        if (!defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos())) {
            return Block.pushEntitiesUp(
                defaultBlockState(),
                baseBlock.get().defaultBlockState(),
                context.getLevel(),
                context.getClickedPos()
            );
        }
        return super.getStateForPlacement(context);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                   LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.UP && !state.canSurvive(level, pos)) {
            level.scheduleTick(pos, this, 1);
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState dirt = Block.pushEntitiesUp(state, baseBlock.get().defaultBlockState(), level, pos);
        level.setBlockAndUpdate(pos, dirt);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState above = level.getBlockState(pos.above());
        return !above.isSolid() || above.getBlock() instanceof BasePathBlock;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}
