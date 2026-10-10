package de.ambertation.wover.surface.api.conditions;

import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;
import net.minecraft.world.level.levelgen.material.rule.MaterialRule;

/**
 * A {@link MaterialCondition} that
 * is based on a custom noise function.
 */
public interface NoiseCondition extends MaterialCondition {
    /**
     * Tests the condition when evaluation a {@link MaterialRule}
     *
     * @param context the evaluation context
     * @return whether the condition is true
     */
    boolean test(SurfaceRulesContext context);
}
