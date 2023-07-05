package org.betterx.wover.surface.api;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.SurfaceRules;

import org.jetbrains.annotations.ApiStatus;

/**
 * Represents a surface rule assigned to a biome.
 * <p>
 * Instances of this class should never get created direct, they are built when a
 * SurfaceRule is added to the registry using
 * {@link SurfaceRuleRegistry#register(BootstapContext, ResourceKey, ResourceKey, SurfaceRules.RuleSource)}
 * or {@link SurfaceRuleBuilder#register(BootstapContext, ResourceKey)}
 */
public class AssignedSurfaceRule {
    /**
     * The rule source of this rule.
     */
    public final SurfaceRules.RuleSource ruleSource;
    /**
     * The biome ID of this rule.
     */
    public final ResourceLocation biomeID;

    /**
     * The priority of this rule. Rules with higher priority will be first in the sequence.
     */
    public final int priority;

    /**
     * There should not be a need to create instances of this class directly.
     *
     * @param ruleSource The rule source of this rule.
     * @param biomeID    The biome ID of this rule.
     * @param priority   The priority of this rule. Rules with higher priority will be first in the sequence.
     * @see SurfaceRuleRegistry#register(BootstapContext, ResourceKey, ResourceKey, SurfaceRules.RuleSource)
     * @see SurfaceRuleBuilder#register(BootstapContext, ResourceKey)
     */
    @ApiStatus.Internal
    protected AssignedSurfaceRule(SurfaceRules.RuleSource ruleSource, ResourceLocation biomeID, int priority) {
        this.ruleSource = ruleSource;
        this.biomeID = biomeID;
        this.priority = priority;
    }
}
