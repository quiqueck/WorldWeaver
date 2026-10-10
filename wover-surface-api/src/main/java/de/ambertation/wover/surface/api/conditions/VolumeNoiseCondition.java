package de.ambertation.wover.surface.api.conditions;

import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.material.condition.ConditionEvaluator;
import net.minecraft.world.level.levelgen.material.condition.MaterialCondition;

/**
 * A {@link MaterialCondition} that
 * is evaluates a custom noise function for a <b>3D Location</b>.
 */
public abstract class VolumeNoiseCondition implements NoiseCondition {
    /**
     * Calls the {@link #test(SurfaceRulesContext)} method
     * with the correct context type for a 3D location.
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
        final VolumeNoiseCondition self = this;

        // LazyYCondition is invalidated on every Y step, and moving to a new column also counts as a
        // Y step (MaterialRuleContext#updateXZ bumps both counters), so it caches per block exactly
        // like the generic SurfaceRules.LazyCondition keyed on lastUpdateY + lastUpdateXZ did before.
        class Generator extends MaterialRuleContext.LazyYCondition {
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
