package org.betterx.wover.feature.api.features.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;

/**
 * Configuration for {@link org.betterx.wover.feature.api.features.ConditionFeature}.
 * <h2>JSON Example</h2>
 * In this example, we test if the selected position is an air block. If it is, we place a desert well
 * at the position. If it is not, we place blue ice.
 * <pre class="json"> {
 *     "filter": {
 *       "type": "minecraft:block_predicate_filter",
 *       "predicate": {
 *         "type": "minecraft:matching_blocks",
 *         "blocks": "minecraft:air"
 *       }
 *     },
 *     "filter_fail": "minecraft:blue_ice",
 *     "filter_pass": "minecraft:desert_well"
 * }</pre>
 * In the above example, we use resource locations to reference the features, however, you can also
 * inline the feature definition (in this example we do not supply a feature in case
 * the condition is not met):
 * <pre class="json"> {
 *     "filter": {
 *       "type": "minecraft:block_predicate_filter",
 *       "predicate": {
 *         "type": "minecraft:matching_blocks",
 *         "blocks": "minecraft:air"
 *       }
 *     },
 *     "filter_pass": {
 *       "feature": {
 *         "type": "minecraft:simple_block",
 *         "config": {
 *           "to_place": {
 *             "type": "minecraft:simple_state_provider",
 *             "state": {
 *               "Name": "minecraft:amethyst_block"
 *             }
 *           }
 *         }
 *       },
 *       "placement": [
 *         {
 *           "type": "minecraft:biome"
 *         },
 *         {
 *           "type": "minecraft:count",
 *           "count": 16
 *         },
 *         {
 *           "type": "minecraft:in_square"
 *         }
 *       ]
 *     }
 * } </pre>
 * <h2>Java Example</h2>
 * Here is how you would create and register the first example using java code:
 * <pre class="java"> new ConditionFeatureConfig(
 *     BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE),
 *     PlacedFeatureManager.getHolder(context, MiscOverworldPlacements.DESERT_WELL),
 *     PlacedFeatureManager.getHolder(context, MiscOverworldPlacements.BLUE_ICE)
 * );</pre>
 * And for the second example:
 * <pre class="java"> new ConditionFeatureConfig(
 *     BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE),
 *     ConfiguredFeatureManager.INLINE_BUILDER
 *                             .simple()
 *                             .block(Blocks.AMETHYST_BLOCK)
 *                             .inlinePlace()
 *                             .onlyInBiome()
 *                             .count(16)
 *                             .squarePlacement()
 *                             .directHolder()
 * );</pre>
 */
public class ConditionFeatureConfig implements FeatureConfiguration {
    /**
     * Codec for {@link ConditionFeatureConfig}.
     * <p>
     * This codec is used to serialize and deserialize {@link ConditionFeatureConfig}s.
     */
    public static final Codec<ConditionFeatureConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    PlacementModifier.CODEC.fieldOf("filter").forGetter(p -> p.filter),
                    PlacedFeature.CODEC.fieldOf("filter_pass").forGetter(p -> p.okFeature),
                    PlacedFeature.CODEC.optionalFieldOf("filter_fail").forGetter(p -> p.failFeature)
            ).apply(instance, ConditionFeatureConfig::new)
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
     * Creates a new configuration that will only place a feature if the condition is met
     *
     * @param filter    The condition to check
     * @param okFeature The feature to place if the condition is met
     */
    public ConditionFeatureConfig(
            @NotNull PlacementFilter filter,
            @NotNull Holder<PlacedFeature> okFeature
    ) {
        this(filter, okFeature, Optional.empty());

    }

    /**
     * Creates a new configuration that will place a feature if the condition is met, and another
     * feature if the condition is not.
     *
     * @param filter      The condition to check
     * @param okFeature   The feature to place if the condition is met
     * @param failFeature The feature to place if the condition is not met
     */
    public ConditionFeatureConfig(
            @NotNull PlacementFilter filter,
            @NotNull Holder<PlacedFeature> okFeature,
            @NotNull Holder<PlacedFeature> failFeature
    ) {
        this(filter, okFeature, Optional.of(failFeature));
    }

    private ConditionFeatureConfig(
            @NotNull PlacementModifier filter,
            @NotNull Holder<PlacedFeature> okFeature,
            @NotNull Optional<Holder<PlacedFeature>> failFeature
    ) {
        this.filter = filter;
        this.okFeature = okFeature;
        this.failFeature = failFeature;
    }
}
