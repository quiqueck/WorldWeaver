package de.ambertation.wover.feature.api.configured.configurators;

import de.ambertation.wover.feature.api.features.SequenceFeature;
import de.ambertation.wover.feature.api.features.config.SequenceFeatureConfig;
import de.ambertation.wover.feature.api.placed.PlacedFeatureKey;

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
     *                   {@link de.ambertation.wover.feature.api.placed.PlacedFeatureManager#createKey(net.minecraft.resources.Identifier)}
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
