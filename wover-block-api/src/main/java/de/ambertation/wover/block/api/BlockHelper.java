package de.ambertation.wover.block.api;

import de.ambertation.wover.tag.api.predefined.CommonBlockTags;

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

/**
 * Static helper methods and constants for working with {@link Block}s and {@link BlockState}s.
 */
public class BlockHelper {
    /**
     * Block update flag: notify neighbors and re-render (see {@link LevelAccessor#setBlock(BlockPos, BlockState, int)}).
     */
    public static final int FLAG_UPDATE_BLOCK = 1;
    /**
     * Block update flag: send the change to clients.
     */
    public static final int FLAG_SEND_CLIENT_CHANGES = 2;
    /**
     * Block update flag: prevent the block from being re-rendered on the client.
     */
    public static final int FLAG_NO_RERENDER = 4;
    /**
     * Block update flag: force a re-render even if it would otherwise be skipped.
     */
    public static final int FORSE_RERENDER = 8;
    /**
     * Block update flag: don't notify block observers of the change.
     */
    public static final int FLAG_IGNORE_OBSERVERS = 16;

    /**
     * Combined flag set that updates the block silently: no observers are notified, but the client is
     * still informed of the change.
     */
    public static final int SET_SILENT = FLAG_IGNORE_OBSERVERS | FLAG_SEND_CLIENT_CHANGES;
    /**
     * Combined flag set that updates the block, notifies neighbors/observers and informs the client.
     */
    public static final int SET_OBSERV = FLAG_UPDATE_BLOCK | FLAG_SEND_CLIENT_CHANGES;
    /**
     * The four horizontal {@link Direction}s (north, east, west, south).
     */
    public static final List<Direction> HORIZONTAL = List.of(
            Direction.NORTH,
            Direction.EAST,
            Direction.WEST,
            Direction.SOUTH
    );
    /**
     * The two vertical {@link Direction}s (up, down).
     */
    public static final List<Direction> VERTICAL = List.of(Direction.UP, Direction.DOWN);
    /**
     * All six {@link Direction}s.
     */
    public static final List<Direction> ALL = List.of(
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST,
            Direction.UP,
            Direction.DOWN
    );

    /**
     * Tests if the given state is a liquid.
     *
     * @param state the block state to test
     * @return {@code true} if the state is a fluid
     */
    public static boolean isFluid(BlockState state) {
        return state.liquid();
    }

    /**
     * Tests if the given state is either air or a liquid.
     *
     * @param state the block state to test
     * @return {@code true} if the state is air or a fluid
     */
    public static boolean isFreeOrFluid(BlockState state) {
        return state.isAir() || isFluid(state);
    }

    /**
     * Test if the block is a terrain block. Whenever possible, you should use
     * {@link de.ambertation.wover.block.api.predicate.BlockPredicates#ONLY_GROUND } instead. However,
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

    /**
     * Searches from {@code startPos} in direction {@code dir} for the transition into/out of a surface
     * matched by {@code surface}, moving {@code startPos} to the found position.
     * <p>
     * Equivalent to {@link #findOnSurroundingSurface(WorldGenLevel, BlockPos.MutableBlockPos, Direction, int,
     * int, BlockPredicate)} with the same limit for both search directions.
     *
     * @param level    the world to search in
     * @param startPos the position to start searching from. This position is mutated in place and will
     *                 point to the found location when the method returns {@code true}
     * @param dir      the direction to search along
     * @param length   the maximum number of blocks to search
     * @param surface  the predicate that identifies the surface
     * @return {@code true} if a matching position was found
     */
    public static boolean findOnSurroundingSurface(
            WorldGenLevel level,
            BlockPos.MutableBlockPos startPos,
            Direction dir,
            int length,
            BlockPredicate surface
    ) {
        return findOnSurroundingSurface(level, startPos, dir, length, length, surface);
    }

    /**
     * Searches from {@code startPos} in direction {@code dir} for the transition into/out of a surface
     * matched by {@code surface}, moving {@code startPos} to the found position.
     * <p>
     * The search has <em>two</em> phases and they do not travel the same way:
     * <ul>
     *     <li>the normal case walks along {@code dir} until it enters the surface and then steps one block
     *     back, so the result is the last free block in front of the surface. That walk is limited by
     *     {@code length};</li>
     *     <li>if {@code startPos} is <em>already</em> inside the surface, the search instead walks the
     *     <em>opposite</em> way until it leaves the surface again. That walk is limited by
     *     {@code oppositeLength}.</li>
     * </ul>
     * The two limits are separate because a caller that has to keep the search inside a bounded region (a
     * worldgen chunk, for example) has a different amount of room available in each of the two directions.
     * Passing a single limit for both - as the {@linkplain #findOnSurroundingSurface(WorldGenLevel,
     * BlockPos.MutableBlockPos, Direction, int, BlockPredicate) short overload} does - lets the backwards
     * walk leave that region by up to {@code length} blocks.
     *
     * @param level          the world to search in
     * @param startPos       the position to start searching from. This position is mutated in place and will
     *                       point to the found location when the method returns {@code true}
     * @param dir            the direction to search along
     * @param length         the maximum number of blocks to search along {@code dir}
     * @param oppositeLength the maximum number of blocks to search against {@code dir} when {@code startPos}
     *                       already is inside the surface. {@code 0} disables that phase.
     * @param surface        the predicate that identifies the surface
     * @return {@code true} if a matching position was found
     */
    public static boolean findOnSurroundingSurface(
            WorldGenLevel level,
            BlockPos.MutableBlockPos startPos,
            Direction dir,
            int length,
            int oppositeLength,
            BlockPredicate surface
    ) {
        for (int len = 0; len < length; len++) {
            if (surface.test(level, startPos)) {
                if (len == 0) { //we started inside of the surface
                    for (int lenUp = 0; lenUp < oppositeLength; lenUp++) {
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
