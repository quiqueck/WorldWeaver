package org.betterx.wover.feature.api.placed.modifiers;

import org.betterx.wover.block.api.BlockHelper;
import org.betterx.wover.block.api.predicate.BlockPredicates;
import org.betterx.wover.feature.impl.placed.modifiers.PlacementModifiersImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.List;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

/**
 * Find a block (by {@link BlockPredicate}) in every direction from a set of directions.
 * <p>
 * This Modifier will start a search from the input position in a given direction up to a given
 * maximum distance. The first block in the search that matches the {@link BlockPredicate} will be
 * emitted for the direction. If none is found in the given distance, no position will be emitted.
 * <p>
 * If multiple directions are supplied to this modifier, all directions will be searched
 * in sequence. Each direction matching a block will emit the found position.
 * <p>
 * You can also specify a result offset ({@code offsetInDir}. If a position was found in a given direction, the
 * result will be offset by the given offset-amount in the direction.
 * <p>
 * Finally, if you want to select only a single, random direction, you can set {@code randomSelect} to true.
 * If set, only one random direction will be searched.
 */
public class FindInDirection extends PlacementModifier {
    /**
     * The codec for this placement modifier.
     */
    public static final Codec<FindInDirection> CODEC = RecordCodecBuilder
            .create((instance) -> instance.group(
                                                  ExtraCodecs.nonEmptyList(Direction.CODEC.listOf())
                                                             .optionalFieldOf("dir", List.of(Direction.DOWN))
                                                             .forGetter(a -> a.directions),
                                                  Codec.intRange(1, 32).optionalFieldOf("dist", 12).forGetter((p) -> p.maxSearchDistance),
                                                  Codec.BOOL.optionalFieldOf("random_select", true).forGetter(p -> p.randomSelect),
                                                  Codec.INT.optionalFieldOf("offset_in_dir", 0).forGetter(p -> p.offsetInDir),
                                                  BlockPredicate.CODEC.optionalFieldOf("surface_predicate", BlockPredicates.ONLY_GROUND)
                                                                      .forGetter(p -> p.surfacePredicate)
                                          )
                                          .apply(
                                                  instance,
                                                  FindInDirection::new
                                          ));

    private static final FindInDirection DOWN = new FindInDirection(
            Direction.DOWN,
            6,
            0,
            BlockPredicates.ONLY_GROUND
    );
    private static final FindInDirection UP = new FindInDirection(
            Direction.UP,
            6,
            0,
            BlockPredicates.ONLY_GROUND
    );
    private final List<Direction> directions;
    private final int maxSearchDistance;

    private final int offsetInDir;
    private final boolean randomSelect;
    private final IntProvider provider;
    private final BlockPredicate surfacePredicate;


    /**
     * Create a new {@link FindInDirection} Modifier with a single direction
     *
     * @param direction         The direction to search in.
     * @param maxSearchDistance The maximum distance to search for a solid block.
     * @param offsetInDir       The offset you want to apply to the result position in the search direction.
     * @param surfacePredicate  The predicate to use to find the surface.
     */
    public FindInDirection(
            Direction direction,
            int maxSearchDistance,
            int offsetInDir,
            BlockPredicate surfacePredicate
    ) {
        this(List.of(direction), maxSearchDistance, false, offsetInDir, surfacePredicate);
    }

    /**
     * Create a new {@link FindInDirection} Modifier multiple directions. All directions will bea searched.
     *
     * @param directions        The directions to search in.
     * @param maxSearchDistance The maximum distance to search for a solid block.
     * @param offsetInDir       The offset you want to apply to the result position in the search direction.
     * @param surfacePredicate  The predicate to use to find the surface.
     */
    public FindInDirection(
            List<Direction> directions,
            int maxSearchDistance,
            int offsetInDir,
            BlockPredicate surfacePredicate
    ) {
        this(directions, maxSearchDistance, directions.size() > 1, offsetInDir, surfacePredicate);
    }

    /**
     * Create a new {@link FindInDirection} Modifier multiple directions.
     *
     * @param directions        The directions to search in.
     * @param maxSearchDistance The maximum distance to search for a solid block.
     * @param randomSelect      If true, only one random direction will be searched.
     * @param offsetInDir       The offset you want to apply to the result position in the search direction.
     * @param surfacePredicate  The predicate to use to find the surface.
     */
    public FindInDirection(
            List<Direction> directions,
            int maxSearchDistance,
            boolean randomSelect,
            int offsetInDir,
            BlockPredicate surfacePredicate
    ) {
        this.directions = directions;
        this.maxSearchDistance = maxSearchDistance;
        this.provider = UniformInt.of(0, this.directions.size() - 1);
        this.randomSelect = randomSelect;
        this.offsetInDir = offsetInDir;
        this.surfacePredicate = surfacePredicate;
    }

    /**
     * Create a new {@link FindInDirection} that will search below the input
     * position with a maximum distance of 6 Blocks. The resulting position will not be offset.
     * <p>
     * The modifier will use the {@link BlockPredicates#ONLY_GROUND} Predicate.
     *
     * @return A new {@link FindInDirection} Modifier.
     */
    public static PlacementModifier down() {
        return DOWN;
    }

    /**
     * Create a new {@link FindInDirection} that will search above the input
     * position with a maximum distance of 6 Blocks. The resulting position will not be offset.
     * <p>
     * The modifier will use the {@link BlockPredicates#ONLY_GROUND} Predicate.
     *
     * @return A new {@link FindInDirection} Modifier.
     */
    public static PlacementModifier up() {
        return UP;
    }

    /**
     * Find the next solid block below the input position.
     * The modifier will use the {@link BlockPredicates#ONLY_GROUND} Predicate.
     *
     * @param dist The maximum distance to search for a solid block.
     * @return A new {@link FindInDirection} Modifier.
     */
    public static PlacementModifier down(int dist) {
        if (dist == DOWN.maxSearchDistance && 0 == DOWN.offsetInDir) return DOWN;
        return new FindInDirection(Direction.DOWN, dist, 0, BlockPredicates.ONLY_GROUND);
    }

    /**
     * Find the next solid block above the input position.
     * The modifier will use the {@link BlockPredicates#ONLY_GROUND} Predicate.
     *
     * @param dist The maximum distance to search for a solid block.
     * @return A new {@link FindInDirection} Modifier.
     */
    public static PlacementModifier up(int dist) {
        if (dist == UP.maxSearchDistance && 0 == UP.offsetInDir) return UP;
        return new FindInDirection(Direction.UP, dist, 0, BlockPredicates.ONLY_GROUND);
    }


    /**
     * Find the next solid block below the input position and offset the resulting position
     * downward by the given amount.
     * The modifier will use the {@link BlockPredicates#ONLY_GROUND} Predicate.
     *
     * @param dist The maximum distance to search for a solid block.
     * @return A new {@link FindInDirection} Modifier.
     */
    public static PlacementModifier down(int dist, int offset) {
        if (dist == DOWN.maxSearchDistance && 0 == DOWN.offsetInDir) return DOWN;
        return new FindInDirection(Direction.DOWN, dist, offset, BlockPredicates.ONLY_GROUND);
    }

    /**
     * Find the next solid block above the input position and offset the resulting position
     * upward by the given amount.
     * The modifier will use the {@link BlockPredicates#ONLY_GROUND} Predicate.
     *
     * @param dist The maximum distance to search for a solid block.
     * @return A new {@link FindInDirection} Modifier.
     */
    public static PlacementModifier up(int dist, int offset) {
        if (dist == UP.maxSearchDistance && offset == UP.offsetInDir) return UP;
        return new FindInDirection(Direction.UP, dist, offset, BlockPredicates.ONLY_GROUND);
    }

    /**
     * select a random direction from the list of directions.
     *
     * @param random The random source to use.
     * @return A random direction.
     */
    public Direction randomDirection(RandomSource random) {
        return directions.get(provider.sample(random));
    }


    /**
     * Calculates the positions that this placement modifier will emit.
     *
     * @param placementContext The placement context.
     * @param randomSource     The random source.
     * @param blockPos         The input position.
     * @return The stream of new positions.
     */
    @Override
    public @NotNull Stream<BlockPos> getPositions(
            PlacementContext placementContext,
            RandomSource randomSource,
            BlockPos blockPos
    ) {
        var builder = Stream.<BlockPos>builder();
        if (randomSelect) {
            submitSingle(placementContext, blockPos, builder, randomDirection(randomSource));
        } else {
            for (Direction d : directions) {
                submitSingle(placementContext, blockPos, builder, d);
            }
        }

        return builder.build();
    }

    private void submitSingle(
            PlacementContext placementContext,
            BlockPos blockPos,
            Stream.Builder<BlockPos> builder,
            Direction searchDirection
    ) {
        int searchDist;
        BlockPos.MutableBlockPos POS = blockPos.mutable();
        if (searchDirection == Direction.EAST) { //+x
            searchDist = Math.min(maxSearchDistance, 15 - SectionPos.sectionRelative(blockPos.getX()));
        } else if (searchDirection == Direction.WEST) { //-x
            searchDist = Math.min(maxSearchDistance, SectionPos.sectionRelative(blockPos.getX()));
        } else if (searchDirection == Direction.SOUTH) { //+z
            searchDist = Math.min(maxSearchDistance, 15 - SectionPos.sectionRelative(blockPos.getZ()));
        } else if (searchDirection == Direction.NORTH) { //-z
            searchDist = Math.min(maxSearchDistance, SectionPos.sectionRelative(blockPos.getZ()));
        } else {
            searchDist = maxSearchDistance;
        }
        if (BlockHelper.findOnSurroundingSurface(
                placementContext.getLevel(),
                POS,
                searchDirection,
                searchDist,
                surfacePredicate
        )) {
            if (offsetInDir != 0)
                builder.add(POS.move(searchDirection, offsetInDir));
            else
                builder.add(POS);
        }
    }

    /**
     * Get the type of this modifier.
     *
     * @return The type.
     */
    @Override
    public @NotNull PlacementModifierType<?> type() {
        return PlacementModifiersImpl.SOLID_IN_DIR;
    }
}
