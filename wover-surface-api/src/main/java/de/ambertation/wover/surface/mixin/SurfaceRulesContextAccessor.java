package de.ambertation.wover.surface.mixin;

import de.ambertation.wover.surface.api.conditions.SurfaceRulesContext;

import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Backs {@link SurfaceRulesContext} with vanilla's {@link MaterialRuleContext}, which replaced
 * {@code SurfaceRules.Context} in Minecraft 26.3.
 * <p>
 * {@link SurfaceRulesContext#getBiome()} is deliberately <b>not</b> declared here as an
 * {@code @Accessor}: the {@code biome} field stays {@code null} until {@code MaterialRuleContext#getBiome()}
 * populates it from the biome getter, so a field accessor would hand out nulls. The public vanilla
 * {@code getBiome()} method has the exact signature of the interface method and satisfies it directly.
 */
@Mixin(MaterialRuleContext.class)
public interface SurfaceRulesContextAccessor extends SurfaceRulesContext {
    @Accessor("blockX")
    int getBlockX();
    @Accessor("blockY")
    int getBlockY();
    @Accessor("blockZ")
    int getBlockZ();
    @Accessor("surfaceDepth")
    int getSurfaceDepth();
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
