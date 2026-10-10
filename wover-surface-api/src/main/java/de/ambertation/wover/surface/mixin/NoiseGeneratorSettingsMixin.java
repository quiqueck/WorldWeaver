package de.ambertation.wover.surface.mixin;

import de.ambertation.wover.common.surface.api.SurfaceRuleProvider;
import de.ambertation.wover.entrypoint.LibWoverSurface;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NoiseGeneratorSettings.class)
public abstract class NoiseGeneratorSettingsMixin implements SurfaceRuleProvider {
    /**
     * 26.3 renamed this record component from {@code surfaceRule} to {@code materialRule} and changed
     * its type from a bare {@link MaterialRule} to a {@link Holder} of one, because the rule
     * sources themselves now live in the {@code minecraft:worldgen/material_rule} datapack registry and
     * a noise settings file references them by id.
     */
    @Mutable
    @Final
    @Shadow
    private Holder<MaterialRule> materialRule;

    public void wover_overwriteSurfaceRules(MaterialRule surfaceRule) {
        if (surfaceRule == null || surfaceRule == this.materialRule.value()) return;
        if (this.wover_containsOverride) {
            LibWoverSurface.C.LOG.warn("Overwriting an overwritten set of Surface Rules.");
        }
        this.wover_containsOverride = true;
        // The merged rule source we install is built at world-load time from the surface rules of every
        // loaded datapack, so it has no identity in the material_rule registry and cannot be referenced
        // by id. A direct holder is the correct representation for it: it behaves like the registered
        // rule for every reader, and it serializes inline rather than as a dangling id if the settings
        // are ever written back out.
        this.materialRule = Holder.direct(surfaceRule);
    }

    public MaterialRule wover_getOriginalSurfaceRules() {
        return this.materialRule.value();
    }

    private boolean wover_containsOverride = false;
}
