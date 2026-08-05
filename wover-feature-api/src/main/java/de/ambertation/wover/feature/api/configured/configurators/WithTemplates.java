package de.ambertation.wover.feature.api.configured.configurators;

import de.ambertation.wover.feature.api.features.TemplateFeature;
import de.ambertation.wover.feature.api.features.config.TemplateFeatureConfig;

import net.minecraft.resources.Identifier;

/**
 * Places a random structure template ({@link TemplateFeature}).
 * <p>
 * The Configurator accepts  {@link Identifier}s of the templates to place. Templates
 * are chosen randomly bases on their weight. The weight is relative to the sum of all weights.
 * Templates are loaded from datapacks at {@code data/<namespace>/structure/<path>.nbt}.
 */
public interface WithTemplates extends FeatureConfigurator<TemplateFeatureConfig, TemplateFeature<TemplateFeatureConfig>> {
    /**
     * Adds a template to the random selection with a weight of 1.
     * <p>
     * This is a convenience method for {@link #add(Identifier, float)}.
     *
     * @param location The location of the template to add
     * @return the same instance
     */
    WithTemplates add(
            Identifier location
    );

    /**
     * Adds a template to the random selection with given weight.
     *
     * @param location The location of the template to add
     * @param weight   The weight of the template
     * @return the same instance
     */
    WithTemplates add(
            Identifier location,
            float weight
    );

    /**
     * Adds a template to the random selection with given weight and offset.
     * <p>
     * When a structure is loaded, the bounding box will get calculated.
     * The bottom center of the bounding box will be placed on the select
     * feature location. The offset is added to the selected y coordinate.
     *
     * @param location The location of the template to add
     * @param offsetY  The placement offset in y direction.
     * @param weight   The weight of the template
     * @return the same instance
     * @see TemplateFeatureConfig.FeatureTemplate#getOffsetY()
     */
    WithTemplates add(
            Identifier location,
            int offsetY,
            float weight
    );
}
