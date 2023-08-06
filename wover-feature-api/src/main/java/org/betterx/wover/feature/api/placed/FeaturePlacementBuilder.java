package org.betterx.wover.feature.api.placed;

import org.betterx.wover.feature.api.configured.configurators.FeatureConfigurator;
import org.betterx.wover.feature.api.configured.configurators.RandomPatch;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
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
     * Re-emits the input position exactly {@code count} times effectivley duplicating it.
     *
     * @param count the number of times to emit the input position
     * @return this builder
     */
    FeaturePlacementBuilder count(int count);

    /**
     * Same as {@link #count(int)}, but re-emits the input position between 0 and {@code count} times.
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

    FeaturePlacementBuilder stencil();
    FeaturePlacementBuilder stencilOneIn4();
    FeaturePlacementBuilder onEveryLayer(int count);
    FeaturePlacementBuilder onEveryLayerMax(int count);
    FeaturePlacementBuilder onEveryLayer();
    FeaturePlacementBuilder onEveryLayerMin4();
    FeaturePlacementBuilder underEveryLayer();
    FeaturePlacementBuilder underEveryLayerMin4();
    FeaturePlacementBuilder onceEvery(int n);
    FeaturePlacementBuilder onlyInBiome();
    FeaturePlacementBuilder noiseIn(double min, double max, float scaleXZ, float scaleY);
    FeaturePlacementBuilder noiseAbove(double value, float scaleXZ, float scaleY);
    FeaturePlacementBuilder noiseBelow(double value, float scaleXZ, float scaleY);
    FeaturePlacementBuilder squarePlacement();
    FeaturePlacementBuilder onHeightmap(Heightmap.Types types);
    FeaturePlacementBuilder randomHeight10FromFloorCeil();
    FeaturePlacementBuilder randomHeight4FromFloorCeil();
    FeaturePlacementBuilder randomHeight8FromFloorCeil();
    FeaturePlacementBuilder randomHeight();
    FeaturePlacementBuilder spreadHorizontal(IntProvider p);
    FeaturePlacementBuilder spreadVertical(IntProvider p);
    FeaturePlacementBuilder spread(IntProvider horizontal, IntProvider vertical);
    FeaturePlacementBuilder offset(Direction dir);
    FeaturePlacementBuilder offset(Vec3i dir);
    FeaturePlacementBuilder findSolidFloor(int distance);
    FeaturePlacementBuilder noiseBasedCount(float noiseLevel, int belowNoiseCount, int aboveNoiseCount);
    FeaturePlacementBuilder extendDown(int min, int max);
    FeaturePlacementBuilder inBasinOf(BlockPredicate... predicates);
    FeaturePlacementBuilder inOpenBasinOf(BlockPredicate... predicates);
    FeaturePlacementBuilder is(BlockPredicate... predicates);
    FeaturePlacementBuilder isAbove(BlockPredicate... predicates);
    FeaturePlacementBuilder isUnder(BlockPredicate... predicates);
    FeaturePlacementBuilder findSolidCeil(int distance);
    FeaturePlacementBuilder findSolidSurface(Direction dir, int distance);
    FeaturePlacementBuilder findSolidSurface(List<Direction> dir, int distance, boolean randomSelect);
    FeaturePlacementBuilder onWalls(int distance, int depth);
    FeaturePlacementBuilder heightmap();
    FeaturePlacementBuilder heightmapTopSolid();
    FeaturePlacementBuilder heightmapWorldSurface();
    FeaturePlacementBuilder extendXZ(int xzSpread);
    FeaturePlacementBuilder extendXYZ(int xzSpread, int ySpread);
    FeaturePlacementBuilder isEmpty();
    FeaturePlacementBuilder is(BlockPredicate predicate);
    FeaturePlacementBuilder isNextTo(BlockPredicate predicate);
    FeaturePlacementBuilder belowIsNextTo(BlockPredicate predicate);
    FeaturePlacementBuilder isNextTo(BlockPredicate predicate, Vec3i offset);
    FeaturePlacementBuilder isOn(BlockPredicate predicate);
    FeaturePlacementBuilder isEmptyAndOn(BlockPredicate predicate);
    FeaturePlacementBuilder isEmptyAndOnNylium();
    FeaturePlacementBuilder isEmptyAndOnNetherGround();
    FeaturePlacementBuilder isUnder(BlockPredicate predicate);
    FeaturePlacementBuilder isEmptyAndUnder(BlockPredicate predicate);
    FeaturePlacementBuilder isEmptyAndUnderNylium();
    FeaturePlacementBuilder isEmptyAndUnderNetherGround();
    FeaturePlacementBuilder isFullShape();
    FeaturePlacementBuilder vanillaNetherGround(int countPerLayer);
    FeaturePlacementBuilder betterNetherGround(int countPerLayer);
    FeaturePlacementBuilder betterNetherCeiling(int countPerLayer);
    FeaturePlacementBuilder betterNetherOnWall(int countPerLayer);
    FeaturePlacementBuilder betterNetherInWall(int countPerLayer);
    FeaturePlacementBuilder modifier(PlacementModifier... modifiers);
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
