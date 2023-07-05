package org.betterx.wover.surface.api;

import org.betterx.wover.surface.api.conditions.NoiseCondition;
import org.betterx.wover.surface.impl.SurfaceRuleRegistryImpl;
import org.betterx.wover.util.PriorityLinkedList;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.RuleSource;
import net.minecraft.world.level.levelgen.placement.CaveSurface;

import org.jetbrains.annotations.NotNull;

public class SurfaceRuleBuilder {
    /**
     * {@code = 2900} - Default priority for steep surfaces. This is the highest priority Rule.
     */
    public static int STEEP_SURFACE_PRIORITY = 2 * PriorityLinkedList.DEFAULT_PRIORITY + 900;
    /**
     * {@code = 2800} - Default priority for surface blocks. This is the next priority after {@link #STEEP_SURFACE_PRIORITY}
     */
    public static int TOP_SURFACE_PRIORITY = 2 * PriorityLinkedList.DEFAULT_PRIORITY + 800;
    /**
     * {@code = 2700} - Default priority for ceiling blocks. This is the next priority after {@link #TOP_SURFACE_PRIORITY}
     */
    public static int CEILING_PRIORITY = 2 * PriorityLinkedList.DEFAULT_PRIORITY + 700;
    /**
     * {@code = 2600} - Default priority for sub surfaces blocks. This is the next priority after {@link #CEILING_PRIORITY}
     */
    public static int SUB_SURFACE_PRIORITY = 2 * PriorityLinkedList.DEFAULT_PRIORITY + 600;
    /**
     * {@code = 2500} - Default priority for floor blocks. This is the next priority after {@link #SUB_SURFACE_PRIORITY}
     */
    public static int FLOOR_PRIORITY = 2 * PriorityLinkedList.DEFAULT_PRIORITY + 500;

    /**
     * {@code = 2400} - Default priority for blocks below a floor. This is the next priority after {@link #FLOOR_PRIORITY}
     */
    public static int BELOW_FLOOR_PRIORITY = 2 * PriorityLinkedList.DEFAULT_PRIORITY + 400;

    /**
     * {@code = 2300} -  Default priority for blocks above a ceiling. This is the next priority after {@link #BELOW_FLOOR_PRIORITY}
     */
    public static int ABOVE_CEILING_PRIORITY = 2 * PriorityLinkedList.DEFAULT_PRIORITY + 300;
    /**
     * {@code = 900} -  Default priority for filler blocks. This is lower than the default priority for all other rules.
     */
    public static int FILLER_PRIORITY = PriorityLinkedList.DEFAULT_PRIORITY - 100;

    private final PriorityLinkedList<RuleSource> rules;
    private ResourceKey<Biome> biomeKey;


    private SurfaceRuleBuilder() {
        this.biomeKey = null;
        this.rules = new PriorityLinkedList<>();
    }

    public static SurfaceRuleBuilder start() {
        return new SurfaceRuleBuilder();
    }

    /**
     * Restricts surface to only one biome.
     *
     * @param biomeKey {@link ResourceKey} for the {@link Biome}.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public SurfaceRuleBuilder biome(ResourceKey<Biome> biomeKey) {
        this.biomeKey = biomeKey;
        return this;
    }

    /**
     * Restricts surface to only one biome.
     *
     * @param biomeHolder {@link Holder} for the {@link Biome}.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public SurfaceRuleBuilder biome(Holder<Biome> biomeHolder) {
        this.biomeKey = biomeHolder.unwrapKey().orElseThrow();
        return this;
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
    public SurfaceRuleBuilder surface(BlockState state) {
        RuleSource rule = SurfaceRules.state(state);
        rule = SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, rule);
        rules.add(rule, TOP_SURFACE_PRIORITY);

        return this;
    }

    /**
     * Set biome subsurface with specified {@link BlockState}. Example - dirt in the Overworld biomes.
     * The rule is added with priority {@link #SUB_SURFACE_PRIORITY}.
     *
     * @param state {@link BlockState} for the subterrain layer.
     * @param depth block layer depth.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public SurfaceRuleBuilder subsurface(BlockState state, int depth) {
        RuleSource rule = SurfaceRules.state(state);
        rule = SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(depth, false, CaveSurface.FLOOR), rule);
        rules.add(rule, SUB_SURFACE_PRIORITY);

        return this;
    }

    /**
     * Set biome filler with specified {@link BlockState}. Example - stone in the Overworld biomes.
     * The rule is added with priority {@link #FILLER_PRIORITY}.
     *
     * @param state {@link BlockState} for filling.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public SurfaceRuleBuilder filler(BlockState state) {
        rules.add(SurfaceRules.state(state), FILLER_PRIORITY);
        return this;
    }

    /**
     * Set biome floor with specified {@link BlockState}. Example - underside of a gravel floor.
     * The rule is added with priority {@link #FLOOR_PRIORITY}.
     *
     * @param state {@link BlockState} for the ground cover.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public SurfaceRuleBuilder floor(BlockState state) {
        RuleSource rule = SurfaceRules.state(state);
        rule = SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, rule);
        rules.add(rule, FLOOR_PRIORITY);
        return this;
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
    public SurfaceRuleBuilder belowFloor(BlockState state, int height, NoiseCondition noise) {

        RuleSource rule = SurfaceRules.state(state);
        rule = SurfaceRules.ifTrue(
                SurfaceRules.stoneDepthCheck(
                        height,
                        false,
                        CaveSurface.FLOOR
                ),
                SurfaceRules.ifTrue(noise, rule)
        );


        rules.add(rule, BELOW_FLOOR_PRIORITY);
        return this;
    }

    /**
     * Set biome floor material with specified {@link BlockState} and height.
     * The rule is added with priority {@link #BELOW_FLOOR_PRIORITY}.
     *
     * @param state  {@link BlockState} for the subterrain layer.
     * @param height block layer height.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public SurfaceRuleBuilder belowFloor(BlockState state, int height) {
        RuleSource rule = SurfaceRules.state(state);
        rule = SurfaceRules.ifTrue(
                SurfaceRules.stoneDepthCheck(
                        height,
                        false,
                        CaveSurface.FLOOR
                ), rule);

        rules.add(rule, BELOW_FLOOR_PRIORITY);
        return this;
    }

    /**
     * Set biome ceiling with specified {@link BlockState}. Example - block of sandstone in the Overworld desert in air pockets.
     * The rule is added with priority {@link #CEILING_PRIORITY}.
     *
     * @param state {@link BlockState} for the ground cover.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public SurfaceRuleBuilder ceil(BlockState state) {
        RuleSource rule = SurfaceRules.state(state);
        rule = SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, rule);
        rules.add(rule, CEILING_PRIORITY);
        return this;
    }

    /**
     * Set biome ceiling material with specified {@link BlockState} and height. Example - sandstone in the Overworld deserts.
     * The rule is added with priority {@link #ABOVE_CEILING_PRIORITY}.
     *
     * @param state  {@link BlockState} for the subterrain layer.
     * @param height block layer height.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public SurfaceRuleBuilder aboveCeil(BlockState state, int height) {
        RuleSource rule = SurfaceRules.state(state);
        rule = SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(height, false, CaveSurface.CEILING), rule);

        rules.add(rule, ABOVE_CEILING_PRIORITY);
        return this;
    }

    /**
     * Will cover steep areas (with large terrain angle). Example - Overworld mountains.
     * The rule is added with priority {@link #STEEP_SURFACE_PRIORITY}.
     *
     * @param state {@link BlockState} for the steep layer.
     * @param depth layer depth
     * @return
     */
    public SurfaceRuleBuilder steep(BlockState state, int depth) {
        RuleSource rule = SurfaceRules.state(state);
        rule = SurfaceRules.ifTrue(SurfaceRules.stoneDepthCheck(depth, false, CaveSurface.FLOOR), rule);
        rule = SurfaceRules.ifTrue(SurfaceRules.steep(), rule);

        rules.add(rule, STEEP_SURFACE_PRIORITY);
        return this;
    }

    /**
     * Allows to add custom rule.
     *
     * @param priority rule priority, lower values = higher priority (rule will be applied before others).
     * @param rule     custom {@link RuleSource}.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public SurfaceRuleBuilder rule(RuleSource rule, int priority) {
        rules.add(rule, priority);
        return this;
    }

    /**
     * Allows to add custom rule with default priority {@link PriorityLinkedList#DEFAULT_PRIORITY}.
     *
     * @param rule custom {@link RuleSource}.
     * @return same {@link SurfaceRuleBuilder} instance.
     */
    public SurfaceRuleBuilder rule(RuleSource rule) {
        rules.add(rule);
        return this;
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
    public SurfaceRuleBuilder chancedFloor(BlockState surfaceBlockA, BlockState surfaceBlockB) {
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
    public SurfaceRuleBuilder chancedFloor(BlockState surfaceBlockA, BlockState surfaceBlockB, NoiseCondition noise) {
        RuleSource rule =
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
        return this;
    }

    /**
     * Finalise rule building process.
     *
     * @return {@link RuleSource}.
     */
    public RuleSource build() {
        RuleSource rule = getRuleSource();
        if (biomeKey != null) {
            rule = SurfaceRules.ifTrue(SurfaceRules.isBiome(biomeKey), rule);
        }
        return rule;
    }

    @NotNull
    private RuleSource getRuleSource() {
        if (rules.size() == 1) {
            return rules.get(0);
        }
        RuleSource[] ruleArray = rules.toArray(new RuleSource[rules.size()]);
        RuleSource rule = SurfaceRules.sequence(ruleArray);
        return rule;
    }

    /**
     * Register rule in the {@link SurfaceRuleRegistry} with default priority {@link PriorityLinkedList#DEFAULT_PRIORITY}.
     *
     * @param ctx The {@link BootstapContext} to register the rule with.
     * @param key The {@link ResourceKey} to register the rule with.
     * @return The {@link Holder} for the registry item.
     */
    public Holder<AssignedSurfaceRule> register(
            @NotNull BootstapContext<AssignedSurfaceRule> ctx,
            @NotNull ResourceKey<AssignedSurfaceRule> key
    ) {
        return register(ctx, key, PriorityLinkedList.DEFAULT_PRIORITY);
    }

    /**
     * Register rule in the {@link SurfaceRuleRegistry}.
     *
     * @param ctx      The {@link BootstapContext} to register the rule with.
     * @param key      The {@link ResourceKey} to register the rule with.
     * @param priority The priority to register the rule with. Higher priority rules are applied first in the rule sequence for a biome.
     * @return The {@link Holder} for the registry item.
     */
    public Holder<AssignedSurfaceRule> register(
            @NotNull BootstapContext<AssignedSurfaceRule> ctx,
            @NotNull ResourceKey<AssignedSurfaceRule> key,
            int priority
    ) {
        if (biomeKey == null) {
            throw new IllegalStateException("Biome key is not set for surface rule '" + key.location() + "'");
        }

        return SurfaceRuleRegistryImpl.register(ctx, key, biomeKey, getRuleSource(), priority);
    }
}
