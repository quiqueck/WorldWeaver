package org.betterx.wover.feature.api.placed;

import org.betterx.wover.block.api.predicate.IsFullShape;
import org.betterx.wover.feature.api.configured.configurators.FeatureConfigurator;
import org.betterx.wover.feature.api.configured.configurators.RandomPatch;
import org.betterx.wover.feature.api.placed.modifiers.EveryLayer;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;

/**
 * Builder for a {@link PlacedFeature}. You can either use the builder register
 * a new {@link PlacedFeature} in a Bootstrap context, or generate a
 * direct holder you can use in inline.
 * <p>
 * The Builder is obtained from a {@link PlacedFeatureKey} using
 * the various place methods. Or you can generate it from a
 * {@link org.betterx.wover.feature.api.configured.configurators.FeatureConfigurator}
 * for any {@link net.minecraft.world.level.levelgen.feature.ConfiguredFeature} by calling
 * {@link FeatureConfigurator#inlinePlace()}.
 * <p>
 * Feature placement is a pipeline starting at a single input position randomly selected by
 * the game. Every stage in the pipeline emits a stream of new positions. The next stage in
 * the pipeline is then applied to each of these positions.
 * <p>
 * Modifications can add but also remove positions from the stream. The feature is then
 * placed at all the positions emitted by the last stage in the pipeline. If the resulting
 * stream is empty, the feature is not placed at all.
 *
 * @see PlacedFeatureKey#place(BootstapContext, Holder)
 * @see PlacedFeatureKey#place(BootstapContext, ResourceKey)
 * @see PlacedFeatureKey.WithConfigured#place(BootstapContext)
 * @see PlacedFeatureKey.WithConfigured#place(BootstapContext, net.minecraft.core.HolderGetter)
 * @see FeatureConfigurator#inlinePlace()
 */
public interface FeaturePlacementBuilder {
    /**
     * Re-emits the input position exactly {@code repetitions} times effectivley duplicating it.
     *
     * @param count the number of times to emit the input position
     * @return this builder
     */
    FeaturePlacementBuilder count(int count);

    /**
     * Same as {@link #count(int)}, but re-emits the input position between 0 and {@code repetitions} times.
     *
     * @param count the maximum number duplicated positions
     * @return this builder
     */
    FeaturePlacementBuilder countMax(int count);

    /**
     * Same as {@link #count(int)}, but re-emits the input position between {@code min} and {@code max} times.
     *
     * @param min the minimum number of duplicated positions
     * @param max the maximum number of duplicated positions
     * @return this builder
     */
    FeaturePlacementBuilder countRange(int min, int max);

    /**
     * Re-emits the input positions offset to all positions in a 16x16 square. The minimal emitted
     * position is the input. The maximal emitted position is the input offset by (15, 0, 15).
     *
     * @return this builder
     */
    FeaturePlacementBuilder all();

    /**
     * Re-emits the input positions offset to all positions in a 16x16 stencil. This will generate a
     * pseudo random distribution.
     *
     * @return this builder
     */
    FeaturePlacementBuilder stencil();
    /**
     * Similar to {@link #stencil()}, but every position in the stencil is emoted with a random chance of 1 in 4.
     *
     * @return this builder
     */
    FeaturePlacementBuilder stencilOneIn4();

    /**
     * The following process is repeated exactly {@code repetitions} times:
     * The input positions' x and z coordinates are offset by a random value between 0 and 15.
     * For the n-th repetition the system will then search for the n-th layer (beginning at
     * the top of the world). If such a layer is found, the new position (using the layers height as y
     * coordinate) is emitted. If there are less than n-layers, no new position will be emitted.
     *
     * @param repetitions The number of search repetitions (this is something like the maximum number
     *                    of layers you want to consider)
     * @return this builder
     * @see net.minecraft.world.level.levelgen.placement.CountOnEveryLayerPlacement
     */
    FeaturePlacementBuilder onEveryLayer(int repetitions);

    /**
     * Similar to {@link #onEveryLayer(int)}, but the number of repetitions
     * is randomly chosen between 0 and {@code repetitions}.
     *
     * @param repetitions The maximum number of search repetitions
     * @return this builder
     */
    FeaturePlacementBuilder onEveryLayerMax(int repetitions);

    /**
     * Search for all layers from the top of the world down to the minimum build
     * height at the input xz-coordinate and emit one resulting position on top of
     * every layer. So all emitted positions will have the same xz-coordinate,
     * but different y-coordinates.
     *
     * @return this builder
     * @see EveryLayer
     */
    FeaturePlacementBuilder onEveryLayer();

    /**
     * Similar to {@link #onEveryLayer()}, but layers are searched from the top layer of the world down to
     * y=4
     *
     * @return this builder
     */
    FeaturePlacementBuilder onEveryLayerMin4();

    /**
     * Similar to {@link #onEveryLayer()}, but positions are emitted underneath every layer.
     *
     * @return this builder
     */
    FeaturePlacementBuilder underEveryLayer();

    /**
     * Similar to {@link #onEveryLayerMin4()}, but positions are emitted underneath every layer.
     *
     * @return this builder
     */
    FeaturePlacementBuilder underEveryLayerMin4();

    /**
     * The input position is only re-emitted with a chance of 1 in {@code n}. So for
     * n=5 there is an 80% chance that the input position is not re-emitted.
     *
     * @param n the chance of re-emitting the input position
     * @return this builder
     */
    FeaturePlacementBuilder onceEvery(int n);

    /**
     * Rejects the input when the input position is no longer in the same biome that
     * triggered the feature placement.
     *
     * @return this builder
     */
    FeaturePlacementBuilder onlyInBiome();

    /**
     * Rejects the input based on {@link net.minecraft.world.level.levelgen.Noises#GRAVEL}-Noise.
     * <p>
     * The modifier will look up the noise-vale {@code v} at {@code (x*scaleXZ, y*scaleY, z*scaleXZ)}. If {@code min < v < max}
     * the position will be accepted. Otherwise, it is rejected.
     *
     * @param min     the minimum noise value (exclusive)
     * @param max     the maximum noise value (exclusive)
     * @param scaleXZ the xz-scale of the noise
     * @param scaleY  the y-scale of the noise
     * @return this builder
     * @see net.minecraft.world.level.levelgen.Noises#GRAVEL
     * @see org.betterx.wover.feature.api.placed.modifiers.NoiseFilter
     */
    FeaturePlacementBuilder noiseIn(double min, double max, float scaleXZ, float scaleY);

    /**
     * Same as {@link #noiseIn(double, double, float, float)}, but the {@code max}-value is
     * set to {@link Double#MAX_VALUE}.
     *
     * @param value   the minimum noise value (exclusive)
     * @param scaleXZ the xz-scale of the noise
     * @param scaleY  the y-scale of the noise
     * @return this builder
     */
    FeaturePlacementBuilder noiseAbove(double value, float scaleXZ, float scaleY);

    /**
     * Same as {@link #noiseIn(double, double, float, float)}, but the {@code min}-value is
     * set to -{@link Double#MAX_VALUE}.
     *
     * @param value   the maximum noise value (exclusive)
     * @param scaleXZ the xz-scale of the noise
     * @param scaleY  the y-scale of the noise
     * @return this builder
     */
    FeaturePlacementBuilder noiseBelow(double value, float scaleXZ, float scaleY);

    /**
     * Adds a random offset between 0 and 15 to the input position and emits the result.
     *
     * @return this builder
     */
    FeaturePlacementBuilder squarePlacement();

    /**
     * Moves the input position to a random height at least 10
     * Blocks above the floor and at least 10 Blocks below the ceiling.
     *
     * @return this builder
     */
    FeaturePlacementBuilder randomHeight10FromFloorCeil();

    /**
     * Moves the input position to a random height at least 4
     * Blocks above the floor and at least 4 Blocks below the ceiling.
     *
     * @return this builder
     */
    FeaturePlacementBuilder randomHeight4FromFloorCeil();

    /**
     * Moves the input position to a random height at least 8
     * Blocks above the floor and at least 8 Blocks below the ceiling.
     *
     * @return this builder
     */
    FeaturePlacementBuilder randomHeight8FromFloorCeil();

    /**
     * Moves the input position to a random height within the buildable range.
     *
     * @return this builder
     */
    FeaturePlacementBuilder randomHeight();
    /**
     * Adds a random offset to the xz-coordinate of the input position.
     *
     * @param p the offset provider
     * @return this builder
     */
    FeaturePlacementBuilder spreadHorizontal(IntProvider p);

    /**
     * Adds a random offset to the y-coordinate of the input position.
     *
     * @param p the offset provider
     * @return this builder
     */
    FeaturePlacementBuilder spreadVertical(IntProvider p);

    /**
     * Adds a random offset to the input position.
     *
     * @param horizontal the offset provider for the xz-coordinate
     * @param vertical   the offset provider for the y-coordinate
     * @return this builder
     */
    FeaturePlacementBuilder spread(IntProvider horizontal, IntProvider vertical);

    /**
     * Moves the input position one block in the given direction.
     *
     * @param dir the direction
     * @return this builder
     */
    FeaturePlacementBuilder offset(Direction dir);

    /**
     * Moves the input position by the specified vector.
     *
     * @param dir the vector to add to the input position
     * @return this builder
     */
    FeaturePlacementBuilder offset(Vec3i dir);

    /**
     * Moves the input position by the specified vector.
     *
     * @param x the provider for the offset in x direction
     * @param y the provider for the offset in y direction
     * @param z the provider for the offset in z direction
     * @return this builder
     */
    FeaturePlacementBuilder offset(int x, int y, int z);

    /**
     * Moves the input position by the specified vector.
     *
     * @param x the provider for the offset in x direction
     * @param y the provider for the offset in y direction
     * @param z the provider for the offset in z direction
     * @return this builder
     */
    FeaturePlacementBuilder offset(IntProvider x, IntProvider y, IntProvider z);

    /**
     * A randomized version of {@link #count(int)}. A noise value is calculated for the given xz-coordinate
     * if that value is smaller than {@code noiseLevel} {@code belowNoiseCount} copies of the input
     * position are emitted. Otherwise{@code aboveNoiseCount} positions are emitted.
     *
     * @param noiseLevel      value the noise value is compared against
     * @param belowNoiseCount Number of positions to emit if the noise value is smaller than {@code noiseLevel}
     * @param aboveNoiseCount Number of positions to emit if the noise value is greater or equal  {@code noiseLevel}
     * @return this builder
     */
    FeaturePlacementBuilder noiseBasedCount(float noiseLevel, int belowNoiseCount, int aboveNoiseCount);

    /**
     * Creates a column of positions downward from the input position. The height
     * of the column is between {@code min} and {@code max} Blocks.
     *
     * @param min The minimum height of the extension
     * @param max The maximum height of the extension
     * @return this builder
     */
    FeaturePlacementBuilder extendDown(int min, int max);

    /**
     * Tests if the block at the input position matches the given predicate. A
     * position is emitted if at least on of the predicate returns true
     * for the block below and the four blocks in horizontal direction
     * (north, east, south and west).
     *
     * @param predicates the predicates to test
     * @return this builder
     */
    FeaturePlacementBuilder inBasinOf(BlockPredicate... predicates);

    /**
     * Same as {@link #inBasinOf(BlockPredicate...)}, but additionally tests
     * if the block above is air.
     *
     * @param predicates the predicates to test
     * @return this builder
     */
    FeaturePlacementBuilder inOpenBasinOf(BlockPredicate... predicates);

    /**
     * Tests if the block at the input position matches any of the given predicates.
     * If all predicates return {@code false}, the input position is rejected.
     *
     * @param predicates the predicates to test
     * @return this builder
     */
    FeaturePlacementBuilder is(BlockPredicate... predicates);

    /**
     * Tests if the block below the input position matches the given predicate.
     * If the predicate returns {@code false}, the input position is rejected.
     *
     * @param predicates the predicates to test
     * @return this builder
     */
    FeaturePlacementBuilder isAbove(BlockPredicate... predicates);

    /**
     * Tests if the block above the input position matches the given predicate.
     * If the predicate returns {@code false}, the input position is rejected.
     *
     * @param predicates the predicates to test
     * @return this builder
     */
    FeaturePlacementBuilder isUnder(BlockPredicate... predicates);

    /**
     * Tries to move the input position downward to the next surface. A surface Block
     * is determined by {@link org.betterx.wover.block.api.predicate.BlockPredicates#ONLY_GROUND},
     * which basically checks if the block has the {@link org.betterx.wover.tag.api.predefined.CommonBlockTags#TERRAIN}
     * tag.
     * <p>
     * If no surface was found, the position is rejected.
     *
     * @param distance The maximum search distance
     * @return this builder
     */
    FeaturePlacementBuilder findSolidFloor(int distance);

    /**
     * Tries to move the input position upward to the next surface. (see
     * {@link #findSolidFloor(int)} for more Details)
     *
     * @param distance The maximum search distance
     * @return this builder
     */
    FeaturePlacementBuilder findSolidCeil(int distance);

    /**
     * Tries to move the input position in the given direction to the next
     * surface. (see {@link #findSolidFloor(int)} for more Details)
     *
     * @param dir      The direction to search in
     * @param distance The maximum search distance
     * @return this builder
     */
    FeaturePlacementBuilder findSolidSurface(Direction dir, int distance);

    /**
     * Tries to move the input position in a (random) direction from the given list
     * to the next surface. (see {@link #findSolidFloor(int)} for more Details)
     *
     * @param directions   The directions to search in
     * @param distance     The maximum search distance
     * @param randomSelect If {@code true}, a random direction from the list is
     *                     selected, otherwise all directions are searched in
     *                     sequence, and multiple positions are emitted (one for
     *                     each direction that found a surface in range)
     * @return this builder
     */
    FeaturePlacementBuilder findSolidSurface(List<Direction> directions, int distance, boolean randomSelect);

    /**
     * Finds all walls in the given distance from the input position. A wall is
     * a surface as defined by {@link #findSolidFloor(int)}. The resulting positions
     * are offset by {@code depth} in the search direction.
     * <p>
     * This modifier will look for walls in all horizontal directions. If a surface is found
     * in a direction, it is offset by depth in the direction the surface was found in
     * and emitted.
     *
     * @param distance The maximum search distance
     * @param depth    The offset in the search direction
     * @return this builder
     */
    FeaturePlacementBuilder onWalls(int distance, int depth);

    /**
     * Moves the input position to the height that is stored in the
     * given heightmap for the xz-coordinate
     *
     * @param types the heightmap type
     * @return this builder
     */
    FeaturePlacementBuilder onHeightmap(Heightmap.Types types);

    /**
     * Projects the point to the surface at the current xz-coordinate.
     * <p>
     * This is basically a shortcut for {@code this.heightmap().offset(0, -1, 0)}
     *
     * @return this builder
     */
    FeaturePlacementBuilder projectToSurface();

    /**
     * Convenience method for {@link #onHeightmap(Heightmap.Types)} using
     * {@link Heightmap.Types#MOTION_BLOCKING} as input.
     *
     * @return this builder
     */
    FeaturePlacementBuilder heightmap();
    /**
     * Convenience method for {@link #onHeightmap(Heightmap.Types)} using
     * {@link Heightmap.Types#OCEAN_FLOOR_WG} as input.
     *
     * @return this builder
     */
    FeaturePlacementBuilder heightmapTopSolid();
    /**
     * Convenience method for {@link #onHeightmap(Heightmap.Types)} using
     * {@link Heightmap.Types#WORLD_SURFACE_WG} as input.
     *
     * @return this builder
     */
    FeaturePlacementBuilder heightmapWorldSurface();

    /**
     * Convenience method for {@link #onHeightmap(Heightmap.Types)} using
     * {@link Heightmap.Types#OCEAN_FLOOR} as input.
     *
     * @return this builder
     */
    FeaturePlacementBuilder heightmapOceanFloor();

    /**
     * Extends the input direction in the xz-plane by the given radius. This will
     * create a new position for every block in the radius around the input.
     * <p>
     * The density of the positions can be controlled by the {@code centerDensity} and
     * {@code borderDensity} parameters. The {@code centerDensity} controls the density
     * at the input position, while the {@code borderDensity} controls the density
     * for blocks at radius distance from the input position. Positions in between
     * will be interpolated linearly. The density is the chance of a position to be
     * accepted. If the density for a position is {@code 0} the position will be
     * rejected. If the density is {@code 1} the position will be accepted.
     * <p>
     * If {@code square} is {@code true}, the positions will be created in a square
     * with edge length {@code 2*xzSpread+1} centered on the input position.
     * If {@code false}, the positions will be created in a circle.
     *
     * @param xzSpread      the exact radius in the xz-plane
     * @param centerDensity the exact density at the input position
     * @param borderDensity the exact density at the border of the radius
     * @param square        if {@code true}, the positions will be created in a square,
     *                      otherwise in a circle
     * @return this builder
     */
    FeaturePlacementBuilder extendXZ(int xzSpread, float centerDensity, float borderDensity, boolean square);

    /**
     * Similar to {@link #extendXZ(int, float, float, boolean)}, but the numeric values can
     * be number Providers, allowing you to randomize the values per feature
     *
     * @param xzSpread      the radius provider in the xz-plane
     * @param centerDensity the density provider at the input position
     * @param borderDensity the density provider at the border of the radius
     * @param square        if {@code true}, the positions will be created in a square,
     *                      otherwise in a circle
     * @return this builder
     */
    FeaturePlacementBuilder extendXZ(
            IntProvider xzSpread,
            FloatProvider centerDensity,
            FloatProvider borderDensity,
            boolean square
    );

    /**
     * Extends the input direction in the xz-plane by the given spread. This will
     * create new positions around the input position. The position will first be extended
     * by {@code xzSpread} Blocks in all horizontal directions (north, east, south and west).<br>
     * All resulting positions will then again be extended in all horizontal
     * directions by {@code xzSpread} Blocks.
     *
     * @param xzSpread the maximal spread radius. The actual radius is randomly
     *                 chosen between 0 and {@code xzSpread}
     * @return this builder
     */
    FeaturePlacementBuilder extendZigZagXZ(int xzSpread);

    /**
     * Adds {@link #extendZigZagXZ(int)} and {@link #extendDown(int, int)} to the builder
     *
     * @param xzSpread the maximal spread radius. The actual radius is randomly
     *                 chosen between 0 and {@code xzSpread}
     * @param ySpread  the vertical spread height
     * @return this builder
     */
    FeaturePlacementBuilder extendZigZagXYZ(int xzSpread, int ySpread);
    FeaturePlacementBuilder isEmpty();

    /**
     * Tests if the block at the input position matches the given predicate.
     * If the predicate returns {@code false}, the input position is rejected.
     *
     * @param predicate the predicate to test
     * @return this builder
     */
    FeaturePlacementBuilder is(BlockPredicate predicate);

    /**
     * Will only accepta  positions if all horizontal neighbors
     * (north, east, south or west) match the given predicate.
     *
     * @param predicate the predicate to test
     * @return this builder
     */
    FeaturePlacementBuilder isNextTo(BlockPredicate predicate);

    /**
     * Will only accept positions if all horizontal neighbors
     * (north, east, south or west) if the block below matches the given predicate.
     *
     * @param predicate the predicate to test
     * @return this builder
     */
    FeaturePlacementBuilder belowIsNextTo(BlockPredicate predicate);
    /**
     * Will only accept positions if all horizontal neighbors
     * (north, east, south or west) if the block at the given offset position
     * matches the given predicate.
     * <p>
     * So if you want to test if the block two blocks above the input position is
     * surrounded by gravel, you would call
     * {@code isNextTo(BlockPredicate.matchesBlocks(Blocks.GRAVEL), new Vec3i(0, 2, 0));}
     *
     * @param predicate
     * @param offset
     * @return
     */
    FeaturePlacementBuilder isNextTo(BlockPredicate predicate, Vec3i offset);

    /**
     * Will only accept positions if the block below matches the given predicate.
     * Otherwise, the position is rejected.
     *
     * @param predicate the predicate to test
     * @return this builder
     */
    FeaturePlacementBuilder isOn(BlockPredicate predicate);

    /**
     * Will only accept positions if the block at the input position is air and
     * the block below matches the given predicate. Otherwise, the position is rejected.
     *
     * @param predicate the predicate to test
     * @return this builder
     */
    FeaturePlacementBuilder isEmptyAndOn(BlockPredicate predicate);

    /**
     * Will only accept positions if the block at the input position is air and
     * the block below matches the {@link org.betterx.wover.block.api.predicate.BlockPredicates#ONLY_NYLIUM}-Predicate.
     * Otherwise, the position is rejected.
     *
     * @return this builder
     */
    FeaturePlacementBuilder isEmptyAndOnNylium();

    /**
     * Will only accept positions if the block at the input position is air and
     * the block below matches the {@link org.betterx.wover.block.api.predicate.BlockPredicates#ONLY_NETHER_GROUND}-Predicate.
     * Otherwise, the position is rejected.
     *
     * @return this builder
     */
    FeaturePlacementBuilder isEmptyAndOnNetherGround();

    /**
     * Will only accept positions if the block above matches the given predicate.
     * Otherwise, the position is rejected.
     *
     * @param predicate the predicate to test
     * @return this builder
     */
    FeaturePlacementBuilder isUnder(BlockPredicate predicate);

    /**
     * Will only accept positions if the block at the input position is air and
     * the block above matches the given predicate. Otherwise, the position is rejected.
     *
     * @param predicate the predicate to test
     * @return this builder
     */
    FeaturePlacementBuilder isEmptyAndUnder(BlockPredicate predicate);

    /**
     * Will only accept positions if the block at the input position is air and
     * the block above matches the {@link org.betterx.wover.block.api.predicate.BlockPredicates#ONLY_NYLIUM}-Predicate.
     * Otherwise, the position is rejected.
     *
     * @return this builder
     */
    FeaturePlacementBuilder isEmptyAndUnderNylium();

    /**
     * Will only accept positions if the block at the input position is air and
     * the block above matches the {@link org.betterx.wover.block.api.predicate.BlockPredicates#ONLY_NETHER_GROUND}-Predicate.
     * Otherwise, the position is rejected.
     *
     * @return this builder
     */
    FeaturePlacementBuilder isEmptyAndUnderNetherGround();

    /**
     * Will only accept a position if the block at the input position has full collision shape.
     *
     * @return this builder
     * @see IsFullShape
     */
    FeaturePlacementBuilder isFullShape();

    /**
     * Adds modifiers that emulate the behavior of vanilla  features that are placed on
     * the nether ground.
     * <p>
     * This is a convenience method for the following call sequence:
     * <pre class="java"> this
     *   .randomHeight4FromFloorCeil()
     *   .onlyInBiome()
     *   .onEveryLayer(countPerLayer)
     *   .onlyInBiome();</pre>
     *
     * @param countPerLayer the number of features per layer
     * @return this builder
     */
    FeaturePlacementBuilder vanillaNetherGround(int countPerLayer);

    /**
     * Adds modifiers that emulate the behavior of BetterNether features that are placed on
     * the nether floor.
     * <p>
     * This is a convenience method for the following call sequence:
     * <pre class="java"> this
     *   .randomHeight4FromFloorCeil()
     *   .count(count)
     *   .squarePlacement()
     *   .onlyInBiome()
     *   .onEveryLayerMin4()
     *   .onlyInBiome();</pre>
     *
     * @param count the number of starting positions. Every layer will contain
     *              the feature that many times.
     * @return this builder
     */
    FeaturePlacementBuilder betterNetherGround(int count);

    /**
     * Adds modifiers that emulate the behavior of BetterNether features that are placed on
     * the nether ceiling.
     * <p>
     * This is a convenience method for the following call sequence:
     * <pre class="java"> this
     *   .randomHeight4FromFloorCeil()
     *   .count(count)
     *   .squarePlacement()
     *   .onlyInBiome()
     *   .underEveryLayerMin4()
     *   .onlyInBiome();</pre>
     *
     * @param count the number of starting positions. Every layer will contain
     *              the feature that many times.
     * @return this builder
     */
    FeaturePlacementBuilder betterNetherCeiling(int count);

    /**
     * Adds modifiers that emulate the behavior of BetterNether features that are placed on
     * the nether wall.
     * <p>
     * This is a convenience method for the following call sequence:
     * <pre class="java"> this
     *   .count(count)
     *   .squarePlacement()
     *   .randomHeight4FromFloorCeil()
     *   .onlyInBiome()
     *   .onWalls(16, 0);</pre>
     *
     * @param count the number of starting positions. The starting positions will be randomly arrange in a 16x16
     *              square, with a randomized height.
     * @return this builder
     */
    FeaturePlacementBuilder betterNetherOnWall(int count);

    /**
     * Adds modifiers that emulate the behavior of BetterNether features that are placed in
     * the nether wall.
     * <p>
     * This is a convenience method for the following call sequence:
     * <pre class="java"> this
     *   .count(count)
     *   .squarePlacement()
     *   .randomHeight4FromFloorCeil()
     *   .onlyInBiome()
     *   .onWalls(16, 1);</pre>
     *
     * @param count the number of starting positions. The starting positions will be randomly arrange in a 16x16
     *              square, with a randomized height.
     * @return this builder
     */
    FeaturePlacementBuilder betterNetherInWall(int count);
    /**
     * Re-emits the input position, but adds a debug message to the log
     * when this PlacedFeature is placed. The message can contain a single
     * parameter (for example {@code "Stage 1 at {}"}). The parameter (if
     * present) will be replaced with the input position.
     *
     * @param caption the debug message
     * @return this builder
     */
    FeaturePlacementBuilder debug(String caption);

    /**
     * Adds all modifiers to the modifier stack
     *
     * @param modifiers the modifiers to add
     * @return this builder
     */
    FeaturePlacementBuilder modifier(PlacementModifier... modifiers);

    /**
     * Adds all modifiers to the modifier stack
     *
     * @param modifiers the modifiers to add
     * @return this builder
     */
    FeaturePlacementBuilder modifier(List<PlacementModifier> modifiers);
    /**
     * Start a new RandomPatch {@link net.minecraft.world.level.levelgen.feature.ConfiguredFeature}
     * based on this PlacedFeature.
     *
     * @return a new RandomPatch builder
     */
    RandomPatch inRandomPatch();
    /**
     * Register this PlacedFeature with the {@link BootstapContext} and {@link ResourceKey} used to create
     * this Builder. If no {@link BootstapContext} or no {@link ResourceKey} was used, the call will fail.
     *
     * @return a holder for the registered PlacedFeature
     * @throws IllegalStateException if no {@link BootstapContext} was used to create this Builder or the
     *                               {@link ResourceKey} for this builder is empty
     */
    Holder<PlacedFeature> register();
    /**
     * Create a new, anonymous Holder for this {@link PlacedFeature}. The holder can be used to
     * inline the PlacedFeature in other configurations (for example in {@link RandomPatch} or
     * {@link org.betterx.wover.feature.api.configured.configurators.AsSequence}).
     *
     * @return a new Holder for this PlacedFeature
     */
    Holder<PlacedFeature> directHolder();
}
