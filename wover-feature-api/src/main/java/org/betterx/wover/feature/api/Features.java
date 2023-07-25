package org.betterx.wover.feature.api;

import org.betterx.wover.feature.api.features.config.ConditionFeatureConfig;
import org.betterx.wover.feature.api.features.config.PillarFeatureConfig;
import org.betterx.wover.feature.api.features.config.PlaceFacingBlockConfig;
import org.betterx.wover.feature.api.features.config.SequenceFeatureConfig;
import org.betterx.wover.feature.impl.FeatureManagerImpl;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class Features {
    public static final Feature<PlaceFacingBlockConfig> PLACE_BLOCK = FeatureManagerImpl.PLACE_BLOCK;
    
    public static final Feature<NoneFeatureConfiguration> MARK_POSTPROCESSING = FeatureManagerImpl.MARK_POSTPROCESSING;

    public static final Feature<SequenceFeatureConfig> SEQUENCE = FeatureManagerImpl.SEQUENCE;

    public static final Feature<ConditionFeatureConfig> CONDITION = FeatureManagerImpl.CONDITION;

    public static final Feature<PillarFeatureConfig> PILLAR = FeatureManagerImpl.PILLAR;
}
