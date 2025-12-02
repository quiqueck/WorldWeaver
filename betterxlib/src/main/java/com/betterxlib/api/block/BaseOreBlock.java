package com.betterxlib.api.block;

import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

/**
 * A base ore block with configurable XP drops.
 */
public class BaseOreBlock extends DropExperienceBlock {

    public BaseOreBlock(Properties properties, IntProvider xpRange) {
        super(xpRange, properties);
    }

    public BaseOreBlock(Properties properties) {
        this(properties, UniformInt.of(0, 0));
    }

    /**
     * Create default stone ore properties.
     *
     * @return properties suitable for stone ores
     */
    public static Properties stoneOreProperties() {
        return Properties.of()
            .mapColor(MapColor.STONE)
            .strength(3.0f, 3.0f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE);
    }

    /**
     * Create default deepslate ore properties.
     *
     * @return properties suitable for deepslate ores
     */
    public static Properties deepslateOreProperties() {
        return Properties.of()
            .mapColor(MapColor.DEEPSLATE)
            .strength(4.5f, 3.0f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.DEEPSLATE);
    }

    /**
     * Create default nether ore properties.
     *
     * @return properties suitable for nether ores
     */
    public static Properties netherOreProperties() {
        return Properties.of()
            .mapColor(MapColor.NETHER)
            .strength(3.0f, 3.0f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.NETHER_ORE);
    }

    /**
     * Create default end ore properties.
     *
     * @return properties suitable for end ores
     */
    public static Properties endOreProperties() {
        return Properties.of()
            .mapColor(MapColor.SAND)
            .strength(3.0f, 9.0f)
            .requiresCorrectToolForDrops()
            .sound(SoundType.STONE);
    }

    /**
     * Create a stone ore block with XP range.
     *
     * @param minXp minimum XP dropped
     * @param maxXp maximum XP dropped
     * @return a new BaseOreBlock
     */
    public static BaseOreBlock stoneOre(int minXp, int maxXp) {
        return new BaseOreBlock(stoneOreProperties(), UniformInt.of(minXp, maxXp));
    }

    /**
     * Create a deepslate ore block with XP range.
     *
     * @param minXp minimum XP dropped
     * @param maxXp maximum XP dropped
     * @return a new BaseOreBlock
     */
    public static BaseOreBlock deepslateOre(int minXp, int maxXp) {
        return new BaseOreBlock(deepslateOreProperties(), UniformInt.of(minXp, maxXp));
    }

    /**
     * Create a nether ore block with XP range.
     *
     * @param minXp minimum XP dropped
     * @param maxXp maximum XP dropped
     * @return a new BaseOreBlock
     */
    public static BaseOreBlock netherOre(int minXp, int maxXp) {
        return new BaseOreBlock(netherOreProperties(), UniformInt.of(minXp, maxXp));
    }

    /**
     * Create an end ore block with XP range.
     *
     * @param minXp minimum XP dropped
     * @param maxXp maximum XP dropped
     * @return a new BaseOreBlock
     */
    public static BaseOreBlock endOre(int minXp, int maxXp) {
        return new BaseOreBlock(endOreProperties(), UniformInt.of(minXp, maxXp));
    }

    /**
     * Create from an existing block.
     *
     * @param source the source block
     * @param minXp minimum XP dropped
     * @param maxXp maximum XP dropped
     * @return a new BaseOreBlock
     */
    public static BaseOreBlock from(Block source, int minXp, int maxXp) {
        return new BaseOreBlock(BlockBehaviour.Properties.ofFullCopy(source), UniformInt.of(minXp, maxXp));
    }
}
