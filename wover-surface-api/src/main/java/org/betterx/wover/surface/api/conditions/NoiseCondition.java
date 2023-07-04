package org.betterx.wover.surface.api.conditions;

import org.betterx.wover.surface.mixin.SurfaceRulesContextAccessor;

import net.minecraft.world.level.levelgen.SurfaceRules;

public interface NoiseCondition extends SurfaceRules.ConditionSource {
    boolean test(SurfaceRulesContextAccessor context);
}
