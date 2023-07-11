package org.betterx.wover.surface.api.conditions;

import net.minecraft.world.level.levelgen.SurfaceRules.Condition;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import net.minecraft.world.level.levelgen.SurfaceRules.LazyCondition;

/**
 * A {@link net.minecraft.world.level.levelgen.SurfaceRules.ConditionSource} that
 * is evaluates a custom noise function for a <b>3D Location</b>.
 */
public abstract class VolumeNoiseCondition implements NoiseCondition {
    /**
     * Calls the {@link #test(SurfaceRulesContext)} method
     * with the correct context type for a 3D location.
     *
     * @param context2 the evaluation context
     * @return A {@link Condition} that evaluates the noise function
     */
    @Override
    public final Condition apply(Context context2) {
        final VolumeNoiseCondition self = this;

        class Generator extends LazyCondition {
            Generator() {
                super(context2);
            }

            @Override
            protected long getContextLastUpdate() {
                final SurfaceRulesContext ctx = SurfaceRulesContext.class.cast(this.context);
                return ctx.getLastUpdateY() + ctx.getLastUpdateXZ();
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
