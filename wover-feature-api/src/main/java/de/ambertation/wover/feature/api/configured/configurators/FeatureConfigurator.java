package de.ambertation.wover.feature.api.configured.configurators;

import de.ambertation.wover.feature.api.placed.FeaturePlacementBuilder;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Base Interface for a Configurator. Configurators are used to build a {@link Feature}.
 * <p>
 * 26.3 deleted {@code ConfiguredFeature<FC, F>} and folded it into {@link Feature}, which is now a plain
 * non-generic interface carrying its own configuration. The {@code <FC>}/{@code <F>} type parameters this
 * interface used to declare no longer describe anything, so they were removed rather than kept as dead
 * ceremony.
 */
public interface FeatureConfigurator {
    /**
     * Registers the {@link Feature} with the currently active
     * {@link net.minecraft.data.worldgen.BootstrapContext}.
     * <p>
     * Will fail if either the key of this Feature or the {@link net.minecraft.data.worldgen.BootstrapContext}
     * are null.
     *
     * @return the holder
     */
    Holder<Feature> register();

    /**
     * Creates a new unnamed {@link FeaturePlacementBuilder}. Such a builder can not be registered. An inline
     * builder is mostly used in combination with {@link FeaturePlacementBuilder#inRandomPatch()}, as this way
     * the feature as well as the placement are inlined into the definition of the random patch.
     *
     * @return the builder
     */
    FeaturePlacementBuilder inlinePlace();

    /**
     * Creates an unnamed {@link Holder} for this {@link FeatureConfigurator}.
     * <p>
     * This method is usefull, if you want to create an anonymous {@link Feature}
     * that is directly inlined into the definition of a
     * {@link net.minecraft.world.level.levelgen.placement.PlacedFeature} (instead of referenced by ID)
     *
     * @return the holder
     */
    Holder<Feature> directHolder();
}
