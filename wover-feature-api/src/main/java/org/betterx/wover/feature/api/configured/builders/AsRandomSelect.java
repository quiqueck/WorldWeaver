package org.betterx.wover.feature.api.configured.builders;

import org.betterx.wover.feature.api.placed.PlacedFeatureKey;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.RandomSelectorFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public interface AsRandomSelect extends FeatureConfigurator<RandomFeatureConfiguration, RandomSelectorFeature> {
    AsRandomSelect add(PlacedFeatureKey feature, float weight);
    AsRandomSelect add(Holder<PlacedFeature> feature, float weight);
    AsRandomSelect defaultFeature(PlacedFeatureKey feature);
    AsRandomSelect defaultFeature(Holder<PlacedFeature> feature);
}
