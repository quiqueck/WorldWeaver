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
    /**
     * This method is used to overwrite the surface rules. Before this method is called,
     * the original surface rules are loaded using {@link #wover_getOriginalSurfaceRules()}
     * and merging them with the Surface Rules from the Registry. Therefore the passed
     * surface rules should replace the original ones.
     *
     * @param surfaceRule The new surface rules.
     */
    void wover_overwriteSurfaceRules(SurfaceRules.RuleSource surfaceRule);
    /**
     * This method is used to get the original, unmodified surface rules.
     *
     * @return The original surface rules
     */
    SurfaceRules.RuleSource wover_getOriginalSurfaceRules();
}
