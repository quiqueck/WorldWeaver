package de.ambertation.wover.feature.api.features;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.Optional;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

/**
 * A feature that places another feature based on a condition (<b>{@code wover:condition}</b>).
 * <p>
 * The condition is expressed as a {@link PlacementModifier}. You can configure two features, one to place if
 * the condition is met, and one to place if the condition is not met.
 * <p>
 * 26.3 collapsed {@code ConfiguredFeature} into {@link Feature}, so the former
 * {@code ConditionFeatureConfig} was merged into this class. Field names ({@code filter},
 * {@code filter_pass}, {@code filter_fail}) are unchanged, but they now sit at the top level of the feature
 * JSON instead of inside a {@code config} object:
 * <pre class="json"> {
 *   "type": "wover:condition",
 *   "filter": {
 *     "type": "minecraft:block_predicate_filter",
 *     "predicate": {
 *       "type": "minecraft:matching_blocks",
 *       "blocks": "minecraft:air"
 *     }
 *   },
 *   "filter_fail": "minecraft:blue_ice",
 *   "filter_pass": "minecraft:desert_well"
 * }</pre>
 * And when using java, you would do something like this in order to create the above {@link Feature}:
 * <pre class="java"> FeatureKey&lt;WithFeature&lt;ConditionFeature>> TEST_KEY = FeatureContentManager.withFeature(
 *     modCore.id("test"),
 *     new ConditionFeature(
 *         BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE),
 *         PlacedFeatureManager.getHolder(context, MiscOverworldPlacements.DESERT_WELL),
 *         PlacedFeatureManager.getHolder(context, MiscOverworldPlacements.BLUE_ICE)
 *     )
 * );
 *
 * TEST_KEY.bootstrap(context).register();</pre>
 *
 * @see de.ambertation.wover.feature.api.Features#CONDITION
 */
public class ConditionFeature implements Feature {
    /**
     * The codec for this feature.
     */
    public static final MapCodec<ConditionFeature> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    PlacementModifier.CODEC.fieldOf("filter").forGetter(p -> p.filter),
                    PlacedFeature.CODEC.fieldOf("filter_pass").forGetter(p -> p.okFeature),
                    PlacedFeature.CODEC.optionalFieldOf("filter_fail").forGetter(p -> p.failFeature)
            ).apply(instance, ConditionFeature::new)
    );

    /**
     * The condition to check.
     */
    public final PlacementModifier filter;
    /**
     * The feature to place if the condition is met.
     */
    public final Holder<PlacedFeature> okFeature;
    /**
     * The feature to place if the condition is not met. If the optional
     * is empty, the feature will not be placed.
     */
    public final Optional<Holder<PlacedFeature>> failFeature;

    /**
     * Creates a new feature that will only place a feature if the condition is met
     *
     * @param filter    The condition to check
     * @param okFeature The feature to place if the condition is met
     */
    public ConditionFeature(
            @NotNull PlacementFilter filter,
            @NotNull Holder<PlacedFeature> okFeature
    ) {
        this(filter, okFeature, Optional.empty());
    }

    /**
     * Creates a new feature that will place a feature if the condition is met, and another
     * feature if the condition is not.
     *
     * @param filter      The condition to check
     * @param okFeature   The feature to place if the condition is met
     * @param failFeature The feature to place if the condition is not met
     */
    public ConditionFeature(
            @NotNull PlacementFilter filter,
            @NotNull Holder<PlacedFeature> okFeature,
            @NotNull Holder<PlacedFeature> failFeature
    ) {
        this(filter, okFeature, Optional.of(failFeature));
    }

    private ConditionFeature(
            @NotNull PlacementModifier filter,
            @NotNull Holder<PlacedFeature> okFeature,
            @NotNull Optional<Holder<PlacedFeature>> failFeature
    ) {
        this.filter = filter;
        this.okFeature = okFeature;
        this.failFeature = failFeature;
    }

    /**
     * The codec that serializes this feature.
     *
     * @return {@link #CODEC}
     */
    @Override
    public MapCodec<ConditionFeature> codec() {
        return CODEC;
    }

    /**
     * The features this one may delegate to. Both branches are reported, because either can be placed.
     *
     * @return the features of the pass branch and (if present) the fail branch
     */
    @Override
    public Stream<Holder<Feature>> getSubFeatures() {
        return Stream.concat(
                okFeature.value().getFeatures(),
                failFeature.stream().flatMap(f -> f.value().getFeatures())
        );
    }

    /**
     * Places the feature if the condition is met
     *
     * @param level     the level to place in
     * @param generator the chunk generator
     * @param random    the random source
     * @param pos       the position to place at
     * @return {@code true} if the feature was placed
     */
    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos pos) {
        final PlacementContext c = new PlacementContext(level, generator, Optional.empty());

        // 26.3 replaced PlacementModifier#getPositions(...) -> Stream<BlockPos> with
        // modify(..., Consumer<BlockPos>). We only care whether the filter emits anything at all, so a
        // boolean flag replaces the former stream.findFirst().isPresent().
        final boolean[] passed = new boolean[1];
        filter.modify(c, random, pos, p -> passed[0] = true);

        Holder<PlacedFeature> state = passed[0] ? okFeature : failFeature.orElse(null);
        if (state != null) {
            return state.value().place(level, generator, random, pos);
        }
        return false;
    }
}
