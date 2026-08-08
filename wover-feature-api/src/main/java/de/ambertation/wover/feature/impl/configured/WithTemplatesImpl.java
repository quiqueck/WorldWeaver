package de.ambertation.wover.feature.impl.configured;

import de.ambertation.wover.feature.api.configured.FeatureKey;
import de.ambertation.wover.feature.api.configured.configurators.WithTemplates;
import de.ambertation.wover.feature.api.features.TemplateFeature;
import de.ambertation.wover.feature.impl.features.FeatureTemplateImpl;
import de.ambertation.wover.util.RandomizedWeightedList;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WithTemplatesImpl extends FeatureConfiguratorImpl implements de.ambertation.wover.feature.api.configured.configurators.WithTemplates {
    private final RandomizedWeightedList<TemplateFeature.FeatureTemplate> templates = new RandomizedWeightedList<>();

    WithTemplatesImpl(
            @Nullable BootstrapContext<Feature> ctx,
            @Nullable ResourceKey<Feature> key
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
    protected @NotNull Feature createFeature() {
        if (templates.isEmpty())
            throwStateError("Template Feature must have at least one template!");
        return TemplateFeature.of(templates);
    }

    public static class Key extends FeatureKey<WithTemplates> {
        public Key(Identifier id) {
            super(id);
        }

        @Override
        public WithTemplates bootstrap(@NotNull BootstrapContext<Feature> ctx) {
            return new WithTemplatesImpl(ctx, key);
        }
    }
}
