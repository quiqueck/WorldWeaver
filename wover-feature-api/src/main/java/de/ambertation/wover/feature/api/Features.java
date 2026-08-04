package de.ambertation.wover.feature.api;

import de.ambertation.wover.feature.api.features.config.*;
import de.ambertation.wover.feature.impl.FeatureManagerImpl;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Contains all provided custom features.
 */
public class Features {
    /**
     * {@code wover:place_block} - Places a block with a facing property
     */
    public static final Feature<PlaceFacingBlockConfig> PLACE_BLOCK = FeatureManagerImpl.PLACE_BLOCK;

    /**
     * {@code wover:mark_postprocessing} - Places a mark for postprocessing on a chunk.
     *
     * @see de.ambertation.wover.feature.api.features.MarkPostProcessingFeature
     */
    public static final Feature<NoneFeatureConfiguration> MARK_POSTPROCESSING = FeatureManagerImpl.MARK_POSTPROCESSING;

    /**
     * {@code wover:sequence} - Places multiple features.
     *
     * @see de.ambertation.wover.feature.api.features.SequenceFeature
     */
    public static final Feature<SequenceFeatureConfig> SEQUENCE = FeatureManagerImpl.SEQUENCE;

    /**
     * {@code wover:condition} - Places features based on a condition.
     *
     * @see de.ambertation.wover.feature.api.features.ConditionFeature
     */
    public static final Feature<ConditionFeatureConfig> CONDITION = FeatureManagerImpl.CONDITION;

    /**
     * {@code wover:pillar} - Creates a pillar of blocks.
     *
     * @see de.ambertation.wover.feature.api.features.PillarFeature
     */
    public static final Feature<PillarFeatureConfig> PILLAR = FeatureManagerImpl.PILLAR;

    /**
     * {@code wover:template} - Places a template.
     *
     * @see de.ambertation.wover.feature.api.features.TemplateFeature
     */
    public static final Feature<TemplateFeatureConfig> TEMPLATE = FeatureManagerImpl.TEMPLATE;

    private Features() {
    }
}
