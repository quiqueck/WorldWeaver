package de.ambertation.wover.feature.api.configured.configurators;

import de.ambertation.wover.feature.api.placed.BasePlacedFeatureKey;
import de.ambertation.wover.feature.api.placed.PlacedFeatureKey;
import de.ambertation.wover.feature.impl.random.RandomPatchConfiguration;
import de.ambertation.wover.feature.impl.random.RandomPatchFeature;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Places a given Feature multiple times in a randomized patch ({@link RandomPatchFeature}).
 * <p>
 * You can set a {@link PlacedFeature} that is randomly placed in the patch.
 * <p>
 * deprecated: Vanilla removed the {@code random_patch} configured-feature type in Minecraft 26.1.
 * WorldWeaver still ships a compatible {@code wover:random_patch} shim so existing data keeps working,
 * and this API remains functional for this release, but it is scheduled for removal. Use
 * {@link de.ambertation.wover.feature.api.placed.FeaturePlacementBuilder#scatter(int, int, int, net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate)
 * FeaturePlacementBuilder.scatter(tries, xzSpread, ySpread, filter)} (or
 * {@link de.ambertation.wover.feature.api.placed.FeaturePlacementBuilder#scatter(int, int, int)} without a
 * filter) on a {@code simple_block} placed feature instead. It expresses the same "scatter a feature N times
 * in an area" behaviour with <b>placement modifiers</b> — {@code CountPlacement.of(tries)} (former
 * {@code tries}) + {@code RandomOffsetPlacement.of(xz, y)} (former {@code xz_spread}/{@code y_spread}) + an
 * optional {@code BlockPredicateFilter} + {@code BiomeFilter} — rather than a configured {@code random_patch}.
 */
//TODO: @Deprecated(since = "26.1.0", forRemoval = true)
public interface RandomPatch extends FeatureConfigurator<RandomPatchConfiguration, RandomPatchFeature>, BasePatch<RandomPatchConfiguration, RandomPatchFeature, RandomPatch> {


    /**
     * The feature that should be placed in the patch.
     *
     * @param featureToPlace The feature to place. A {@link PlacedFeatureKey} can be created using
     *                       {@link de.ambertation.wover.feature.api.placed.PlacedFeatureManager#createKey(net.minecraft.resources.Identifier)}
     * @return the same instance
     */
    <K extends BasePlacedFeatureKey<K>> RandomPatch featureToPlace(BasePlacedFeatureKey<K> featureToPlace);

    /**
     * The feature that should be placed in the patch.
     *
     * @param featureToPlace The feature to place
     * @return the same instance
     */
    RandomPatch featureToPlace(Holder<PlacedFeature> featureToPlace);
}
