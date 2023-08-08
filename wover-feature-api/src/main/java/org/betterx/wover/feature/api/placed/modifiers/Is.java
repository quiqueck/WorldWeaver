package org.betterx.wover.feature.api.placed.modifiers;

import org.betterx.wover.feature.impl.placed.modifiers.PlacementModifiersImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;

/**
 * Tests if the block at a given offset relative to the input position
 * matches the given predicate.
 * <p>
 * The offset is added to the input position, and the test is performed
 * at the resulting position. The Modifier will emit the unmodified
 * input position if the test succeeds, or nothing if it fails.
 */
public class Is extends PlacementFilter {
    /**
     * Codec for this placement modifier.
     */
    public static final Codec<Is> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(
                    BlockPredicate.CODEC
                            .fieldOf("predicate")
                            .forGetter(cfg -> cfg.predicate),
                    Vec3i.CODEC
                            .optionalFieldOf("offset")
                            .forGetter(cfg -> cfg.offset)
            )
            .apply(instance, Is::new));

    private final BlockPredicate predicate;
    private final Optional<Vec3i> offset;

    /**
     * Constructs a new instance.
     *
     * @param predicate The predicate to test.
     * @param offset    The offset to apply.
     */
    public Is(BlockPredicate predicate, Optional<Vec3i> offset) {
        this.predicate = predicate;
        this.offset = offset;
    }

    /**
     * Will test the input position for the given predicate
     *
     * @param predicate The predicate to test
     * @return a new instance
     */
    public static Is simple(BlockPredicate predicate) {
        return new Is(predicate, Optional.empty());
    }

    /**
     * Will test the block below the input position for the given predicate
     *
     * @param predicate The predicate to test
     * @return a new instance
     */
    public static Is below(BlockPredicate predicate) {
        return new Is(predicate, Optional.of(Direction.DOWN.getNormal()));
    }

    /**
     * Will test the block above the input position for the given predicate
     *
     * @param predicate The predicate to test
     * @return a new instance
     */
    public static Is above(BlockPredicate predicate) {
        return new Is(predicate, Optional.of(Direction.UP.getNormal()));
    }

    /**
     * Returns true, if the predicate was true for the block at the given offset
     *
     * @param ctx    The placement context
     * @param random The random source
     * @param pos    The input position
     * @return true, if the predicate was true for the block at the given offset
     */
    @Override
    protected boolean shouldPlace(PlacementContext ctx, RandomSource random, BlockPos pos) {
        WorldGenLevel level = ctx.getLevel();
        return predicate.test(level, offset.map(v -> pos.offset(v.getX(), v.getY(), v.getZ())).orElse(pos));
    }

    /**
     * Returns the type of this placement modifier.
     *
     * @return The type
     */
    @Override
    public @NotNull PlacementModifierType<Is> type() {
        return PlacementModifiersImpl.IS;
    }
}
