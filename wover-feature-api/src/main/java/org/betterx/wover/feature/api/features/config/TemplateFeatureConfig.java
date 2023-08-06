package org.betterx.wover.feature.api.features.config;

import org.betterx.wover.feature.impl.features.FeatureTemplate;
import org.betterx.wover.util.RandomizedWeightedList;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * Config for a {@link org.betterx.wover.feature.api.features.TemplateFeature}. The Configuration
 * holds a weighted list of {@link FeatureTemplate}s that will be placed at random.
 * <p>
 * Structures are identified by {@link ResourceLocation}s. The structure
 * is loaded from a datapack at {@code data/<namespace>/structures/<path>.nbt}.
 */
public class TemplateFeatureConfig implements FeatureConfiguration {
    /**
     * Codec for {@link TemplateFeatureConfig}.
     */
    public static final Codec<TemplateFeatureConfig> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(
                    RandomizedWeightedList
                            .buildCodec(FeatureTemplate.CODEC)
                            .fieldOf("structures")
                            .forGetter((TemplateFeatureConfig cfg) -> cfg.structures)
            )
            .apply(instance, TemplateFeatureConfig::of)
    );

    private final RandomizedWeightedList<FeatureTemplate> structures;

    /**
     * Creates a new {@link FeatureTemplate} from the given structure with the
     * given offset.
     *
     * @param location The location of the structure.
     * @param offsetY  The offset in y direction used to place the structure
     * @return A new {@link FeatureTemplate}.
     */
    public static FeatureTemplate createTemplate(ResourceLocation location, int offsetY) {
        return FeatureTemplate.create(location, offsetY);
    }

    /**
     * Creates a new {@link FeatureTemplate} from the given structure
     *
     * @param location The location of the structure.
     * @return A new {@link FeatureTemplate}.
     */
    public static FeatureTemplate createTemplate(ResourceLocation location) {
        return FeatureTemplate.create(location);
    }

    /**
     * Creates a new {@link TemplateFeatureConfig} from the given structures
     *
     * @param structures The weighted list of structures.
     * @return A new {@link TemplateFeatureConfig}.
     */
    public static TemplateFeatureConfig of(RandomizedWeightedList<FeatureTemplate> structures) {
        return new TemplateFeatureConfig(structures);
    }


    private TemplateFeatureConfig(RandomizedWeightedList<FeatureTemplate> structures) {
        this.structures = structures;
    }

    /**
     * Returns a random structure from the weighted list.
     *
     * @param random The random source to use.
     * @return A random structure.
     */
    public FeatureTemplate randomStructure(RandomSource random) {
        return structures.getRandomValue(random);
    }
}
