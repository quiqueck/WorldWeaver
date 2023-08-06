package org.betterx.wover.feature.api.configured.configurators;

import org.betterx.wover.feature.api.features.SequenceFeature;
import org.betterx.wover.feature.api.features.config.SequenceFeatureConfig;
import org.betterx.wover.feature.api.placed.PlacedFeatureKey;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

/**
 * Places multiple features ({@link SequenceFeature}).
 */
public interface AsSequence extends FeatureConfigurator<SequenceFeatureConfig, SequenceFeature> {
    /**
     * Adds a feature to the sequence.
     *
     * @param featureKey The feature to add. A {@link PlacedFeatureKey} can be created using
     *                   {@link org.betterx.wover.feature.api.placed.PlacedFeatureManager#createKey(net.minecraft.resources.ResourceLocation)}
     * @return the same instance
     */
    AsSequence add(PlacedFeatureKey featureKey);
    /**
     * Adds a feature to the sequence.
     *
     * @param holder The feature to add
     * @return the same instance
     */
    AsSequence add(Holder<PlacedFeature> holder);
}
