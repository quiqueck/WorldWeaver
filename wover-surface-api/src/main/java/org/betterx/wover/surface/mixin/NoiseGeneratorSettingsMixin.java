package org.betterx.wover.surface.mixin;

import org.betterx.wover.common.surface.api.SurfaceRuleProvider;
import org.betterx.wover.entrypoint.LibWoverSurface;

import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NoiseGeneratorSettings.class)
public abstract class NoiseGeneratorSettingsMixin implements SurfaceRuleProvider {
    @Mutable
    @Final
    @Shadow
    private SurfaceRules.RuleSource surfaceRule;

    public void wover_overwriteSurfaceRules(SurfaceRules.RuleSource surfaceRule) {
        if (surfaceRule == null || surfaceRule == this.surfaceRule) return;
        if (this.wover_containsOverride) {
            LibWoverSurface.C.LOG.warn("Overwriting an overwritten set of Surface Rules.");
        }
        this.wover_containsOverride = true;
        this.surfaceRule = surfaceRule;
    }

    public SurfaceRules.RuleSource wover_getOriginalSurfaceRules() {
        return this.surfaceRule;
    }

    private boolean wover_containsOverride = false;
}
