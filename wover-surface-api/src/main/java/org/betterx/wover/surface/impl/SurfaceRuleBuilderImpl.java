package org.betterx.wover.surface.impl;

import org.betterx.wover.surface.api.AssignedSurfaceRule;
import org.betterx.wover.surface.api.Conditions;
import org.betterx.wover.surface.api.SurfaceRuleBuilder;
import org.betterx.wover.surface.api.SurfaceRuleRegistry;
import org.betterx.wover.surface.api.conditions.NoiseCondition;
import org.betterx.wover.util.PriorityLinkedList;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

import org.jetbrains.annotations.NotNull;

public class SurfaceRuleBuilderImpl<T extends BaseSurfaceRuleBuilder<T>> implements BaseSurfaceRuleBuilder<T> {
    private final PriorityLinkedList<SurfaceRules.RuleSource> rules;
    protected ResourceKey<Biome> biomeKey;
    protected int sortPriority;


    public SurfaceRuleBuilderImpl() {
        this.biomeKey = null;
        this.sortPriority = PriorityLinkedList.DEFAULT_PRIORITY;
        this.rules = new PriorityLinkedList<>();
    }

    /**
     * Restricts surface to only one biome.
     *
     * @param biomeKey {@link ResourceKey} for the {@link Biome}.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T biome(ResourceKey<Biome> biomeKey) {
        this.biomeKey = biomeKey;
        return (T) this;
    }

    /**
     * Restricts surface to only one biome.
     *
     * @param biomeHolder {@link Holder} for the {@link Biome}.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T biome(Holder<Biome> biomeHolder) {
        this.biomeKey = biomeHolder.unwrapKey().orElseThrow();
        return (T) this;
    }

    /**
     * The {@link ResourceKey} for the biome filter
     *
     * @return {@link ResourceKey} for the {@link Biome}.
     */
    public ResourceKey<Biome> biomeKey() {
        return biomeKey;
    }

    /**
     * Set biome surface with specified {@link BlockState}. Example - block of grass in the Overworld biomes
     * The rule is added with priority {@link #TOP_SURFACE_PRIORITY}.
     *
     * @param state {@link BlockState} for the ground cover.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T surface(BlockState state) {
        SurfaceRules.RuleSource rule = SurfaceRules.state(state);
        rule = SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, rule);
        rules.add(rule, TOP_SURFACE_PRIORITY);

        return (T) this;
    }

    /**
     * Set biome subsurface with specified {@link BlockState}. Example - dirt in the Overworld biomes.
     * The rule is added with priority {@link #SUB_SURFACE_PRIORITY}.
     *
     * @param state {@link BlockState} for the subterrain layer.
     * @param depth block layer depth.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T subsurface(BlockState state, int depth) {
        SurfaceRules.RuleSource rule = SurfaceRules.state(state);
        rule = SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(depth, false, CaveSurface.FLOOR), rule);
        rules.add(rule, SUB_SURFACE_PRIORITY);

        return (T) this;
    }

    /**
     * Set biome filler with specified {@link BlockState}. Example - stone in the Overworld biomes.
     * The rule is added with priority {@link #FILLER_PRIORITY}.
     *
     * @param state {@link BlockState} for filling.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T filler(BlockState state) {
        rules.add(SurfaceRules.state(state), FILLER_PRIORITY);
        return (T) this;
    }

    /**
     * Set biome floor with specified {@link BlockState}. Example - underside of a gravel floor.
     * The rule is added with priority {@link #FLOOR_PRIORITY}.
     *
     * @param state {@link BlockState} for the ground cover.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T floor(BlockState state) {
        SurfaceRules.RuleSource rule = SurfaceRules.state(state);
        rule = SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, rule);
        rules.add(rule, FLOOR_PRIORITY);
        return (T) this;
    }

    /**
     * Set biome floor material with specified {@link BlockState} and height.
     * The rule is added with priority {@link #BELOW_FLOOR_PRIORITY}.
     *
     * @param state  {@link BlockState} for the subterrain layer.
     * @param height block layer height.
     * @param noise  The noise object that is applied
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T belowFloor(BlockState state, int height, NoiseCondition noise) {

        SurfaceRules.RuleSource rule = SurfaceRules.state(state);
        rule = SurfaceRules.ifTrue(
                SurfaceRules.stoneDepthCheck(
                        height,
                        false,
                        CaveSurface.FLOOR
                ),
                SurfaceRules.ifTrue(noise, rule)
        );


        rules.add(rule, BELOW_FLOOR_PRIORITY);
        return (T) this;
    }

    /**
     * Set biome floor material with specified {@link BlockState} and height.
     * The rule is added with priority {@link #BELOW_FLOOR_PRIORITY}.
     *
     * @param state  {@link BlockState} for the subterrain layer.
     * @param height block layer height.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T belowFloor(BlockState state, int height) {
        SurfaceRules.RuleSource rule = SurfaceRules.state(state);
        rule = SurfaceRules.ifTrue(
                SurfaceRules.stoneDepthCheck(
                        height,
                        false,
                        CaveSurface.FLOOR
                ), rule);

        rules.add(rule, BELOW_FLOOR_PRIORITY);
        return (T) this;
    }

    /**
     * Set biome ceiling with specified {@link BlockState}. Example - block of sandstone in the Overworld desert in air pockets.
     * The rule is added with priority {@link #CEILING_PRIORITY}.
     *
     * @param state {@link BlockState} for the ground cover.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T ceil(BlockState state) {
        SurfaceRules.RuleSource rule = SurfaceRules.state(state);
        rule = SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, rule);
        rules.add(rule, CEILING_PRIORITY);
        return (T) this;
    }

    /**
     * Set biome ceiling material with specified {@link BlockState} and height. Example - sandstone in the Overworld deserts.
     * The rule is added with priority {@link #ABOVE_CEILING_PRIORITY}.
     *
     * @param state  {@link BlockState} for the subterrain layer.
     * @param height block layer height.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T aboveCeil(BlockState state, int height) {
        SurfaceRules.RuleSource rule = SurfaceRules.state(state);
        rule = SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(height, false, CaveSurface.CEILING), rule);

        rules.add(rule, ABOVE_CEILING_PRIORITY);
        return (T) this;
    }

    /**
     * Will cover steep areas (with large terrain angle). Example - Overworld mountains.
     * The rule is added with priority {@link #STEEP_SURFACE_PRIORITY}.
     *
     * @param state {@link BlockState} for the steep layer.
     * @param depth layer depth
     * @return
     */
    public T steep(BlockState state, int depth) {
        SurfaceRules.RuleSource rule = SurfaceRules.state(state);
        rule = SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(depth, false, CaveSurface.FLOOR), rule);
        rule = SurfaceRules.ifTrue(SurfaceRules.steep(), rule);

        rules.add(rule, STEEP_SURFACE_PRIORITY);
        return (T) this;
    }

    /**
     * Allows to add custom rule.
     *
     * @param priority rule priority, lower values = higher priority (rule will be applied before others).
     * @param rule     custom {@link SurfaceRules.RuleSource}.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T rule(SurfaceRules.RuleSource rule, int priority) {
        rules.add(rule, priority);
        return (T) this;
    }

    /**
     * Allows to add custom rule with default priority {@link PriorityLinkedList#DEFAULT_PRIORITY}.
     *
     * @param rule custom {@link SurfaceRules.RuleSource}.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T rule(SurfaceRules.RuleSource rule) {
        rules.add(rule);
        return (T) this;
    }

    /**
     * Set biome floor with specified {@link BlockState} and the
     * {@link Conditions#DOUBLE_BLOCK_SURFACE_NOISE}.
     * The rule is added with priority {@link #FLOOR_PRIORITY}.
     *
     * @param surfaceBlockA {@link BlockState} for the ground cover.
     * @param surfaceBlockB {@link BlockState} for the alternative ground cover.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T chancedFloor(BlockState surfaceBlockA, BlockState surfaceBlockB) {
        return chancedFloor(surfaceBlockA, surfaceBlockB, Conditions.DOUBLE_BLOCK_SURFACE_NOISE);
    }

    /**
     * Set biome floor with specified {@link BlockState} and the given Noise Function.
     * The rule is added with priority {@link #FLOOR_PRIORITY}.
     *
     * @param surfaceBlockA {@link BlockState} for the ground cover.
     * @param surfaceBlockB {@link BlockState} for the alternative ground cover.
     * @param noise         The {@link NoiseCondition}
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T chancedFloor(BlockState surfaceBlockA, BlockState surfaceBlockB, NoiseCondition noise) {
        SurfaceRules.RuleSource rule =
                SurfaceRules.ifTrue(
                        SurfaceRules.ON_FLOOR,
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(
                                        noise,
                                        SurfaceRules.state(
                                                surfaceBlockA)
                                ),
                                SurfaceRules.state(surfaceBlockB)
                        )
                );

        rules.add(rule, FLOOR_PRIORITY);
        return (T) this;
    }

    /**
     * Change the sorting order of this rule. Higher priority rules are applied first if there are multiple rules for a
     * specific biome. By default the Priority is set to {@link PriorityLinkedList#DEFAULT_PRIORITY}
     *
     * @param priority The priority to register the rule with.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public T sortPriority(int priority) {
        this.sortPriority = priority;
        return (T) this;
    }

    /**
     * Finalise rule building process.
     *
     * @return {@link SurfaceRules.RuleSource}.
     */
    public SurfaceRules.RuleSource build() {
        SurfaceRules.RuleSource rule = getRuleSource();
        if (biomeKey != null) {
            rule = SurfaceRules.ifTrue(SurfaceRules.isBiome(biomeKey), rule);
        }
        return rule;
    }

    @NotNull
    protected SurfaceRules.RuleSource getRuleSource() {
        if (rules.size() == 1) {
            return rules.get(0);
        }
        SurfaceRules.RuleSource[] ruleArray = rules.toArray(new SurfaceRules.RuleSource[rules.size()]);
        SurfaceRules.RuleSource rule = SurfaceRules.sequence(ruleArray);
        return rule;
    }


    public static class StandalonBuilder extends SurfaceRuleBuilderImpl<SurfaceRuleBuilder> implements SurfaceRuleBuilder {
        /**
         * Register rule in the {@link SurfaceRuleRegistry} with the currently set sort priority (see {@link #sortPriority}).
         *
         * @param ctx The {@link BootstapContext} to register the rule with.
         * @param key The {@link ResourceKey} to register the rule with.
         * @return The {@link Holder} for the registry item.
         * @see SurfaceRuleRegistry#register(BootstapContext, ResourceKey, ResourceKey, SurfaceRules.RuleSource, int)
         */

        public Holder<AssignedSurfaceRule> register(
                @NotNull BootstapContext<AssignedSurfaceRule> ctx,
                @NotNull ResourceKey<AssignedSurfaceRule> key
        ) {
            if (biomeKey == null) {
                throw new IllegalStateException("Biome key is not set for surface rule '" + key.location() + "'");
            }

            return SurfaceRuleRegistryImpl.register(ctx, key, biomeKey, getRuleSource(), sortPriority);
        }
    }
}
