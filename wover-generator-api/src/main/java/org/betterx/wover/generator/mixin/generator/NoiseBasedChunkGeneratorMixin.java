package org.betterx.wover.generator.mixin.generator;


import org.betterx.wover.common.generator.api.biomesource.NoiseGeneratorSettingsProvider;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class NoiseBasedChunkGeneratorMixin implements NoiseGeneratorSettingsProvider {
    @Final
    @Shadow
    protected Holder<NoiseGeneratorSettings> settings;


    @Override
    public NoiseGeneratorSettings wover_getNoiseGeneratorSettings() {
        return settings.value();
    }

    @Override
    public Holder<NoiseGeneratorSettings> wover_getNoiseGeneratorSettingHolders() {
        return settings;
    }
}
