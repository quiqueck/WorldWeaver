package org.betterx.wover.feature.api.features;

import org.betterx.wover.block.api.BlockHelper;
import org.betterx.wover.feature.api.features.config.PillarFeatureConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

/**
 * Creates a pillar of blocks (<b>{@code wover:pillar}</b>).
 * <p>
 * The pillar is created by placing blocks in a line in a given direction.
 * Here is a simple Java example that creates a Pillar (please note that this Blocks
 * can not actually be used, as it does not implement the correct size property:
 * <pre class="java"> ConfiguredFeatureKey&lt;AsPillar> TEST_KEY;
 *
 * TEST_KEY = ConfiguredFeatureManager.pillar(
 *     modCore.id("test")
 * );
 *
 * TEST_KEY.bootstrap(context)
 *         .allowedPlacement(BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE)
 *         .maxHeight(UniformInt.of(4, 8))
 *         .minHeight(3)
 *         .direction(Direction.UP)
 *         .transformer(PillarFeatureConfig.KnownTransformers.SIZE_INCREASE)
 *         .blockState(Blocks.LAPIS_BLOCK)
 *         .register();
 *  </pre>
 * The same ConfiguredFeature can be created using JSON:
 * <pre class="json"> {
 *   "type": "wover:pillar",
 *   "config": {
 *     "allowed_placement": {
 *       "type": "minecraft:matching_blocks",
 *       "blocks": [
 *         "minecraft:air",
 *         "minecraft:water"
 *       ]
 *     },
 *     "direction": "up",
 *     "max_height": {
 *       "type": "minecraft:uniform",
 *       "value": {
 *         "max_inclusive": 8,
 *         "min_inclusive": 4
 *       }
 *     },
 *     "min_height": 3,
 *     "state": {
 *       "type": "minecraft:simple_state_provider",
 *       "state": {
 *         "Name": "minecraft:lapis_block"
 *       }
 *     },
 *     "transform": "size_increase"
 *   }
 * }</pre>
 *
 * @see PillarFeatureConfig
 * @see org.betterx.wover.feature.api.Features#PILLAR
 */
public class PillarFeature extends Feature<PillarFeatureConfig> {
    /**
     * Creates a new PillarFeature.
     */
    public PillarFeature() {
        super(PillarFeatureConfig.CODEC);
    }


    /**
     * Places a pillar of blocks.
     *
     * @param featurePlaceContext the context
     * @return {@code true} if the pillar was placed, {@code false} otherwise
     */
    @Override
    public boolean place(FeaturePlaceContext<PillarFeatureConfig> featurePlaceContext) {
        int height;
        final WorldGenLevel level = featurePlaceContext.level();
        final PillarFeatureConfig config = featurePlaceContext.config();
        final RandomSource rnd = featurePlaceContext.random();
        int maxHeight = config.maxHeight.sample(rnd);
        int minHeight = config.minHeight.sample(rnd);
        BlockPos.MutableBlockPos posnow = featurePlaceContext.origin().mutable();
        posnow.move(config.direction);

        for (height = 1; height < maxHeight; ++height) {
            if (!config.allowedPlacement.test(level, posnow)) {
                maxHeight = height;
                break;
            }
            posnow.move(config.direction);
        }
        if (maxHeight < minHeight) return false;

        if (!config.transformer.canPlace.at(
                minHeight,
                maxHeight,
                featurePlaceContext.origin(),
                posnow,
                level,
                config.allowedPlacement,
                rnd
        )) {
            return false;
        }
        posnow = featurePlaceContext.origin().mutable();
        for (height = 0; height < maxHeight; ++height) {
            BlockState state = config.transform(height, maxHeight - 1, posnow, rnd);
            level.setBlock(posnow, state, BlockHelper.SET_SILENT);
            posnow.move(config.direction);
        }

        return true;
    }
}
