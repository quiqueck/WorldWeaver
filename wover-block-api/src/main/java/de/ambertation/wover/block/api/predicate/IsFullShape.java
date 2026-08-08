package de.ambertation.wover.block.api.predicate;

import de.ambertation.wover.block.impl.predicate.BlockPredicatesImpl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelAccessor;
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
    /**
     * Tests if the block at the current position has a full collision shape.
     */
    public static final IsFullShape HERE = new IsFullShape();

    /**
     * Codec for this block predicate.
     */
    public static final MapCodec<IsFullShape> CODEC = RecordCodecBuilder.mapCodec(
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
     * <p>
     * 26.3 widened vanilla's {@code BlockPredicate} from {@code BiPredicate<WorldGenLevel, BlockPos>} to
     * {@code BiPredicate<LevelAccessor, BlockPos>}, so block predicates are no longer restricted to the
     * worldgen path. {@code LevelAccessor} is a supertype of {@code WorldGenLevel} and still provides both
     * {@code getBlockState} and the {@code BlockGetter} that {@code isCollisionShapeFullBlock} needs, so the
     * test itself is unchanged.
     *
     * @param levelAccessor the world to check in
     * @param blockPos      the position to check
     * @return {@code true} if the block at the given position has a full collision shape, {@code false} otherwise
     */
    @Override
    public boolean test(LevelAccessor levelAccessor, BlockPos blockPos) {
        BlockState state = levelAccessor.getBlockState(blockPos.offset(this.offset));
        return state.isCollisionShapeFullBlock(levelAccessor, blockPos);
    }
}