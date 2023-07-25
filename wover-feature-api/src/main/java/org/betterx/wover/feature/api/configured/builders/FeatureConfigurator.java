package org.betterx.wover.feature.api.configured.builders;

import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.feature.api.placed.FeaturePlacementBuilder;
import org.betterx.wover.feature.impl.configured.FeatureConfiguratorImpl;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import org.jetbrains.annotations.NotNull;

public interface FeatureConfigurator<FC extends FeatureConfiguration, F extends Feature<FC>> {
    Event<OnBootstrapRegistry<ConfiguredFeature<?, ?>>> BOOTSTRAP_CONFIGURED_FEATURES =
            FeatureConfiguratorImpl.BOOTSTRAP_CONFIGURED_FEATURES;
    Holder<ConfiguredFeature<?, ?>> register(@NotNull BootstapContext<ConfiguredFeature<?, ?>> ctx);
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
