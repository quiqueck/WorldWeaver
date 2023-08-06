package org.betterx.wover.feature.api.features;

import org.betterx.wover.feature.api.features.config.SequenceFeatureConfig;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

/**
 * Places multiple features  (<b>{@code wover:sequence}</b>).
 *
 * @see org.betterx.wover.feature.api.Features#SEQUENCE
 * @see SequenceFeatureConfig
 */
public class SequenceFeature extends Feature<SequenceFeatureConfig> {
    /**
     * Creates a new Instance.
     */
    public SequenceFeature() {
        super(SequenceFeatureConfig.CODEC);
    }

    /**
     * Try to place all features.
     *
     * @param featurePlaceContext The context
     * @return {@code true} if at least one feature was placed
     */
    @Override
    public boolean place(FeaturePlaceContext<SequenceFeatureConfig> featurePlaceContext) {
        return featurePlaceContext.config().placeAll(featurePlaceContext);
    }
}
