package org.betterx.wover.feature.api.features.config;

import org.betterx.wover.feature.impl.features.FeatureTemplate;
import org.betterx.wover.util.RandomizedWeightedList;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class TemplateFeatureConfig implements FeatureConfiguration {
    public static final Codec<TemplateFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(
                    RandomizedWeightedList
                            .buildCodec(FeatureTemplate.CODEC)
                            .fieldOf("structures")
                            .forGetter((TemplateFeatureConfig cfg) -> cfg.structures)
            )
            .apply(instance, TemplateFeatureConfig::new)
    );

    public final RandomizedWeightedList<FeatureTemplate> structures;

    public static FeatureTemplate cfg(ResourceLocation location, int offsetY) {
        return FeatureTemplate.create(location, offsetY);
    }

    public static FeatureTemplate cfg(ResourceLocation location) {
        return FeatureTemplate.create(location);
    }

    public TemplateFeatureConfig(ResourceLocation location, int offsetY) {
        this(RandomizedWeightedList.of(cfg(location, offsetY)));
    }

    public TemplateFeatureConfig(RandomizedWeightedList<FeatureTemplate> structures) {
        this.structures = structures;
    }
}
