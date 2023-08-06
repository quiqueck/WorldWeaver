package org.betterx.wover.feature.api;

import org.betterx.wover.feature.api.features.config.*;
import org.betterx.wover.feature.impl.FeatureManagerImpl;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Contains all provided custom features.
 */
public class Features {
    public static final Feature<PlaceFacingBlockConfig> PLACE_BLOCK = FeatureManagerImpl.PLACE_BLOCK;

    /**
     * {@code wover:mark_postprocessing} - Places a mark for postprocessing on a chunk.
     *
     * @see org.betterx.wover.feature.api.features.MarkPostProcessingFeature
     */
    public static final Feature<NoneFeatureConfiguration> MARK_POSTPROCESSING = FeatureManagerImpl.MARK_POSTPROCESSING;

    public static final Feature<SequenceFeatureConfig> SEQUENCE = FeatureManagerImpl.SEQUENCE;

    /**
     * {@code wover:condition} - Places features based on a condition.
     *
     * @see org.betterx.wover.feature.api.features.ConditionFeature
     */
    public static final Feature<ConditionFeatureConfig> CONDITION = FeatureManagerImpl.CONDITION;

    /**
     * {@code wover:pillar} - Creates a pillar of blocks.
     *
     * @see org.betterx.wover.feature.api.features.PillarFeature
     */
    public static final Feature<PillarFeatureConfig> PILLAR = FeatureManagerImpl.PILLAR;

    public static final Feature<TemplateFeatureConfig> TEMPLATE = FeatureManagerImpl.TEMPLATE;
}
