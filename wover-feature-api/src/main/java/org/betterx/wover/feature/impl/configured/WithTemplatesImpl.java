package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.feature.api.Features;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.configurators.WithTemplates;
import org.betterx.wover.feature.api.features.TemplateFeature;
import org.betterx.wover.feature.api.features.config.TemplateFeatureConfig;
import org.betterx.wover.feature.impl.features.FeatureTemplateImpl;
import org.betterx.wover.util.RandomizedWeightedList;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WithTemplatesImpl extends FeatureConfiguratorImpl<TemplateFeatureConfig, TemplateFeature<TemplateFeatureConfig>> implements org.betterx.wover.feature.api.configured.configurators.WithTemplates {
    private final RandomizedWeightedList<TemplateFeatureConfig.FeatureTemplate> templates = new RandomizedWeightedList<>();

    WithTemplatesImpl(
            @Nullable BootstapContext<ConfiguredFeature<?, ?>> ctx,
            @Nullable ResourceKey<ConfiguredFeature<?, ?>> key
    ) {
        super(ctx, key);
    }

    @Override
    public WithTemplates add(
            ResourceLocation location
    ) {
        return add(location, 0, 1.0f);
    }

    @Override
    public WithTemplates add(
            ResourceLocation location,
            float weight
    ) {
        return add(location, 0, weight);
    }

    @Override
    public WithTemplates add(
            ResourceLocation location,
            int offsetY,
            float weight
    ) {
        templates.add(FeatureTemplateImpl.createTemplate(location, offsetY), weight);
        return this;
    }

    @Override
    public @NotNull TemplateFeatureConfig createConfiguration() {
        if (templates.isEmpty())
            throwStateError("Template Feature Config must have at least one template!");
        return TemplateFeatureConfig.of(templates);
    }

    @Override
    protected @NotNull TemplateFeature<TemplateFeatureConfig> getFeature() {
        return (TemplateFeature<TemplateFeatureConfig>) Features.TEMPLATE;
    }

    public static class Key extends ConfiguredFeatureKey<WithTemplates> {
        public Key(ResourceLocation id) {
            super(id);
        }

        @Override
        public WithTemplates bootstrap(@NotNull BootstapContext<ConfiguredFeature<?, ?>> ctx) {
            return new WithTemplatesImpl(ctx, key);
        }
    }
}
