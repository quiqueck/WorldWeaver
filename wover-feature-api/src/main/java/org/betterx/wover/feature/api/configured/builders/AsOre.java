package org.betterx.wover.feature.api.configured.builders;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.OreFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

public interface AsOre extends FeatureConfigurator<OreConfiguration, OreFeature> {
    AsOre add(Block containedIn, Block ore);
    AsOre add(Block containedIn, BlockState ore);
    AsOre add(RuleTest containedIn, Block ore);
    AsOre add(RuleTest containedIn, BlockState ore);
    AsOre veinSize(int size);
    AsOre discardChanceOnAirExposure(float chance);
}
