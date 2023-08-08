package org.betterx.wover.block.api;

import org.betterx.wover.tag.api.predefined.CommonBlockTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

public class BlockHelper {
    public static final int FLAG_UPDATE_BLOCK = 1;
    public static final int FLAG_SEND_CLIENT_CHANGES = 2;
    public static final int FLAG_NO_RERENDER = 4;
    public static final int FORSE_RERENDER = 8;
    public static final int FLAG_IGNORE_OBSERVERS = 16;

    public static final int SET_SILENT = FLAG_IGNORE_OBSERVERS | FLAG_SEND_CLIENT_CHANGES;
    public static final int SET_OBSERV = FLAG_UPDATE_BLOCK | FLAG_SEND_CLIENT_CHANGES;
    public static final List<Direction> HORIZONTAL = List.of(
            Direction.NORTH,
            Direction.EAST,
            Direction.WEST,
            Direction.SOUTH
    );
    public static final List<Direction> VERTICAL = List.of(Direction.UP, Direction.DOWN);
    public static final List<Direction> ALL = List.of(
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST,
            Direction.UP,
            Direction.DOWN
    );

    public static boolean isFluid(BlockState state) {
        return state.liquid();
    }

    public static boolean isFreeOrFluid(BlockState state) {
        return state.isAir() || isFluid(state);
    }

    /**
     * Test if the block is a terrain block. Whenever possible, you should use
     * {@link org.betterx.wover.block.api.predicate.BlockPredicates#ONLY_GROUND } instead. However,
     * this method call will be faster if you already have or need to use the block state multiple times.
     *
     * @param state the block state to test
     * @return true if the block is a terrain block. This is the case if the block has
     * the {@link CommonBlockTags#TERRAIN} tag.
     */
    public static boolean isTerrain(BlockState state) {
        return state.is(CommonBlockTags.TERRAIN);
    }

    /**
     * @param world the world
     * @param pos   the position
     * @param state the state to set
     * @deprecated use {@link LevelAccessor#setBlock(BlockPos, BlockState, int)} instead
     */
    @Deprecated(forRemoval = true)
    public static void setWithoutUpdate(LevelAccessor world, BlockPos pos, BlockState state) {
        world.setBlock(pos, state, SET_SILENT);
    }

    public static boolean findOnSurroundingSurface(
            WorldGenLevel level,
            BlockPos.MutableBlockPos startPos,
            Direction dir,
            int length,
            BlockPredicate surface
    ) {
        for (int len = 0; len < length; len++) {
            if (surface.test(level, startPos)) {
                if (len == 0) { //we started inside of the surface
                    for (int lenUp = 0; lenUp < length; lenUp++) {
                        startPos.move(dir, -1);
                        if (!surface.test(level, startPos)) {
                            return true;
                        }
                    }
                    return false;
                }
                startPos.move(dir, -1);
                return true;
            }

            startPos.move(dir, 1);
        }
        return false;
    }

    /**
     * Returns a set of all possible states for the given block.
     *
     * @param block the block
     * @return a set of all possible states for the given block
     */
    public static Set<BlockState> getPossibleStates(Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }
}
