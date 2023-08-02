package org.betterx.wover.feature.api.configured.builders;

import org.betterx.wover.feature.api.features.SequenceFeature;
import org.betterx.wover.feature.api.features.config.SequenceFeatureConfig;
import org.betterx.wover.feature.api.placed.PlacedFeatureKey;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public interface AsSequence extends FeatureConfigurator<SequenceFeatureConfig, SequenceFeature> {
    AsSequence add(PlacedFeatureKey p);
    AsSequence add(Holder<PlacedFeature> p);
}
