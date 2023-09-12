package org.betterx.wover.surface.impl;

import org.betterx.wover.surface.api.Conditions;
import org.betterx.wover.surface.api.conditions.NoiseCondition;
import org.betterx.wover.util.PriorityLinkedList;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;

public interface BaseSurfaceRuleBuilder<T extends BaseSurfaceRuleBuilder<T>> {
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


    /**
     * Set biome surface with specified {@link BlockState}. Example - block of grass in the Overworld biomes
     * The rule is added with priority {@link #TOP_SURFACE_PRIORITY}.
     *
     * @param state {@link BlockState} for the ground cover.
     * @return same {@link BaseSurfaceRuleBuilder} instance.
     */
    T surface(BlockState state);

    /**
     * Set biome surface with the specified {@link Block}. Example - block of grass in the Overworld biomes
     * The rule is added with priority {@link #TOP_SURFACE_PRIORITY}.
     *
     * @param block {@link Block} for the ground cover.
     * @return same {@link BaseSurfaceRuleBuilder} instance.
     */
    default T surface(Block block) {
        return surface(block.defaultBlockState());
    }

    /**
     * Set biome subsurface with specified {@link BlockState}. Example - dirt in the Overworld biomes.
     * The rule is added with priority {@link #SUB_SURFACE_PRIORITY}.
     *
     * @param state {@link BlockState} for the subterrain layer.
     * @param depth block layer depth.
     * @return same {@link BaseSurfaceRuleBuilder} instance.
     */
    T subsurface(BlockState state, int depth);

    /**
     * Set biome subsurface with specified {@link Block}. Example - dirt in the Overworld biomes.
     * The rule is added with priority {@link #SUB_SURFACE_PRIORITY}.
     *
     * @param block {@link Block} for the subterrain layer.
     * @param depth block layer depth.
     * @return same {@link BaseSurfaceRuleBuilder} instance.
     */
    default T subsurface(Block block, int depth) {
        return subsurface(block.defaultBlockState(), depth);
    }

    /**
     * Set biome filler with specified {@link BlockState}. Example - stone in the Overworld biomes.
     * The rule is added with priority {@link #FILLER_PRIORITY}.
     *
     * @param state {@link BlockState} for filling.
     * @return same {@link BaseSurfaceRuleBuilder} instance.
     */
    T filler(BlockState state);

    /**
     * Set biome floor with specified {@link BlockState}. Example - underside of a gravel floor.
     * The rule is added with priority {@link #FLOOR_PRIORITY}.
     *
     * @param state {@link BlockState} for the ground cover.
     * @return same {@link BaseSurfaceRuleBuilder} instance.
     */
    T floor(BlockState state);

    /**
     * Set biome floor material with specified {@link BlockState} and height.
     * The rule is added with priority {@link #BELOW_FLOOR_PRIORITY}.
     *
     * @param state  {@link BlockState} for the subterrain layer.
     * @param height block layer height.
     * @param noise  The noise object that is applied
     * @return same {@link BaseSurfaceRuleBuilder} instance.
     */
    T belowFloor(BlockState state, int height, NoiseCondition noise);

    /**
     * Set biome floor material with specified {@link BlockState} and height.
     * The rule is added with priority {@link #BELOW_FLOOR_PRIORITY}.
     *
     * @param state  {@link BlockState} for the subterrain layer.
     * @param height block layer height.
     * @return same {@link BaseSurfaceRuleBuilder} instance.
     */
    T belowFloor(BlockState state, int height);

    /**
     * Set biome ceiling with specified {@link BlockState}. Example - block of sandstone in the Overworld desert in air pockets.
     * The rule is added with priority {@link #CEILING_PRIORITY}.
     *
     * @param state {@link BlockState} for the ground cover.
     * @return same {@link BaseSurfaceRuleBuilder} instance.
     */
    T ceil(BlockState state);

    /**
     * Set biome ceiling material with specified {@link BlockState} and height. Example - sandstone in the Overworld deserts.
     * The rule is added with priority {@link #ABOVE_CEILING_PRIORITY}.
     *
     * @param state  {@link BlockState} for the subterrain layer.
     * @param height block layer height.
     * @return same {@link BaseSurfaceRuleBuilder} instance.
     */
    T aboveCeil(BlockState state, int height);

    /**
     * Will cover steep areas (with large terrain angle). Example - Overworld mountains.
     * The rule is added with priority {@link #STEEP_SURFACE_PRIORITY}.
     *
     * @param state {@link BlockState} for the steep layer.
     * @param depth layer depth
     * @return same {@link BaseSurfaceRuleBuilder} instance.
     */
    T steep(BlockState state, int depth);

    /**
     * Allows to add custom rule.
     *
     * @param priority rule priority, lower values = higher priority (rule will be applied before others).
     * @param rule     custom {@link SurfaceRules.RuleSource}.
     * @return same {@link BaseSurfaceRuleBuilder} instance.
     */
    T rule(SurfaceRules.RuleSource rule, int priority);

    /**
     * Allows to add custom rule with default priority {@link PriorityLinkedList#DEFAULT_PRIORITY}.
     *
     * @param rule custom {@link SurfaceRules.RuleSource}.
     * @return same {@link BaseSurfaceRuleBuilder} instance.
     */
    T rule(SurfaceRules.RuleSource rule);

    /**
     * Set biome floor with specified {@link BlockState} and the
     * {@link Conditions#DOUBLE_BLOCK_SURFACE_NOISE}.
     * The rule is added with priority {@link #FLOOR_PRIORITY}.
     *
     * @param surfaceBlockA {@link BlockState} for the ground cover.
     * @param surfaceBlockB {@link BlockState} for the alternative ground cover.
     * @return same {@link BaseSurfaceRuleBuilder} instance.
     */
    T chancedFloor(BlockState surfaceBlockA, BlockState surfaceBlockB);

    /**
     * Set biome floor with specified {@link BlockState} and the given Noise Function.
     * The rule is added with priority {@link #FLOOR_PRIORITY}.
     *
     * @param surfaceBlockA {@link BlockState} for the ground cover.
     * @param surfaceBlockB {@link BlockState} for the alternative ground cover.
     * @param noise         The {@link NoiseCondition}
     * @return same {@link BaseSurfaceRuleBuilder} instance.
     */
    T chancedFloor(BlockState surfaceBlockA, BlockState surfaceBlockB, NoiseCondition noise);

    /**
     * Set biome floor with specified {@link BlockState}/{@link SurfaceRules.RuleSource} and the given Noise Function.
     * The rule is added with priority {@link #FLOOR_PRIORITY}.
     *
     * @param surfaceBlockA {@link BlockState} for the ground cover.
     * @param surfaceBlockB {@link SurfaceRules.RuleSource} for the alternative ground cover.
     * @param noise         The {@link NoiseCondition}
     * @return same {@link BaseSurfaceRuleBuilder} instance.
     */
    T chancedFloor(BlockState surfaceBlockA, SurfaceRules.RuleSource surfaceBlockB, NoiseCondition noise);

    /**
     * Change the sorting order of this rule. Higher priority rules are applied first if there are multiple rules for a
     * specific biome. By default the Priority is set to {@link PriorityLinkedList#DEFAULT_PRIORITY}
     *
     * @param priority The priority to register the rule with.
     * @return same {@link BaseSurfaceRuleBuilder} instance.
     */
    T sortPriority(int priority);
}
