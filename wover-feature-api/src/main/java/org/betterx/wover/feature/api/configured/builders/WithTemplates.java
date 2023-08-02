package org.betterx.wover.feature.api.configured.builders;

import org.betterx.wover.feature.api.features.TemplateFeature;
import org.betterx.wover.feature.api.features.config.TemplateFeatureConfig;

import net.minecraft.resources.ResourceLocation;

public interface WithTemplates extends FeatureConfigurator<TemplateFeatureConfig, TemplateFeature<TemplateFeatureConfig>> {
    WithTemplates add(
            ResourceLocation location
    );
    WithTemplates add(
            ResourceLocation location,
            float chance
    );
    WithTemplates add(
            ResourceLocation location,
            int offsetY,
            float chance
    );
}
