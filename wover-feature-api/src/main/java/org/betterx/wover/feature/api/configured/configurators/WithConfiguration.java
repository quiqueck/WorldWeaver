package org.betterx.wover.feature.api.configured.configurators;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * Configurator that allows to set a {@link Feature} and a {@link FeatureConfiguration}.
 * <p>
 * This is used for custom Configurators that are not part of the API. If you have special
 * needs, you can use this Configurator to specify a custom Feature and Configuration.
 *
 * @param <F>  The Feature
 * @param <FC> The FeatureConfiguration
 */
public interface WithConfiguration<F extends Feature<FC>, FC extends FeatureConfiguration> extends FeatureConfigurator<FC, F> {
    /**
     * Sets the {@link Feature}
     *
     * @param feature The feature
     * @return the same instance
     */
    WithConfiguration<F, FC> feature(F feature);
    /**
     * Sets the {@link FeatureConfiguration}. If you do not specify a configuration,
     * an empty configuration
     * ({@link net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration#NONE})
     * will be used.
     *
     * @param config The configuration
     * @return the same instance
     */
    WithConfiguration<F, FC> configuration(FC config);
}
