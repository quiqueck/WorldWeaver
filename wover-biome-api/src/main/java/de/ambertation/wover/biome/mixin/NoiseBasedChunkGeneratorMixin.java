package de.ambertation.wover.biome.mixin;

import de.ambertation.wover.biome.impl.modification.ChunkGeneratorHelper;
import de.ambertation.wover.common.generator.api.chunkgenerator.RebuildableFeaturesPerStep;

import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class NoiseBasedChunkGeneratorMixin implements RebuildableFeaturesPerStep<NoiseBasedChunkGenerator> {
    // This is needed by BiomeModificationImpl to synchronize the state of available features between the biomes and the
    // chunk generator. If this is not executed, added features that were previously not available in any biome known
    // to the chunk generator will result in an Index -1 out of bounds exception in applyBiomeDecoration
    @Override
    public void wover_rebuildFeaturesPerStep() {
        final ChunkGenerator g = (ChunkGenerator) (Object) this;
        ChunkGeneratorHelper.rebuildFeaturesPerStep(g, g.getBiomeSource());
    }
}
