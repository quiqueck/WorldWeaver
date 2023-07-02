package org.betterx.wover.common.surface.api;

import net.minecraft.world.level.levelgen.SurfaceRules;

/**
 * This interface is used to provide a way to overwrite surface rules.
 * <p>
 * In the vanilla game this is used to overwrite the surface rules in the
 * {@link net.minecraft.world.level.levelgen.NoiseGeneratorSettings} class.
 * <p>
 * Any Modded class that provides surface rules, can also implement this interface
 * to enable support for WorldWeaver
 */
public interface SurfaceRuleProvider {
    void wover_overwriteSurfaceRules(SurfaceRules.RuleSource surfaceRule);
    SurfaceRules.RuleSource wover_getOriginalSurfaceRules();
}
