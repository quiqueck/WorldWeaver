package de.ambertation.wover.feature.impl.configured;

import de.ambertation.wover.feature.api.Features;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureKey;
import de.ambertation.wover.feature.api.configured.configurators.WithTemplates;
import de.ambertation.wover.feature.api.features.TemplateFeature;
import de.ambertation.wover.feature.api.features.config.TemplateFeatureConfig;
import de.ambertation.wover.feature.impl.features.FeatureTemplateImpl;
import de.ambertation.wover.util.RandomizedWeightedList;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WithTemplatesImpl extends FeatureConfiguratorImpl<TemplateFeatureConfig, TemplateFeature<TemplateFeatureConfig>> implements de.ambertation.wover.feature.api.configured.configurators.WithTemplates {
    private final RandomizedWeightedList<TemplateFeatureConfig.FeatureTemplate> templates = new RandomizedWeightedList<>();

    WithTemplatesImpl(
            @Nullable BootstrapContext<ConfiguredFeature<?, ?>> ctx,
            @Nullable ResourceKey<ConfiguredFeature<?, ?>> key
    ) {
        super(ctx, key);
    }

    @Override
    public WithTemplates add(
            Identifier location
    ) {
        return add(location, 0, 1.0f);
    }

    @Override
    public WithTemplates add(
            Identifier location,
            float weight
    ) {
        return add(location, 0, weight);
    }

    @Override
    public WithTemplates add(
            Identifier location,
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
        public Key(Identifier id) {
            super(id);
        }

        @Override
        public WithTemplates bootstrap(@NotNull BootstrapContext<ConfiguredFeature<?, ?>> ctx) {
            return new WithTemplatesImpl(ctx, key);
        }
    }
}
