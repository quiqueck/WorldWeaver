package de.ambertation.wover.feature.api.configured.configurators;

import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Configurator that registers a {@link Feature} you built yourself.
 * <p>
 * This is used for custom Features that are not covered by one of the other configurators. If you have
 * special needs, you can use this Configurator to register any {@link Feature} instance under a key.
 * <p>
 * This replaces 26.1's {@code WithConfiguration<F, FC>}. 26.3 deleted {@code ConfiguredFeature} and
 * {@code FeatureConfiguration}: a {@link Feature} <em>is</em> its configuration now, so pairing a stateless
 * feature singleton with a separate configuration object is no longer a thing. Consequently the {@code <FC>}
 * type parameter and the {@code configuration(FC)} method are gone — there is nothing left for them to mean.
 *
 * @param <F> The Feature
 */
public interface WithFeature<F extends Feature> extends FeatureConfigurator {
    /**
     * Sets the {@link Feature}
     *
     * @param feature The feature
     * @return The same instance
     */
    WithFeature<F> feature(F feature);
}
