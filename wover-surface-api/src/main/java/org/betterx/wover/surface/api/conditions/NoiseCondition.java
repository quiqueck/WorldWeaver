package org.betterx.wover.surface.api.conditions;

import net.minecraft.world.level.levelgen.SurfaceRules;

/**
 * A {@link net.minecraft.world.level.levelgen.SurfaceRules.ConditionSource} that
 * is based on a custom noise function.
 */
public interface NoiseCondition extends SurfaceRules.ConditionSource {
    /**
     * Tests the condition when evaluation a {@link net.minecraft.world.level.levelgen.SurfaceRules.RuleSource}
     *
     * @param context the evaluation context
     * @return whether the condition is true
     */
    boolean test(SurfaceRulesContext context);
}
