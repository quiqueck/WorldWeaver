package org.betterx.wover.feature.impl.placed;

import org.betterx.wover.block.api.BlockHelper;
import org.betterx.wover.block.api.predicate.BlockPredicates;
import org.betterx.wover.feature.api.configured.configurators.RandomPatch;
import org.betterx.wover.feature.api.placed.FeaturePlacementBuilder;
import org.betterx.wover.feature.api.placed.modifiers.*;
import org.betterx.wover.feature.impl.configured.FeatureConfiguratorImpl;
import org.betterx.wover.feature.impl.configured.InlineBuilderImpl;
import org.betterx.wover.feature.impl.configured.RandomPatchImpl;
import org.betterx.wover.math.api.valueproviders.Vec3iProvider;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.*;
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

public class FeaturePlacementBuilderImpl implements org.betterx.wover.feature.api.placed.FeaturePlacementBuilder {
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

    public FeaturePlacementBuilderImpl(
            @Nullable BootstapContext<PlacedFeature> bootstrapContext,
            @Nullable ResourceKey<PlacedFeature> key,
            @NotNull Holder<ConfiguredFeature<?, ?>> configuredFeatureHolder
    ) {
        this(bootstrapContext, key, configuredFeatureHolder, null, null);
    }

    public FeaturePlacementBuilderImpl(
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
    public static FeaturePlacementBuilderImpl withTransitive(
            FeatureConfiguratorImpl<?, ?> configuredFeatureBuilder,
            BiFunction<ResourceKey<ConfiguredFeature<?, ?>>, ResourceKey<PlacedFeature>, RandomPatchImpl> randomPatchBuilder
    ) {
        return new FeaturePlacementBuilderImpl(
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
    @Override
    public FeaturePlacementBuilderImpl count(int count) {
        return modifier(CountPlacement.of(count));
    }

    /**
     * Generate feature in certain iterations (per chunk). Count can be between 0 and max value.
     *
     * @param count maximum amount of iterations per chunk.
     * @return same instance.
     */
    @Override
    public FeaturePlacementBuilderImpl countMax(int count) {
        return modifier(CountPlacement.of(UniformInt.of(0, count)));
    }

    @Override
    public FeaturePlacementBuilderImpl countRange(int min, int max) {
        return modifier(CountPlacement.of(UniformInt.of(min, max)));
    }

    /**
     * Generate points for every xz-Coordinate in a chunk. Be carefuller, this is quite expensive!
     *
     * @return same instance.
     */
    @Override
    public FeaturePlacementBuilderImpl all() {
        return modifier(All.simple());
    }

    @Override
    public FeaturePlacementBuilderImpl stencil() {
        return modifier(Stencil.all());
    }

    @Override
    public FeaturePlacementBuilderImpl stencilOneIn4() {
        return modifier(Stencil.oneIn4());
    }


    /**
     * Generate feature in certain iterations (per chunk).
     * Feature will be generated on all layers (example - Nether plants).
     *
     * @param repetitions how many times feature will be generated in chunk layers.
     * @return same instance.
     */
    @Override
    @SuppressWarnings("deprecation")
    public FeaturePlacementBuilderImpl onEveryLayer(int repetitions) {
        return modifier(CountOnEveryLayerPlacement.of(repetitions));
    }

    /**
     * Generate feature in certain iterations (per chunk). Count can be between 0 and max value.
     * Feature will be generated on all layers (example - Nether plants).
     *
     * @param count maximum amount of iterations per chunk layers.
     * @return same instance.
     */
    @Override
    @SuppressWarnings("deprecation")
    public FeaturePlacementBuilderImpl onEveryLayerMax(int count) {
        return modifier(CountOnEveryLayerPlacement.of(UniformInt.of(0, count)));
    }

    @Override
    public FeaturePlacementBuilderImpl onEveryLayer() {
        return modifier(EveryLayer.on());
    }

    @Override
    public FeaturePlacementBuilderImpl onEveryLayerMin4() {
        return modifier(EveryLayer.onTopMin4());
    }

    @Override
    public FeaturePlacementBuilderImpl underEveryLayer() {
        return modifier(EveryLayer.underneath());
    }

    @Override
    public FeaturePlacementBuilderImpl underEveryLayerMin4() {
        return modifier(EveryLayer.underneathMin4());
    }

    /**
     * Will place feature once every n-th attempts (in average).
     *
     * @param n amount of attempts.
     * @return same instance.
     */
    @Override
    public FeaturePlacementBuilderImpl onceEvery(int n) {
        return modifier(RarityFilter.onAverageOnceEvery(n));
    }

    /**
     * Restricts feature generation only to biome where feature was added.
     *
     * @return same instance.
     */
    @Override
    public FeaturePlacementBuilderImpl onlyInBiome() {
        return modifier(BiomeFilter.biome());
    }

    @Override
    public FeaturePlacementBuilderImpl noiseIn(double min, double max, float scaleXZ, float scaleY) {
        return modifier(new NoiseFilter(Noises.GRAVEL, min, max, scaleXZ, scaleY));
    }

    @Override
    public FeaturePlacementBuilderImpl noiseAbove(double value, float scaleXZ, float scaleY) {
        return modifier(new NoiseFilter(Noises.GRAVEL, value, Double.MAX_VALUE, scaleXZ, scaleY));
    }

    @Override
    public FeaturePlacementBuilderImpl noiseBelow(double value, float scaleXZ, float scaleY) {
        return modifier(new NoiseFilter(Noises.GRAVEL, -Double.MAX_VALUE, value, scaleXZ, scaleY));
    }

    @Override
    public FeaturePlacementBuilderImpl squarePlacement() {
        return modifier(InSquarePlacement.spread());
    }

    @Override
    public FeaturePlacementBuilderImpl randomHeight10FromFloorCeil() {
        return modifier(PlacementUtils.RANGE_10_10);
    }

    @Override
    public FeaturePlacementBuilderImpl randomHeight4FromFloorCeil() {
        return modifier(PlacementUtils.RANGE_4_4);
    }

    @Override
    public FeaturePlacementBuilderImpl randomHeight8FromFloorCeil() {
        return modifier(PlacementUtils.RANGE_8_8);
    }

    @Override
    public FeaturePlacementBuilderImpl randomHeight() {
        return modifier(PlacementUtils.FULL_RANGE);
    }

    @Override
    public FeaturePlacementBuilderImpl spreadHorizontal(IntProvider p) {
        return modifier(RandomOffsetPlacement.horizontal(p));
    }

    @Override
    public FeaturePlacementBuilderImpl spreadVertical(IntProvider p) {
        return modifier(RandomOffsetPlacement.horizontal(p));
    }

    @Override
    public FeaturePlacementBuilderImpl spread(IntProvider horizontal, IntProvider vertical) {
        return modifier(RandomOffsetPlacement.of(horizontal, vertical));
    }

    @Override
    public FeaturePlacementBuilderImpl offset(Direction dir) {
        return modifier(Offset.inDirection(dir));
    }

    @Override
    public FeaturePlacementBuilderImpl offset(Vec3i dir) {
        return modifier(new Offset(dir));
    }

    @Override
    public FeaturePlacementBuilderImpl offset(int x, int y, int z) {
        return offset(new Vec3i(x, y, z));
    }

    @Override
    public FeaturePlacementBuilderImpl offset(IntProvider x, IntProvider y, IntProvider z) {
        return modifier(new OffsetProvider(new Vec3iProvider(x, y, z)));
    }

    @Override
    public FeaturePlacementBuilderImpl noiseBasedCount(float noiseLevel, int belowNoiseCount, int aboveNoiseCount) {
        return modifier(NoiseThresholdCountPlacement.of(noiseLevel, belowNoiseCount, aboveNoiseCount));
    }

    @Override
    public FeaturePlacementBuilderImpl extendDown(int min, int max) {
        return modifier(new Extend(Direction.DOWN, UniformInt.of(min, max)));
    }

    @Override
    public FeaturePlacementBuilderImpl inBasinOf(BlockPredicate... predicates) {
        return modifier(IsBasin.simple(BlockPredicate.anyOf(predicates)));
    }

    @Override
    public FeaturePlacementBuilderImpl inOpenBasinOf(BlockPredicate... predicates) {
        return modifier(IsBasin.openTop(BlockPredicate.anyOf(predicates)));
    }

    @Override
    public FeaturePlacementBuilderImpl is(BlockPredicate... predicates) {
        return modifier(new Is(BlockPredicate.anyOf(predicates), Optional.empty()));
    }

    @Override
    public FeaturePlacementBuilderImpl isAbove(BlockPredicate... predicates) {
        return modifier(new Is(BlockPredicate.anyOf(predicates), Optional.of(Direction.DOWN.getNormal())));
    }

    @Override
    public FeaturePlacementBuilderImpl isUnder(BlockPredicate... predicates) {
        return modifier(new Is(BlockPredicate.anyOf(predicates), Optional.of(Direction.UP.getNormal())));
    }

    @Override
    public FeaturePlacementBuilderImpl findSolidFloor(int distance) {
        return modifier(FindInDirection.down(distance));
    }

    @Override
    public FeaturePlacementBuilderImpl findSolidCeil(int distance) {
        return modifier(FindInDirection.up(distance));
    }

    @Override
    public FeaturePlacementBuilderImpl findSolidSurface(Direction dir, int distance) {
        return modifier(new FindInDirection(dir, distance, 0, BlockPredicates.ONLY_GROUND));
    }

    @Override
    public FeaturePlacementBuilderImpl findSolidSurface(List<Direction> dir, int distance, boolean randomSelect) {
        return modifier(new FindInDirection(dir, distance, randomSelect, 0, BlockPredicates.ONLY_GROUND));
    }

    @Override
    public FeaturePlacementBuilderImpl onWalls(int distance, int depth) {
        return modifier(new FindInDirection(
                BlockHelper.HORIZONTAL,
                distance,
                false,
                depth,
                BlockPredicates.ONLY_GROUND
        ));
    }

    @Override
    public FeaturePlacementBuilderImpl onHeightmap(Heightmap.Types types) {
        return modifier(HeightmapPlacement.onHeightmap(types));
    }

    @Override
    public FeaturePlacementBuilder projectToSurface() {
        return this.heightmap().offset(0, -1, 0);
    }

    @Override
    public FeaturePlacementBuilderImpl heightmap() {
        return modifier(PlacementUtils.HEIGHTMAP);
    }

    @Override
    public FeaturePlacementBuilderImpl heightmapTopSolid() {
        return modifier(PlacementUtils.HEIGHTMAP_TOP_SOLID);
    }

    @Override
    public FeaturePlacementBuilderImpl heightmapWorldSurface() {
        return modifier(PlacementUtils.HEIGHTMAP_WORLD_SURFACE);
    }


    @Override
    public FeaturePlacementBuilder heightmapOceanFloor() {
        return modifier(PlacementUtils.HEIGHTMAP_OCEAN_FLOOR);
    }

    @Override
    public FeaturePlacementBuilderImpl extendXZ(
            int xzSpread,
            float centerDensity,
            float borderDensity,
            boolean square
    ) {
        return this.modifier(new ExtendXZ(
                ConstantInt.of(xzSpread),
                ConstantFloat.of(centerDensity),
                ConstantFloat.of(borderDensity),
                square
        ));
    }

    @Override
    public FeaturePlacementBuilder extendXZ(
            IntProvider xzSpread,
            FloatProvider centerDensity,
            FloatProvider borderDensity,
            boolean square
    ) {
        return this.modifier(new ExtendXZ(
                xzSpread,
                centerDensity,
                borderDensity,
                square
        ));
    }

    @Override
    public FeaturePlacementBuilderImpl extendZigZagXZ(int xzSpread) {
        IntProvider xz = UniformInt.of(0, xzSpread);
        return modifier(
                new Merge(List.of(
                        new Extend(Direction.NORTH, xz),
                        new Extend(Direction.SOUTH, xz),
                        new Extend(Direction.EAST, xz),
                        new Extend(Direction.WEST, xz)
                )),
                new Merge(List.of(
                        new Extend(Direction.EAST, xz),
                        new Extend(Direction.WEST, xz),
                        new Extend(Direction.NORTH, xz),
                        new Extend(Direction.SOUTH, xz)
                ))
        );
    }

    @Override
    public FeaturePlacementBuilderImpl extendZigZagXYZ(int xzSpread, int ySpread) {
        IntProvider xz = UniformInt.of(0, xzSpread);
        return extendZigZagXZ(xzSpread).extendDown(1, ySpread);
    }

    @Override
    public FeaturePlacementBuilderImpl isEmpty() {
        return modifier(BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE));
    }


    @Override
    public FeaturePlacementBuilderImpl is(BlockPredicate predicate) {
        return modifier(BlockPredicateFilter.forPredicate(predicate));
    }

    @Override
    public FeaturePlacementBuilderImpl isNextTo(BlockPredicate predicate) {
        return modifier(IsNextTo.simple(predicate));
    }

    @Override
    public FeaturePlacementBuilderImpl belowIsNextTo(BlockPredicate predicate) {
        return modifier(IsNextTo.offset(predicate, Direction.DOWN.getNormal()));
    }

    @Override
    public FeaturePlacementBuilderImpl isNextTo(BlockPredicate predicate, Vec3i offset) {
        return modifier(IsNextTo.offset(predicate, offset));
    }

    @Override
    public FeaturePlacementBuilderImpl isOn(BlockPredicate predicate) {
        return modifier(Is.below(predicate));
    }

    @Override
    public FeaturePlacementBuilderImpl isEmptyAndOn(BlockPredicate predicate) {
        return this.isEmpty().isOn(predicate);
    }

    @Override
    public FeaturePlacementBuilderImpl isEmptyAndOnNylium() {
        return isEmptyAndOn(BlockPredicates.ONLY_NYLIUM);
    }

    @Override
    public FeaturePlacementBuilderImpl isEmptyAndOnNetherGround() {
        return isEmptyAndOn(BlockPredicates.ONLY_NETHER_GROUND);
    }

    @Override
    public FeaturePlacementBuilderImpl isUnder(BlockPredicate predicate) {
        return modifier(Is.above(predicate));
    }

    @Override
    public FeaturePlacementBuilderImpl isEmptyAndUnder(BlockPredicate predicate) {
        return this.isEmpty().isUnder(predicate);
    }

    @Override
    public FeaturePlacementBuilderImpl isEmptyAndUnderNylium() {
        return isEmptyAndUnder(BlockPredicates.ONLY_NYLIUM);
    }

    @Override
    public FeaturePlacementBuilderImpl isEmptyAndUnderNetherGround() {
        return isEmptyAndUnder(BlockPredicates.ONLY_NETHER_GROUND);
    }

    @Override
    public FeaturePlacementBuilderImpl isFullShape() {
        return isEmptyAndUnder(BlockPredicates.IS_FULL_BLOCK);
    }

    @Override
    public FeaturePlacementBuilderImpl vanillaNetherGround(int countPerLayer) {
        return this.randomHeight4FromFloorCeil().onlyInBiome().onEveryLayer(countPerLayer).onlyInBiome();
    }

    @Override
    public FeaturePlacementBuilderImpl betterNetherGround(int countPerLayer) {
        return this.randomHeight4FromFloorCeil()
                   .count(countPerLayer)
                   .squarePlacement()
                   .onlyInBiome()
                   .onEveryLayerMin4()
                   .onlyInBiome();
    }

    @Override
    public FeaturePlacementBuilderImpl betterNetherCeiling(int countPerLayer) {
        return this.randomHeight4FromFloorCeil()
                   .count(countPerLayer)
                   .squarePlacement()
                   .onlyInBiome()
                   .underEveryLayerMin4()
                   .onlyInBiome();
    }

    @Override
    public FeaturePlacementBuilderImpl betterNetherOnWall(int countPerLayer) {
        return this.count(countPerLayer)
                   .squarePlacement()
                   .randomHeight4FromFloorCeil()
                   .onlyInBiome()
                   .onWalls(16, 0);
    }

    @Override
    public FeaturePlacementBuilderImpl betterNetherInWall(int countPerLayer) {
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
    @Override
    public FeaturePlacementBuilderImpl modifier(PlacementModifier... modifiers) {
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
    @Override
    public FeaturePlacementBuilderImpl modifier(List<PlacementModifier> modifiers) {
        modifications.addAll(modifiers);
        return this;
    }

    @Override
    public FeaturePlacementBuilderImpl debug(String caption) {
        return modifier(new Debug(caption));
    }

    /**
     * Creates a new {@link RandomPatch} {@link org.betterx.wover.feature.api.configured.configurators.FeatureConfigurator}
     *
     * @return {@link RandomPatch} instance.
     */
    @Override
    public RandomPatch inRandomPatch() {
        final RandomPatch randomPatch;
        if (randomPatchBuilder != null) {
            randomPatch = randomPatchBuilder.apply(transitiveConfiguredFeatureKey, key);
        } else {
            randomPatch = new InlineBuilderImpl(this.bootstrapContext, this.key).randomPatch();
        }

        return randomPatch.featureToPlace(directHolder());
    }


    @Override
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

    @Override
    public Holder<PlacedFeature> directHolder() {
        return Holder.direct(build());
    }


    @NotNull
    public PlacedFeature build() {
        return new PlacedFeature(configuredFeatureHolder, modifications);
    }
}
