package com.betterxlib.api.worldgen;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

/**
 * Helper methods for creating common surface rules.
 */
public final class SurfaceRuleHelper {

    private SurfaceRuleHelper() {
        // Utility class
    }

    /**
     * Create a simple biome surface rule with grass, dirt, and stone-like blocks.
     *
     * @param biome the biome key
     * @param topBlock the top grass-like block
     * @param underBlock the dirt-like under block
     * @param stoneBlock the stone-like deep block
     * @return a surface rule for this biome
     */
    public static SurfaceRules.RuleSource simpleSurface(
        ResourceKey<Biome> biome,
        Block topBlock,
        Block underBlock,
        Block stoneBlock
    ) {
        return SurfaceRules.ifTrue(
            SurfaceRules.isBiome(biome),
            SurfaceRules.sequence(
                // Top layer (grass)
                SurfaceRules.ifTrue(
                    SurfaceRules.waterBlockCheck(-1, 0),
                    SurfaceRules.ifTrue(
                        SurfaceRules.stoneDepthCheck(0, false, 0, SurfaceRules.CaveSurface.FLOOR),
                        SurfaceRules.state(topBlock.defaultBlockState())
                    )
                ),
                // Middle layer (dirt)
                SurfaceRules.ifTrue(
                    SurfaceRules.stoneDepthCheck(0, true, 3, SurfaceRules.CaveSurface.FLOOR),
                    SurfaceRules.state(underBlock.defaultBlockState())
                ),
                // Deep layer (stone)
                SurfaceRules.state(stoneBlock.defaultBlockState())
            )
        );
    }

    /**
     * Create an End biome surface rule with a single top block.
     *
     * @param biome the biome key
     * @param surfaceBlock the surface block (like end stone)
     * @return a surface rule for this End biome
     */
    public static SurfaceRules.RuleSource endSurface(ResourceKey<Biome> biome, Block surfaceBlock) {
        return SurfaceRules.ifTrue(
            SurfaceRules.isBiome(biome),
            SurfaceRules.state(surfaceBlock.defaultBlockState())
        );
    }

    /**
     * Create a Nether biome surface rule with netherrack-like blocks.
     *
     * @param biome the biome key
     * @param topBlock the top surface block
     * @param baseBlock the base block (like netherrack)
     * @return a surface rule for this Nether biome
     */
    public static SurfaceRules.RuleSource netherSurface(
        ResourceKey<Biome> biome,
        Block topBlock,
        Block baseBlock
    ) {
        return SurfaceRules.ifTrue(
            SurfaceRules.isBiome(biome),
            SurfaceRules.sequence(
                // Surface layer
                SurfaceRules.ifTrue(
                    SurfaceRules.stoneDepthCheck(0, false, 0, SurfaceRules.CaveSurface.FLOOR),
                    SurfaceRules.state(topBlock.defaultBlockState())
                ),
                // Ceiling layer
                SurfaceRules.ifTrue(
                    SurfaceRules.stoneDepthCheck(0, false, 0, SurfaceRules.CaveSurface.CEILING),
                    SurfaceRules.state(topBlock.defaultBlockState())
                ),
                // Base layer
                SurfaceRules.state(baseBlock.defaultBlockState())
            )
        );
    }

    /**
     * Create a rule that applies a block state.
     *
     * @param state the block state
     * @return a surface rule that places this block
     */
    public static SurfaceRules.RuleSource block(BlockState state) {
        return SurfaceRules.state(state);
    }

    /**
     * Create a rule that applies a block.
     *
     * @param block the block
     * @return a surface rule that places this block
     */
    public static SurfaceRules.RuleSource block(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }

    /**
     * Create a condition for being in a biome.
     *
     * @param biome the biome key
     * @return a condition source
     */
    public static SurfaceRules.ConditionSource inBiome(ResourceKey<Biome> biome) {
        return SurfaceRules.isBiome(biome);
    }

    /**
     * Create a condition for being above water level.
     *
     * @return a condition source
     */
    public static SurfaceRules.ConditionSource aboveWater() {
        return SurfaceRules.waterBlockCheck(-1, 0);
    }

    /**
     * Create a condition for the floor surface.
     *
     * @param depth the depth from surface
     * @return a condition source
     */
    public static SurfaceRules.ConditionSource floorDepth(int depth) {
        return SurfaceRules.stoneDepthCheck(depth, false, 0, SurfaceRules.CaveSurface.FLOOR);
    }

    /**
     * Create a condition for above a Y level.
     *
     * @param y the Y level
     * @return a condition source
     */
    public static SurfaceRules.ConditionSource aboveY(int y) {
        return SurfaceRules.yBlockCheck(VerticalAnchor.absolute(y), 0);
    }

    /**
     * Create a conditional rule.
     *
     * @param condition the condition
     * @param rule the rule to apply if condition is true
     * @return a conditional surface rule
     */
    public static SurfaceRules.RuleSource ifTrue(
        SurfaceRules.ConditionSource condition,
        SurfaceRules.RuleSource rule
    ) {
        return SurfaceRules.ifTrue(condition, rule);
    }

    /**
     * Create a sequence of rules.
     *
     * @param rules the rules to apply in order
     * @return a sequential surface rule
     */
    public static SurfaceRules.RuleSource sequence(SurfaceRules.RuleSource... rules) {
        return SurfaceRules.sequence(rules);
    }
}
