package com.betterxlib.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * A base ladder block with configurable properties.
 */
public class BaseLadderBlock extends LadderBlock {

    private final boolean isFlammable;

    public BaseLadderBlock(Properties properties) {
        this(properties, true);
    }

    public BaseLadderBlock(Properties properties, boolean isFlammable) {
        super(properties);
        this.isFlammable = isFlammable;
    }

    /**
     * Create default wood ladder properties.
     *
     * @return properties suitable for ladders
     */
    public static Properties woodProperties() {
        return Properties.of()
            .mapColor(MapColor.WOOD)
            .strength(0.4f)
            .noOcclusion()
            .sound(SoundType.LADDER)
            .pushReaction(PushReaction.DESTROY);
    }

    /**
     * Create default metal ladder properties.
     *
     * @return properties suitable for metal ladders
     */
    public static Properties metalProperties() {
        return Properties.of()
            .mapColor(MapColor.METAL)
            .strength(0.8f)
            .requiresCorrectToolForDrops()
            .noOcclusion()
            .sound(SoundType.METAL)
            .pushReaction(PushReaction.DESTROY);
    }

    /**
     * Create a wood ladder block.
     *
     * @return a new BaseLadderBlock
     */
    public static BaseLadderBlock wood() {
        return new BaseLadderBlock(woodProperties(), true);
    }

    /**
     * Create a metal ladder block.
     *
     * @return a new BaseLadderBlock
     */
    public static BaseLadderBlock metal() {
        return new BaseLadderBlock(metalProperties(), false);
    }

    /**
     * Create from an existing block.
     *
     * @param source the source block
     * @return a new BaseLadderBlock
     */
    public static BaseLadderBlock from(Block source) {
        return new BaseLadderBlock(BlockBehaviour.Properties.ofFullCopy(source));
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return isFlammable ? 20 : 0;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return isFlammable ? 5 : 0;
    }
}
