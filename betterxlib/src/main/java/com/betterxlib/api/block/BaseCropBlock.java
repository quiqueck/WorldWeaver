package com.betterxlib.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

/**
 * A base crop block with configurable growth stages.
 */
public class BaseCropBlock extends CropBlock {

    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
        Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
        Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
        Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
        Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
        Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
        Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
        Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
        Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
    };

    private final Supplier<? extends ItemLike> seedsItem;
    private final int maxAge;
    private final float growthSpeed;

    public BaseCropBlock(Properties properties, Supplier<? extends ItemLike> seedsItem) {
        this(properties, seedsItem, 7, 1.0f);
    }

    public BaseCropBlock(Properties properties, Supplier<? extends ItemLike> seedsItem, int maxAge, float growthSpeed) {
        super(properties);
        this.seedsItem = seedsItem;
        this.maxAge = maxAge;
        this.growthSpeed = growthSpeed;
    }

    /**
     * Create default crop properties.
     *
     * @return properties suitable for crops
     */
    public static Properties defaultProperties() {
        return Properties.of()
            .noCollission()
            .randomTicks()
            .instabreak()
            .sound(SoundType.CROP)
            .pushReaction(PushReaction.DESTROY);
    }

    /**
     * Create a crop block with default properties.
     *
     * @param seedsItem supplier for the seeds item
     * @return a new BaseCropBlock
     */
    public static BaseCropBlock create(Supplier<? extends ItemLike> seedsItem) {
        return new BaseCropBlock(defaultProperties(), seedsItem);
    }

    /**
     * Create a crop block with custom max age.
     *
     * @param seedsItem supplier for the seeds item
     * @param maxAge the maximum age (growth stage)
     * @return a new BaseCropBlock
     */
    public static BaseCropBlock create(Supplier<? extends ItemLike> seedsItem, int maxAge) {
        return new BaseCropBlock(defaultProperties(), seedsItem, maxAge, 1.0f);
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return seedsItem.get();
    }

    @Override
    public int getMaxAge() {
        return maxAge;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int age = state.getValue(getAgeProperty());
        int shapeIndex = Math.min(age, SHAPE_BY_AGE.length - 1);
        return SHAPE_BY_AGE[shapeIndex];
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;
        if (level.getRawBrightness(pos, 0) >= 9) {
            int age = getAge(state);
            if (age < getMaxAge()) {
                float growthChance = getGrowthSpeed(state, level, pos) * growthSpeed;
                if (random.nextFloat() < growthChance / 10.0f) {
                    level.setBlock(pos, getStateForAge(age + 1), 2);
                }
            }
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return !isMaxAge(state);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }
}
