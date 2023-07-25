package org.betterx.wover.feature.api.configured.builders;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.RandomPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public interface RandomPatch extends FeatureConfigurator<RandomPatchConfiguration, RandomPatchFeature> {
    RandomPatch likeDefaultNetherVegetation();
    RandomPatch likeDefaultNetherVegetation(int xzSpread, int ySpread);
    RandomPatch tries(int v);
    RandomPatch spreadXZ(int v);
    RandomPatch spreadY(int v);
    RandomPatch featureToPlace(Holder<PlacedFeature> featureToPlace);
}
