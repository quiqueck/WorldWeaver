package org.betterx.wover.feature.api.configured.configurators;

import org.betterx.wover.feature.api.placed.PlacedFeatureKey;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.RandomSelectorFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Places a random feature ({@link RandomSelectorFeature}).
 */
public interface AsRandomSelect extends FeatureConfigurator<RandomFeatureConfiguration, RandomSelectorFeature> {
    /**
     * Adds a feature to the random selection.
     *
     * @param feature The feature to add. A {@link PlacedFeatureKey} can be created using
     *                {@link org.betterx.wover.feature.api.placed.PlacedFeatureManager#createKey(ResourceLocation)}
     * @param weight  The weight of the feature
     * @return the same instance
     */
    AsRandomSelect add(PlacedFeatureKey feature, float weight);
    /**
     * Adds a feature to the random selection.
     *
     * @param feature The feature to add
     * @param weight  The weight of the feature
     * @return the same instance
     */
    AsRandomSelect add(Holder<PlacedFeature> feature, float weight);
    /**
     * Sets the default feature that should be placed if no other feature is selected.
     *
     * @param feature The default feature.
     * @return the same instance
     */
    AsRandomSelect defaultFeature(PlacedFeatureKey feature);
    /**
     * Sets the default feature that should be placed if no other feature is selected.
     *
     * @param feature The default feature.
     * @return the same instance
     */
    AsRandomSelect defaultFeature(Holder<PlacedFeature> feature);
}
