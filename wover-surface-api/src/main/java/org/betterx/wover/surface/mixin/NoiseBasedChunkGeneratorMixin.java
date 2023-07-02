package org.betterx.wover.surface.mixin;

import org.betterx.wover.common.surface.api.InjectableSurfaceRules;
import org.betterx.wover.surface.impl.SurfaceRuleUtil;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class NoiseBasedChunkGeneratorMixin implements InjectableSurfaceRules<NoiseBasedChunkGenerator> {
    @Shadow
    public abstract Holder<NoiseGeneratorSettings> generatorSettings();

    public void wover_injectSurfaceRules(Registry<LevelStem> dimensionRegistry, ResourceKey<LevelStem> dimensionKey) {
        ChunkGenerator self = (ChunkGenerator) (Object) this;

        SurfaceRuleUtil.injectNoiseBasedSurfaceRules(
                dimensionKey,
                generatorSettings(),
                self.getBiomeSource()
        );
    }
}
