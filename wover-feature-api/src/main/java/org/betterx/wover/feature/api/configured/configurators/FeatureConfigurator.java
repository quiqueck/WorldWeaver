package org.betterx.wover.feature.api.configured.configurators;

import org.betterx.wover.feature.api.placed.FeaturePlacementBuilder;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * Base Interface for a Configurator. Configurators are used to build a {@link ConfiguredFeature}.
 *
 * @param <FC> The type of the {@link FeatureConfiguration}
 * @param <F>  The Type of the {@link Feature}
 */
public interface FeatureConfigurator<FC extends FeatureConfiguration, F extends Feature<FC>> {
    /**
     * Registers the {@link ConfiguredFeature} with the currently active
     * {@link net.minecraft.data.worldgen.BootstapContext}.
     * <p>
     * Will fail if either the key of this Feature or the {@link net.minecraft.data.worldgen.BootstapContext}
     * are null.
     *
     * @return the holder
     */
    Holder<ConfiguredFeature<?, ?>> register();

    /**
     * Creates a new unnamed {@link FeaturePlacementBuilder}. Such a builder can not be registered. An inline
     * builder is mostly used in combination with {@link FeaturePlacementBuilder#inRandomPatch()}, as this way
     * the configured feature as well as the placement are inlined into the definition of the random patch.
     *
     * @return the builder
     */
    FeaturePlacementBuilder inlinePlace();

    /**
     * Creates an unnamed {@link Holder} for this {@link FeatureConfigurator}.
     * <p>
     * This method is usefull, if you want to create an anonymous {@link ConfiguredFeature}
     * that is directly inlined into the definition of a
     * {@link net.minecraft.world.level.levelgen.placement.PlacedFeature} (instead of referenced by ID)
     *
     * @return the holder
     */
    Holder<ConfiguredFeature<?, ?>> directHolder();
}
