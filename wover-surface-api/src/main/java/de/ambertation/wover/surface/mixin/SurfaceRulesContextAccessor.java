package de.ambertation.wover.surface.mixin;

import de.ambertation.wover.surface.api.conditions.SurfaceRulesContext;

import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceRules;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Note that {@link SurfaceRulesContext#getBiome()} is deliberately <b>not</b> declared here as an
 * {@code @Accessor}. Up to 26.1 the {@code biome} field held a {@code Supplier<Holder<Biome>>} that did
 * the lazy lookup itself, so reading the field was enough. In 26.2 the field is a plain
 * {@code Holder<Biome>} that stays {@code null} until {@code Context#getBiome()} populates it from
 * {@code biomeGetter}, so a field accessor would hand out nulls. We therefore widen the real
 * {@code getBiome()} method with the access widener instead and let it satisfy the interface.
 * <p>
 * Mixin rejects the stale {@code @Accessor} outright - it fails at apply time with
 * "No candidates were found matching biome:Ljava/util/function/Supplier;", which nothing catches
 * at compile time.
 */
@Mixin(SurfaceRules.Context.class)
public interface SurfaceRulesContextAccessor extends SurfaceRulesContext {
    @Accessor("blockX")
    int getBlockX();
    @Accessor("blockY")
    int getBlockY();
    @Accessor("blockZ")
    int getBlockZ();
    @Accessor("surfaceDepth")
    int getSurfaceDepth();
    @Accessor("chunk")
    ChunkAccess getChunk();
    @Accessor("noiseChunk")
    NoiseChunk getNoiseChunk();
    @Accessor("stoneDepthAbove")
    int getStoneDepthAbove();
    @Accessor("stoneDepthBelow")
    int getStoneDepthBelow();
    @Accessor("lastUpdateY")
    long getLastUpdateY();
    @Accessor("lastUpdateXZ")
    long getLastUpdateXZ();
    @Accessor("randomState")
    RandomState getRandomState();

}
