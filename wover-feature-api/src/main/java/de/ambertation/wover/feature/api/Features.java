package de.ambertation.wover.feature.api;

import de.ambertation.wover.feature.api.features.*;
import de.ambertation.wover.feature.impl.FeatureManagerImpl;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.levelgen.feature.Feature;

/**
 * Contains all provided custom feature types.
 * <p>
 * These used to be {@code Feature<FC>} singletons. 26.3 collapsed {@code ConfiguredFeature} into
 * {@link Feature}, so a {@link Feature} is a configured value now and the registered <em>type</em> is its
 * {@link MapCodec}. Referencing a constant here therefore guarantees the type is registered; to build a
 * feature you construct the corresponding class directly (or use
 * {@link de.ambertation.wover.feature.api.configured.FeatureContentManager}).
 */
public class Features {
    /**
     * {@code wover:place_block} - Places a block with a facing property
     *
     * @see PlaceFacingBlockFeature
     */
    public static final MapCodec<PlaceFacingBlockFeature> PLACE_BLOCK = FeatureManagerImpl.PLACE_BLOCK;

    /**
     * {@code wover:mark_postprocessing} - Places a mark for postprocessing on a chunk.
     *
     * @see MarkPostProcessingFeature
     */
    public static final MapCodec<MarkPostProcessingFeature> MARK_POSTPROCESSING = FeatureManagerImpl.MARK_POSTPROCESSING;

    /**
     * {@code wover:sequence} - Places multiple features.
     *
     * @see SequenceFeature
     */
    public static final MapCodec<SequenceFeature> SEQUENCE = FeatureManagerImpl.SEQUENCE;

    /**
     * {@code wover:condition} - Places features based on a condition.
     *
     * @see ConditionFeature
     */
    public static final MapCodec<ConditionFeature> CONDITION = FeatureManagerImpl.CONDITION;

    /**
     * {@code wover:pillar} - Creates a pillar of blocks.
     *
     * @see PillarFeature
     */
    public static final MapCodec<PillarFeature> PILLAR = FeatureManagerImpl.PILLAR;

    /**
     * {@code wover:template} - Places a template.
     *
     * @see TemplateFeature
     */
    public static final MapCodec<TemplateFeature> TEMPLATE = FeatureManagerImpl.TEMPLATE;

    private Features() {
    }
}
