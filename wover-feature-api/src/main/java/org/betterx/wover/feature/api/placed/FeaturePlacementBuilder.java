package org.betterx.wover.feature.api.placed;

import org.betterx.wover.block.api.BlockHelper;
import org.betterx.wover.block.api.predicate.BlockPredicates;
import org.betterx.wover.block.api.predicate.IsFullShape;
import org.betterx.wover.feature.api.configured.configurators.RandomPatch;
import org.betterx.wover.feature.api.placed.modifiers.*;
import org.betterx.wover.feature.impl.configured.FeatureConfiguratorImpl;
import org.betterx.wover.feature.impl.configured.InlineBuilderImpl;
import org.betterx.wover.feature.impl.configured.RandomPatchImpl;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FeaturePlacementBuilder {
    protected final List<PlacementModifier> modifications = new LinkedList<>();
    @Nullable
    private final ResourceKey<PlacedFeature> key;
    @NotNull
    private final Holder<ConfiguredFeature<?, ?>> configuredFeatureHolder;
    @Nullable
    private final BootstapContext<PlacedFeature> bootstrapContext;

    //Transitive Members
    @Nullable
    private final ResourceKey<ConfiguredFeature<?, ?>> transitiveConfiguredFeatureKey;

    @Nullable
    private final BiFunction<ResourceKey<ConfiguredFeature<?, ?>>, ResourceKey<PlacedFeature>, RandomPatchImpl> randomPatchBuilder;

    FeaturePlacementBuilder(
            @Nullable BootstapContext<PlacedFeature> bootstrapContext,
            @Nullable ResourceKey<PlacedFeature> key,
            @NotNull Holder<ConfiguredFeature<?, ?>> configuredFeatureHolder
    ) {
        this(bootstrapContext, key, configuredFeatureHolder, null, null);
    }

    FeaturePlacementBuilder(
            @Nullable BootstapContext<PlacedFeature> bootstrapContext,
            @Nullable ResourceKey<PlacedFeature> key,
            @NotNull Holder<ConfiguredFeature<?, ?>> configuredFeatureHolder,
            @Nullable ResourceKey<ConfiguredFeature<?, ?>> transitiveConfiguredFeatureKey,
            @Nullable BiFunction<ResourceKey<ConfiguredFeature<?, ?>>, ResourceKey<PlacedFeature>, RandomPatchImpl> randomPatchBuilder
    ) {
        this.bootstrapContext = bootstrapContext;
        this.key = key;
        this.configuredFeatureHolder = configuredFeatureHolder;
        this.transitiveConfiguredFeatureKey = transitiveConfiguredFeatureKey;
        this.randomPatchBuilder = randomPatchBuilder;
    }

    @ApiStatus.Internal
    public static FeaturePlacementBuilder withTransitive(
            FeatureConfiguratorImpl<?, ?> configuredFeatureBuilder,
            BiFunction<ResourceKey<ConfiguredFeature<?, ?>>, ResourceKey<PlacedFeature>, RandomPatchImpl> randomPatchBuilder
    ) {
        return new FeaturePlacementBuilder(
                configuredFeatureBuilder.getTransitiveBootstrapContext(),
                configuredFeatureBuilder.getTransitiveFeatureKey(),
                configuredFeatureBuilder.directHolder(),
                configuredFeatureBuilder.key,
                randomPatchBuilder
        );
    }

    /**
     * Generate feature in certain iterations (per chunk).
     *
     * @param count how many times feature will be generated in chunk.
     * @return same instance.
     */
    public FeaturePlacementBuilder count(int count) {
        return modifier(CountPlacement.of(count));
    }

    /**
     * Generate feature in certain iterations (per chunk). Count can be between 0 and max value.
     *
     * @param count maximum amount of iterations per chunk.
     * @return same instance.
     */
    public FeaturePlacementBuilder countMax(int count) {
        return modifier(CountPlacement.of(UniformInt.of(0, count)));
    }

    public FeaturePlacementBuilder countRange(int min, int max) {
        return modifier(CountPlacement.of(UniformInt.of(min, max)));
    }

    /**
     * Generate points for every xz-Coordinate in a chunk. Be carefuller, this is quite expensive!
     *
     * @return same instance.
     */
    public FeaturePlacementBuilder all() {
        return modifier(All.simple());
    }

    public FeaturePlacementBuilder stencil() {
        return modifier(Stencil.all());
    }

    public FeaturePlacementBuilder stencilOneIn4() {
        return modifier(Stencil.oneIn4());
    }


    /**
     * Generate feature in certain iterations (per chunk).
     * Feature will be generated on all layers (example - Nether plants).
     *
     * @param count how many times feature will be generated in chunk layers.
     * @return same instance.
     */
    @SuppressWarnings("deprecation")
    public FeaturePlacementBuilder onEveryLayer(int count) {
        return modifier(CountOnEveryLayerPlacement.of(count));
    }

    /**
     * Generate feature in certain iterations (per chunk). Count can be between 0 and max value.
     * Feature will be generated on all layers (example - Nether plants).
     *
     * @param count maximum amount of iterations per chunk layers.
     * @return same instance.
     */
    @SuppressWarnings("deprecation")
    public FeaturePlacementBuilder onEveryLayerMax(int count) {
        return modifier(CountOnEveryLayerPlacement.of(UniformInt.of(0, count)));
    }

    public FeaturePlacementBuilder onEveryLayer() {
        return modifier(OnEveryLayer.simple());
    }

    public FeaturePlacementBuilder onEveryLayerMin4() {
        return modifier(OnEveryLayer.min4());
    }

    public FeaturePlacementBuilder underEveryLayer() {
        return modifier(UnderEveryLayer.simple());
    }

    public FeaturePlacementBuilder underEveryLayerMin4() {
        return modifier(UnderEveryLayer.min4());
    }

    /**
     * Will place feature once every n-th attempts (in average).
     *
     * @param n amount of attempts.
     * @return same instance.
     */
    public FeaturePlacementBuilder onceEvery(int n) {
        return modifier(RarityFilter.onAverageOnceEvery(n));
    }

    /**
     * Restricts feature generation only to biome where feature was added.
     *
     * @return same instance.
     */
    public FeaturePlacementBuilder onlyInBiome() {
        return modifier(BiomeFilter.biome());
    }

    public FeaturePlacementBuilder noiseIn(double min, double max, float scaleXZ, float scaleY) {
        return modifier(new NoiseFilter(Noises.GRAVEL, min, max, scaleXZ, scaleY));
    }

    public FeaturePlacementBuilder noiseAbove(double value, float scaleXZ, float scaleY) {
        return modifier(new NoiseFilter(Noises.GRAVEL, value, Double.MAX_VALUE, scaleXZ, scaleY));
    }

    public FeaturePlacementBuilder noiseBelow(double value, float scaleXZ, float scaleY) {
        return modifier(new NoiseFilter(Noises.GRAVEL, -Double.MAX_VALUE, value, scaleXZ, scaleY));
    }

    /**
     * Randomize the xz-Coordinates
     *
     * @return same instance.
     */
    public FeaturePlacementBuilder squarePlacement() {
        return modifier(InSquarePlacement.spread());
    }

    public FeaturePlacementBuilder onHeightmap(Heightmap.Types types) {
        return modifier(HeightmapPlacement.onHeightmap(types));
    }


    /**
     * Select random height that is 10 above min Build height and 10 below max generation height
     *
     * @return The instance it was called on
     */
    public FeaturePlacementBuilder randomHeight10FromFloorCeil() {
        return modifier(PlacementUtils.RANGE_10_10);
    }

    /**
     * Select random height that is 4 above min Build height and 10 below max generation height
     *
     * @return The instance it was called on
     */
    public FeaturePlacementBuilder randomHeight4FromFloorCeil() {
        return modifier(PlacementUtils.RANGE_4_4);
    }

    /**
     * Select random height that is 8 above min Build height and 10 below max generation height
     *
     * @return The instance it was called on
     */
    public FeaturePlacementBuilder randomHeight8FromFloorCeil() {
        return modifier(PlacementUtils.RANGE_8_8);
    }

    /**
     * Select random height that is above min Build height and 10 below max generation height
     *
     * @return The instance it was called on
     */
    public FeaturePlacementBuilder randomHeight() {
        return modifier(PlacementUtils.FULL_RANGE);
    }

    public FeaturePlacementBuilder spreadHorizontal(IntProvider p) {
        return modifier(RandomOffsetPlacement.horizontal(p));
    }

    public FeaturePlacementBuilder spreadVertical(IntProvider p) {
        return modifier(RandomOffsetPlacement.horizontal(p));
    }

    public FeaturePlacementBuilder spread(IntProvider horizontal, IntProvider vertical) {
        return modifier(RandomOffsetPlacement.of(horizontal, vertical));
    }

    public FeaturePlacementBuilder offset(Direction dir) {
        return modifier(Offset.inDirection(dir));
    }

    public FeaturePlacementBuilder offset(Vec3i dir) {
        return modifier(new Offset(dir));
    }

    /**
     * Cast a downward ray with max {@code distance} length to find the next solid Block.
     *
     * @param distance The maximum search Distance
     * @return The instance it was called on
     * @see #findSolidSurface(Direction, int) for Details
     */
    public FeaturePlacementBuilder findSolidFloor(int distance) {
        return modifier(FindSolidInDirection.down(distance));
    }

    public FeaturePlacementBuilder noiseBasedCount(float noiseLevel, int belowNoiseCount, int aboveNoiseCount) {
        return modifier(NoiseThresholdCountPlacement.of(noiseLevel, belowNoiseCount, aboveNoiseCount));
    }

    public FeaturePlacementBuilder extendDown(int min, int max) {
        return modifier(new Extend(Direction.DOWN, UniformInt.of(min, max)));
    }

    public FeaturePlacementBuilder inBasinOf(BlockPredicate... predicates) {
        return modifier(new IsBasin(BlockPredicate.anyOf(predicates)));
    }

    public FeaturePlacementBuilder inOpenBasinOf(BlockPredicate... predicates) {
        return modifier(IsBasin.openTop(BlockPredicate.anyOf(predicates)));
    }

    public FeaturePlacementBuilder is(BlockPredicate... predicates) {
        return modifier(new Is(BlockPredicate.anyOf(predicates), Optional.empty()));
    }

    public FeaturePlacementBuilder isAbove(BlockPredicate... predicates) {
        return modifier(new Is(BlockPredicate.anyOf(predicates), Optional.of(Direction.DOWN.getNormal())));
    }

    public FeaturePlacementBuilder isUnder(BlockPredicate... predicates) {
        return modifier(new Is(BlockPredicate.anyOf(predicates), Optional.of(Direction.UP.getNormal())));
    }

    public FeaturePlacementBuilder findSolidCeil(int distance) {
        return modifier(FindSolidInDirection.up(distance));
    }

    /**
     * Cast a ray with max {@code distance} length to find the next solid Block. The ray will travel through replaceable
     * Blocks and will be accepted if it hits a block with the
     * {@link org.betterx.wover.tag.api.predefined.CommonBlockTags#TERRAIN}-tag
     *
     * @param dir      The direction the ray is cast
     * @param distance The maximum search Distance
     * @return The instance it was called on
     * @see #findSolidSurface(Direction, int) for Details
     */
    public FeaturePlacementBuilder findSolidSurface(Direction dir, int distance) {
        return modifier(new FindSolidInDirection(dir, distance, 0));
    }

    public FeaturePlacementBuilder findSolidSurface(List<Direction> dir, int distance, boolean randomSelect) {
        return modifier(new FindSolidInDirection(dir, distance, randomSelect, 0));
    }

    public FeaturePlacementBuilder onWalls(int distance, int depth) {
        return modifier(new FindSolidInDirection(BlockHelper.HORIZONTAL, distance, false, depth));
    }

    public FeaturePlacementBuilder heightmap() {
        return modifier(PlacementUtils.HEIGHTMAP);
    }

    public FeaturePlacementBuilder heightmapTopSolid() {
        return modifier(PlacementUtils.HEIGHTMAP_TOP_SOLID);
    }

    public FeaturePlacementBuilder heightmapWorldSurface() {
        return modifier(PlacementUtils.HEIGHTMAP_WORLD_SURFACE);
    }

    public FeaturePlacementBuilder extendXZ(int xzSpread) {
        IntProvider xz = UniformInt.of(0, xzSpread);
        return modifier(
                new ForAll(List.of(
                        new Extend(Direction.NORTH, xz),
                        new Extend(Direction.SOUTH, xz),
                        new Extend(Direction.EAST, xz),
                        new Extend(Direction.WEST, xz)
                )),
                new ForAll(List.of(
                        new Extend(Direction.EAST, xz),
                        new Extend(Direction.WEST, xz),
                        new Extend(Direction.NORTH, xz),
                        new Extend(Direction.SOUTH, xz)
                ))
        );
    }

    public FeaturePlacementBuilder extendXYZ(int xzSpread, int ySpread) {
        IntProvider xz = UniformInt.of(0, xzSpread);
        return extendXZ(xzSpread).extendDown(1, ySpread);
    }

    public FeaturePlacementBuilder isEmpty() {
        return modifier(BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE));
    }


    public FeaturePlacementBuilder is(BlockPredicate predicate) {
        return modifier(BlockPredicateFilter.forPredicate(predicate));
    }

    public FeaturePlacementBuilder isNextTo(BlockPredicate predicate) {
        return modifier(new IsNextTo(predicate));
    }

    public FeaturePlacementBuilder belowIsNextTo(BlockPredicate predicate) {
        return modifier(new IsNextTo(predicate, Direction.DOWN.getNormal()));
    }

    public FeaturePlacementBuilder isNextTo(BlockPredicate predicate, Vec3i offset) {
        return modifier(new IsNextTo(predicate, offset));
    }

    public FeaturePlacementBuilder isOn(BlockPredicate predicate) {
        return modifier(Is.below(predicate));
    }

    public FeaturePlacementBuilder isEmptyAndOn(BlockPredicate predicate) {
        return this.isEmpty().isOn(predicate);
    }

    public FeaturePlacementBuilder isEmptyAndOnNylium() {
        return isEmptyAndOn(BlockPredicates.ONLY_NYLIUM);
    }

    public FeaturePlacementBuilder isEmptyAndOnNetherGround() {
        return isEmptyAndOn(BlockPredicates.ONLY_NETHER_GROUND);
    }

    public FeaturePlacementBuilder isUnder(BlockPredicate predicate) {
        return modifier(Is.above(predicate));
    }

    public FeaturePlacementBuilder isEmptyAndUnder(BlockPredicate predicate) {
        return this.isEmpty().isUnder(predicate);
    }

    public FeaturePlacementBuilder isEmptyAndUnderNylium() {
        return isEmptyAndUnder(BlockPredicates.ONLY_NYLIUM);
    }

    public FeaturePlacementBuilder isEmptyAndUnderNetherGround() {
        return isEmptyAndUnder(BlockPredicates.ONLY_NETHER_GROUND);
    }

    public FeaturePlacementBuilder isFullShape() {
        return isEmptyAndUnder(IsFullShape.HERE);
    }

    public FeaturePlacementBuilder vanillaNetherGround(int countPerLayer) {
        return this.randomHeight4FromFloorCeil().onlyInBiome().onEveryLayer(countPerLayer).onlyInBiome();
    }

    public FeaturePlacementBuilder betterNetherGround(int countPerLayer) {
        return this.randomHeight4FromFloorCeil()
                   .count(countPerLayer)
                   .squarePlacement()
                   .onlyInBiome()
                   .onEveryLayerMin4()
                   .onlyInBiome();
    }

    public FeaturePlacementBuilder betterNetherCeiling(int countPerLayer) {
        return this.randomHeight4FromFloorCeil()
                   .count(countPerLayer)
                   .squarePlacement()
                   .onlyInBiome()
                   .underEveryLayerMin4()
                   .onlyInBiome();
    }

    public FeaturePlacementBuilder betterNetherOnWall(int countPerLayer) {
        return this.count(countPerLayer)
                   .squarePlacement()
                   .randomHeight4FromFloorCeil()
                   .onlyInBiome()
                   .onWalls(16, 0);
    }

    public FeaturePlacementBuilder betterNetherInWall(int countPerLayer) {
        return this.count(countPerLayer)
                   .squarePlacement()
                   .randomHeight4FromFloorCeil()
                   .onlyInBiome()
                   .onWalls(16, 1);
    }

    /**
     * Add feature placement modifier.
     * <p>
     * Modifiers are applied in the order they are added.
     * They are used to filter and repostion the possible feature locations.
     *
     * @param modifiers {@link PlacementModifier}s to add.
     * @return the same builder.
     */
    public FeaturePlacementBuilder modifier(PlacementModifier... modifiers) {
        for (var m : modifiers) {
            modifications.add(m);
        }
        return this;
    }

    /**
     * Add feature placement modifier. Used as a condition for feature how to generate.
     *
     * @param modifiers {@link PlacementModifier}s to add.
     * @return same instance.
     */
    public FeaturePlacementBuilder modifier(List<PlacementModifier> modifiers) {
        modifications.addAll(modifiers);
        return this;
    }

    /**
     * Creates a new {@link RandomPatch} {@link org.betterx.wover.feature.api.configured.configurators.FeatureConfigurator}
     *
     * @return {@link RandomPatch} instance.
     */
    public RandomPatch inRandomPatch() {
        final RandomPatch randomPatch;
        if (randomPatchBuilder != null) {
            randomPatch = randomPatchBuilder.apply(transitiveConfiguredFeatureKey, key);
        } else {
            randomPatch = new InlineBuilderImpl(this.bootstrapContext, this.key).randomPatch();
        }

        return randomPatch.featureToPlace(directHolder());
    }


    public Holder<PlacedFeature> register() {
        if (key == null) {
            throw new IllegalStateException("A ResourceKey for a Feature can not be null if it should be registered!");
        }
        if (bootstrapContext == null) {
            throw new IllegalStateException(
                    "A BootstrapContext for a Feature can not be null if it should be registered! (" + key.location() + ")"
            );
        }
        PlacedFeature feature = build();
        return bootstrapContext.register(key, feature);
    }

    public Holder<PlacedFeature> directHolder() {
        return Holder.direct(build());
    }


    @NotNull
    private PlacedFeature build() {
        return new PlacedFeature(configuredFeatureHolder, modifications);
    }

}
