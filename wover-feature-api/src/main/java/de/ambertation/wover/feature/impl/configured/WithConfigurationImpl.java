package de.ambertation.wover.feature.impl.configured;

import de.ambertation.wover.feature.api.configured.ConfiguredFeatureKey;
import de.ambertation.wover.feature.api.configured.configurators.WithConfiguration;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WithConfigurationImpl<F extends Feature<FC>, FC extends FeatureConfiguration> extends FeatureConfiguratorImpl<FC, F> implements de.ambertation.wover.feature.api.configured.configurators.WithConfiguration<F, FC> {
    private FC configuration;
    private F feature;

    WithConfigurationImpl(
            @Nullable BootstrapContext<ConfiguredFeature<?, ?>> ctx,
            @Nullable ResourceKey<ConfiguredFeature<?, ?>> key
    ) {
        super(ctx, key);
    }


    @Override
    public WithConfiguration<F, FC> feature(F feature) {
        this.feature = feature;
        return this;
    }

    @Override
    public WithConfiguration<F, FC> configuration(FC config) {
        this.configuration = config;
        return this;
    }


    @Override
    public @NotNull FC createConfiguration() {
        if (feature == null) {
            throwStateError("Feature must be set before creating configuration");
        }
        if (configuration == null) {
            //Moonlight Lib seems to trigger a load of our data before
            //NoneFeatureConfiguration.NONE is initialized. This Code
            // is meant to prevent that...
            if (NoneFeatureConfiguration.NONE != null)
                return (FC) NoneFeatureConfiguration.NONE;

            return (FC) NoneFeatureConfiguration.INSTANCE;
        }
        return configuration;
    }

    @Override
    protected @NotNull F getFeature() {
        return feature;
    }

    public static class Key<F extends Feature<FC>, FC extends FeatureConfiguration> extends ConfiguredFeatureKey<WithConfiguration<F, FC>> {
        private final F feature;

        public Key(Identifier id, F feature) {
            super(id);
            this.feature = feature;
        }

        @Override
        public WithConfiguration<F, FC> bootstrap(@NotNull BootstrapContext<ConfiguredFeature<?, ?>> ctx) {
            return new WithConfigurationImpl<F, FC>(ctx, key).feature(feature);
        }
    }
}
