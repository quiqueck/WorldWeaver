package de.ambertation.wover.surface.api;

import de.ambertation.wover.core.api.registry.DatapackRegistryBuilder;
import de.ambertation.wover.entrypoint.LibWoverSurface;
import de.ambertation.wover.events.api.Event;
import de.ambertation.wover.events.api.types.OnBootstrapRegistry;
import de.ambertation.wover.surface.impl.SurfaceRuleRegistryImpl;
import de.ambertation.wover.util.PriorityLinkedList;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules;

import org.jetbrains.annotations.NotNull;

/**
 * Provides a Datapack driven registry.
 * <p>
 * The Datapack files should be stored as {@code data/<namespace>/wover/surface_rules/<surface_rule_name>.json}.
 * The file can contain three Propertires:
 * <ul>
 * <li> <b>biome</b>: The ID of the Biome this rule applies to
 * <li> <b>ruleSource</b>: A Serialized Rule Source</li>
 * <li> <b>priority</b>: An optional priority. When multiple rules are added for a single biome,
 * the rules are put in a sequence sorted by priority, with the highest priority being first.</li>
 * </ul>
 * <p>
 * Surface Rules are injected using the {@link de.ambertation.wover.events.api.WorldLifecycle#BEFORE_CREATING_LEVELS}
 * event with an event priority of 500.
 *
 * @see de.ambertation.wover.surface.api
 */
public class SurfaceRuleRegistry {
    private SurfaceRuleRegistry() {
    }

    /**
     * This event is fired, after the surface rule registry was loaded. At this point, the
     * registry has gathered all surface rules from datapacks and is not yet frozen
     */
    public static final Event<OnBootstrapRegistry<AssignedSurfaceRule>> BOOTSTRAP_SURFACE_RULE_REGISTRY
            = SurfaceRuleRegistryImpl.BOOTSTRAP_SURFACE_RULE_REGISTRY;

    /**
     * The Key of the Registry. ({@code wover/worldgen/surface_rules})
     */
    public static final ResourceKey<Registry<AssignedSurfaceRule>> SURFACE_RULES_REGISTRY =
            DatapackRegistryBuilder.createRegistryKey(LibWoverSurface.C.id("wover/worldgen/surface_rules"));

    /**
     * Creates a ResourceKey for a SurfaceRule.
     *
     * @param ruleID The ID of the SurfaceRule
     * @return The ResourceKey
     */
    public static ResourceKey<AssignedSurfaceRule> createKey(
            Identifier ruleID
    ) {
        return SurfaceRuleRegistryImpl.createKey(ruleID);
    }

    /**
     * Registers a SurfaceRule for a Biome.
     * You can register multiple Rules for a single Biome. All rules for a Biome will be collected and sorted by priority.
     * The rule with the highest priority will be first in the sequence.
     *
     * @param ctx      The Bootstrap Context
     * @param key      The ResourceKey of the SurfaceRule
     * @param biomeKey The ResourceKey of the Biome you want to register the rule for
     * @param rules    The RuleSource of the SurfaceRule
     * @param priority The priority of the SurfaceRule.
     * @return A Holder for the SurfaceRule wrapped in a {@link AssignedSurfaceRule}
     */
    public static Holder<AssignedSurfaceRule> register(
            @NotNull BootstrapContext<AssignedSurfaceRule> ctx,
            @NotNull ResourceKey<AssignedSurfaceRule> key,
            @NotNull ResourceKey<Biome> biomeKey,
            @NotNull SurfaceRules.RuleSource rules,
            int priority
    ) {
        return SurfaceRuleRegistryImpl.register(ctx, key, biomeKey, rules, priority);
    }

    /**
     * Registers a SurfaceRule for a Biome with the default priority of PriorityLinkedList.DEFAULT_PRIORITY.
     * See {@link #register(BootstrapContext, ResourceKey, ResourceKey, SurfaceRules.RuleSource, int)} for mor Details.
     *
     * @param ctx      The Bootstrap Context
     * @param key      The ResourceKey of the SurfaceRule
     * @param biomeKey The ResourceKey of the Biome you want to register the rule for
     * @param rules    The RuleSource of the SurfaceRule
     * @return A Holder for the SurfaceRule wrapped in a {@link AssignedSurfaceRule}
     * @see #register(BootstrapContext, ResourceKey, ResourceKey, SurfaceRules.RuleSource, int)
     */
    public static Holder<AssignedSurfaceRule> register(
            @NotNull BootstrapContext<AssignedSurfaceRule> ctx,
            @NotNull ResourceKey<AssignedSurfaceRule> key,
            @NotNull ResourceKey<Biome> biomeKey,
            @NotNull SurfaceRules.RuleSource rules
    ) {
        return SurfaceRuleRegistryImpl.register(ctx, key, biomeKey, rules, PriorityLinkedList.DEFAULT_PRIORITY);
    }
}
