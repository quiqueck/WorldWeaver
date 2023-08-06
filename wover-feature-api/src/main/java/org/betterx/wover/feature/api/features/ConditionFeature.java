package org.betterx.wover.feature.api.features;

import org.betterx.wover.feature.api.features.config.ConditionFeatureConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * A feature that places another feature based on a condition (<b>{@code wover:condition}</b>).
 * <p>
 * The condition is expressed as a {@link net.minecraft.world.level.levelgen.placement.PlacementModifier}.
 * You can configure two features, one to place if the condition is met, and one to place if the
 * condition is not met.
 * <p>
 * This is how you would define a configured feature of this type  in JSON:
 * <pre class="json"> {
 *   "type": "wover:condition",
 *   "config": {
 *     "filter": {
 *       "type": "minecraft:block_predicate_filter",
 *       "predicate": {
 *         "type": "minecraft:matching_blocks",
 *         "blocks": "minecraft:air"
 *       }
 *     },
 *     "filter_fail": "minecraft:blue_ice",
 *     "filter_pass": "minecraft:desert_well"
 *   }
 * }</pre>
 * And when using java, you would do something like this in order to create the above
 * {@link net.minecraft.world.level.levelgen.feature.ConfiguredFeature}:
 * <pre class="java"> ConfiguredFeatureKey&lt;WithConfiguration&lt;Feature&lt;ConditionFeatureConfig>, ConditionFeatureConfig>> TEST_KEY;
 *
 * TEST_KEY = ConfiguredFeatureManager.configuration(
 *     modCore.id("test"),
 *     Features.CONDITION
 * );
 *
 * TEST_KEY.bootstrap(context)
 *         .configuration(new ConditionFeatureConfig(
 *             BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE),
 *             PlacedFeatureManager.getHolder(context, MiscOverworldPlacements.DESERT_WELL),
 *             PlacedFeatureManager.getHolder(context, MiscOverworldPlacements.BLUE_ICE)
 *         ))
 *         .register();</pre>
 *
 * @see ConditionFeatureConfig
 * @see org.betterx.wover.feature.api.Features#CONDITION
 */
public class ConditionFeature extends Feature<ConditionFeatureConfig> {
    /**
     * Creates a new instance.
     */
    public ConditionFeature() {
        super(ConditionFeatureConfig.CODEC);
    }

    /**
     * Places the feature if the condition is met
     *
     * @param ctx The placement context
     * @return {@code true} if the feature was placed
     */
    @Override
    public boolean place(FeaturePlaceContext<ConditionFeatureConfig> ctx) {
        final ConditionFeatureConfig cfg = ctx.config();
        final WorldGenLevel level = ctx.level();
        final RandomSource random = ctx.random();
        final BlockPos pos = ctx.origin();

        final PlacementContext c = new PlacementContext(level, ctx.chunkGenerator(), Optional.empty());

        Stream<BlockPos> stream = cfg.filter.getPositions(c, ctx.random(), pos);
        Holder<PlacedFeature> state = (stream.findFirst().isPresent() ? cfg.okFeature : cfg.failFeature.orElse(null));
        if (state != null) {
            return state.value().place(level, ctx.chunkGenerator(), random, pos);
        }
        return false;
    }
}
