package com.betterxlib.api.worldgen;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

import java.util.ArrayList;
import java.util.List;

/**
 * Fluent builder for creating surface rules.
 * <p>
 * Surface rules define how terrain is generated (what blocks appear at the surface).
 * <p>
 * Example usage:
 * <pre>{@code
 * SurfaceRules.RuleSource rule = SurfaceRuleBuilder.create()
 *     .ifInBiome(MY_BIOME)
 *         .ifAboveWater()
 *             .ifStoneDepthFloor(0)
 *                 .setBlock(MY_GRASS)
 *             .endIf()
 *             .ifStoneDepthFloor(3)
 *                 .setBlock(MY_DIRT)
 *             .endIf()
 *         .endIf()
 *     .endIf()
 *     .build();
 * }</pre>
 */
public class SurfaceRuleBuilder {
    private final List<SurfaceRules.RuleSource> rules = new ArrayList<>();
    private SurfaceRules.ConditionSource currentCondition = null;
    private final List<ConditionContext> conditionStack = new ArrayList<>();

    private SurfaceRuleBuilder() {}

    /**
     * Create a new surface rule builder.
     *
     * @return a new SurfaceRuleBuilder
     */
    public static SurfaceRuleBuilder create() {
        return new SurfaceRuleBuilder();
    }

    // Biome conditions

    /**
     * Apply rules only if in a specific biome.
     *
     * @param biome the biome key
     * @return this builder
     */
    public SurfaceRuleBuilder ifInBiome(ResourceKey<Biome> biome) {
        pushCondition(SurfaceRules.isBiome(biome));
        return this;
    }

    /**
     * Apply rules only if in any of the specified biomes.
     *
     * @param biomes the biome keys
     * @return this builder
     */
    @SafeVarargs
    public final SurfaceRuleBuilder ifInBiome(ResourceKey<Biome>... biomes) {
        pushCondition(SurfaceRules.isBiome(biomes));
        return this;
    }

    // Height conditions

    /**
     * Apply rules only if above a specific Y level.
     *
     * @param y the Y level
     * @return this builder
     */
    public SurfaceRuleBuilder ifAboveY(int y) {
        pushCondition(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(y), 0));
        return this;
    }

    /**
     * Apply rules only if below a specific Y level.
     *
     * @param y the Y level
     * @return this builder
     */
    public SurfaceRuleBuilder ifBelowY(int y) {
        pushCondition(SurfaceRules.not(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(y), 0)));
        return this;
    }

    /**
     * Apply rules only if above preliminary surface (prelim surface is where grass would go).
     *
     * @return this builder
     */
    public SurfaceRuleBuilder ifAboveWater() {
        pushCondition(SurfaceRules.waterBlockCheck(-1, 0));
        return this;
    }

    /**
     * Apply rules only if below water.
     *
     * @return this builder
     */
    public SurfaceRuleBuilder ifBelowWater() {
        pushCondition(SurfaceRules.waterBlockCheck(0, 0));
        return this;
    }

    // Stone depth conditions

    /**
     * Apply rules for the floor surface at a specific depth.
     *
     * @param depth the depth from surface
     * @return this builder
     */
    public SurfaceRuleBuilder ifStoneDepthFloor(int depth) {
        pushCondition(SurfaceRules.stoneDepthCheck(depth, false, 0, SurfaceRules.CaveSurface.FLOOR));
        return this;
    }

    /**
     * Apply rules for the ceiling surface at a specific depth.
     *
     * @param depth the depth from surface
     * @return this builder
     */
    public SurfaceRuleBuilder ifStoneDepthCeiling(int depth) {
        pushCondition(SurfaceRules.stoneDepthCheck(depth, false, 0, SurfaceRules.CaveSurface.CEILING));
        return this;
    }

    /**
     * Apply rules on the surface (depth 0, floor).
     *
     * @return this builder
     */
    public SurfaceRuleBuilder ifOnSurface() {
        return ifStoneDepthFloor(0);
    }

    /**
     * Apply rules in the subsurface (depth 0-5, floor).
     *
     * @return this builder
     */
    public SurfaceRuleBuilder ifInSubsurface() {
        pushCondition(SurfaceRules.stoneDepthCheck(0, true, 5, SurfaceRules.CaveSurface.FLOOR));
        return this;
    }

    // Noise conditions

    /**
     * Apply rules based on surface noise.
     *
     * @param noiseMin minimum noise value
     * @param noiseMax maximum noise value
     * @return this builder
     */
    public SurfaceRuleBuilder ifSurfaceNoise(double noiseMin, double noiseMax) {
        pushCondition(SurfaceRules.noiseCondition(
            net.minecraft.world.level.levelgen.synth.NormalNoise.NoiseParameters.CODEC.parse(
                com.mojang.serialization.JsonOps.INSTANCE,
                com.google.gson.JsonParser.parseString("{}")
            ).result().orElseThrow(), noiseMin, noiseMax));
        return this;
    }

    // Steep conditions

    /**
     * Apply rules only on steep terrain.
     *
     * @return this builder
     */
    public SurfaceRuleBuilder ifSteep() {
        pushCondition(SurfaceRules.steep());
        return this;
    }

    // Hole condition

    /**
     * Apply rules only in holes/caves.
     *
     * @return this builder
     */
    public SurfaceRuleBuilder ifHole() {
        pushCondition(SurfaceRules.hole());
        return this;
    }

    // Block setting

    /**
     * Set the block at this position.
     *
     * @param block the block
     * @return this builder
     */
    public SurfaceRuleBuilder setBlock(Block block) {
        return setBlockState(block.defaultBlockState());
    }

    /**
     * Set the block state at this position.
     *
     * @param state the block state
     * @return this builder
     */
    public SurfaceRuleBuilder setBlockState(BlockState state) {
        SurfaceRules.RuleSource blockRule = SurfaceRules.state(state);

        if (conditionStack.isEmpty()) {
            rules.add(blockRule);
        } else {
            // Wrap in all current conditions
            SurfaceRules.RuleSource wrapped = blockRule;
            for (int i = conditionStack.size() - 1; i >= 0; i--) {
                wrapped = SurfaceRules.ifTrue(conditionStack.get(i).condition, wrapped);
            }
            conditionStack.get(conditionStack.size() - 1).rules.add(blockRule);
        }

        return this;
    }

    /**
     * End the current condition block.
     *
     * @return this builder
     */
    public SurfaceRuleBuilder endIf() {
        if (!conditionStack.isEmpty()) {
            ConditionContext context = conditionStack.remove(conditionStack.size() - 1);
            if (!context.rules.isEmpty()) {
                SurfaceRules.RuleSource combined;
                if (context.rules.size() == 1) {
                    combined = SurfaceRules.ifTrue(context.condition, context.rules.get(0));
                } else {
                    combined = SurfaceRules.ifTrue(context.condition,
                        SurfaceRules.sequence(context.rules.toArray(new SurfaceRules.RuleSource[0])));
                }

                if (conditionStack.isEmpty()) {
                    rules.add(combined);
                } else {
                    conditionStack.get(conditionStack.size() - 1).rules.add(combined);
                }
            }
        }
        return this;
    }

    /**
     * Build the final surface rule.
     *
     * @return the surface rule source
     */
    public SurfaceRules.RuleSource build() {
        // Close any remaining conditions
        while (!conditionStack.isEmpty()) {
            endIf();
        }

        if (rules.isEmpty()) {
            throw new IllegalStateException("No rules added to surface rule builder");
        }

        if (rules.size() == 1) {
            return rules.get(0);
        }

        return SurfaceRules.sequence(rules.toArray(new SurfaceRules.RuleSource[0]));
    }

    private void pushCondition(SurfaceRules.ConditionSource condition) {
        conditionStack.add(new ConditionContext(condition));
    }

    private static class ConditionContext {
        final SurfaceRules.ConditionSource condition;
        final List<SurfaceRules.RuleSource> rules = new ArrayList<>();

        ConditionContext(SurfaceRules.ConditionSource condition) {
            this.condition = condition;
        }
    }
}
