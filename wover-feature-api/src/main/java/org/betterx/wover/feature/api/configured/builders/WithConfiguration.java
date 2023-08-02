package org.betterx.wover.feature.api.configured.builders;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public interface WithConfiguration<F extends Feature<FC>, FC extends FeatureConfiguration> extends FeatureConfigurator<FC, F> {
    WithConfiguration<F, FC> feature(F feature);
    WithConfiguration<F, FC> configuration(FC config);
}
