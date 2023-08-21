package org.betterx.wover.common.generator.api.biomesource;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public interface NoiseGeneratorSettingsProvider {
    NoiseGeneratorSettings wover_getNoiseGeneratorSettings();
    Holder<NoiseGeneratorSettings> wover_getNoiseGeneratorSettingHolders();
}