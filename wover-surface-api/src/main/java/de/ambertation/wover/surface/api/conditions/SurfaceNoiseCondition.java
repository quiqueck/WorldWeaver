package de.ambertation.wover.surface.api.conditions;

import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.condition.ConditionEvaluator;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;

/**
 * A {@link MaterialCondition} that
 * is evaluates a custom noise function for a <b>2D Location</b>.
 */
public abstract class SurfaceNoiseCondition implements NoiseCondition {
    /**
     * Calls the {@link #test(SurfaceRulesContext)} method
     * with the correct context type for a 2D (X/Z) location.
     * <p>
     * Before Minecraft 26.3 this was {@code apply(SurfaceRules.Context)}, returning a
     * {@code SurfaceRules.Condition}. 26.3 replaced the surface rule system with the material rule
     * system, where a condition is compiled once per chunk into a {@link ConditionEvaluator}.
     *
     * @param context2 the evaluation context
     * @return A {@link ConditionEvaluator} that evaluates the noise function
     */
    @Override
    public final ConditionEvaluator compile(MaterialRuleContext context2) {
        final SurfaceNoiseCondition self = this;

        class Generator extends MaterialRuleContext.LazyXZCondition {
            Generator() {
                super(context2);
            }

            @Override
            protected boolean compute() {
                final SurfaceRulesContext context = SurfaceRulesContext.class.cast(this.context);
                if (context == null) return false;
                return self.test(context);
            }
        }

        return new Generator();
    }
}
