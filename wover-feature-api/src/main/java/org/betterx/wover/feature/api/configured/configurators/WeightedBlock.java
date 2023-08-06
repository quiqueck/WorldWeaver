package org.betterx.wover.feature.api.configured.configurators;

import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;


/**
 * Places a random block ({@link SimpleBlockFeature}).
 */
public interface WeightedBlock extends BaseWeightedBlock<SimpleBlockConfiguration, SimpleBlockFeature, WeightedBlock> {
}
