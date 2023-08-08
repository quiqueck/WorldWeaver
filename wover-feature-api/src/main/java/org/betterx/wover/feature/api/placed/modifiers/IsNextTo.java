package org.betterx.wover.feature.api.placed.modifiers;

import org.betterx.wover.feature.impl.placed.modifiers.PlacementModifiersImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Tests if the block at a given offset to teh input position is surrounded
 * in the xz plane (north, east, south, west) by blocks that match the given
 * predicate.
 */
public class IsNextTo extends PlacementFilter {
    /**
     * Codec for this placement modifier.
     */
    public static final Codec<IsNextTo> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(
                    BlockPredicate.CODEC
                            .fieldOf("predicate")
                            .forGetter(cfg -> cfg.predicate),
                    Vec3i.CODEC
                            .optionalFieldOf("offset", Vec3i.ZERO)
                            .forGetter(cfg -> cfg.offset)
            )
            .apply(instance, IsNextTo::new));

    private final BlockPredicate predicate;
    private final Vec3i offset;


    private IsNextTo(@NotNull BlockPredicate predicate, @NotNull Vec3i offset) {
        this.predicate = predicate;
        this.offset = offset;
    }

    /**
     * Tests the xz-neighborhood of the input position
     *
     * @param predicate The predicate to test
     * @return A new instance
     */
    public static PlacementFilter simple(@NotNull BlockPredicate predicate) {
        return new IsNextTo(predicate, Vec3i.ZERO);
    }

    /**
     * Tests the xz-neighborhood of the the offset input position
     *
     * @param predicate The predicate to test
     * @param offset    The offset relative to the input position to test at
     * @return A new instance
     */
    public static PlacementFilter offset(@NotNull BlockPredicate predicate, @Nullable Vec3i offset) {
        return new IsNextTo(predicate, offset);
    }

    /**
     * Tests if the block at a given offset relative to the input position
     * is surrounded in the xz plane (north, east, south, west) by blocks
     * that match the given predicate.
     *
     * @param ctx    The placement context
     * @param random The random source
     * @param pos    The input position
     * @return True if the block at the given offset is surrounded by blocks
     */
    @Override
    protected boolean shouldPlace(PlacementContext ctx, RandomSource random, BlockPos pos) {
        WorldGenLevel level = ctx.getLevel();

        pos = pos.offset(this.offset);
        return predicate.test(level, pos.west())
                || predicate.test(level, pos.east())
                || predicate.test(level, pos.north())
                || predicate.test(level, pos.south());
    }

    /**
     * Returns the type of this placement modifier.
     *
     * @return The type of this placement modifier.
     */
    @Override
    public @NotNull PlacementModifierType<?> type() {
        return PlacementModifiersImpl.IS_NEXT_TO;
    }
}
