package org.betterx.wover.block.api.predicate;

import org.betterx.wover.block.impl.predicate.BlockPredicatesImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;

import org.jetbrains.annotations.NotNull;

/**
 * And additional Block Predicate. Checks if the block at the given position has a full collision shape.
 * <p>
 * This predicate allows you to specify an offset to the position that is checked. The offset is added
 * to the input position before the test is performed
 */
public class IsFullShape implements BlockPredicate {
    static final IsFullShape HERE = new IsFullShape();
    /**
     * Codec for this block predicate.
     */
    public static final Codec<IsFullShape> CODEC = RecordCodecBuilder.create(
            instance -> instance
                    .group(
                            Vec3i.offsetCodec(16).optionalFieldOf("offset", Vec3i.ZERO).forGetter((p) -> p.offset)
                    ).apply(instance, IsFullShape::new));

    protected final Vec3i offset;

    private IsFullShape() {
        this(Vec3i.ZERO);
    }

    /**
     * Creates a new instance.
     *
     * @param offset The offset to apply
     */
    public IsFullShape(Vec3i offset) {
        super();
        this.offset = offset;
    }

    /**
     * Gets the type of this block predicate.
     *
     * @return The type
     */
    @Override
    public @NotNull BlockPredicateType<IsFullShape> type() {
        return BlockPredicatesImpl.FULL_SHAPE;
    }

    /**
     * Tests if the block at the given position has a full collision shape.
     *
     * @param worldGenLevel the world to check in
     * @param blockPos      the position to check
     * @return {@code true} if the block at the given position has a full collision shape, {@code false} otherwise
     */
    @Override
    public boolean test(WorldGenLevel worldGenLevel, BlockPos blockPos) {
        BlockState state = worldGenLevel.getBlockState(blockPos.offset(this.offset));
        return state.isCollisionShapeFullBlock(worldGenLevel, blockPos);
    }
}