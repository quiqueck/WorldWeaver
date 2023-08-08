package org.betterx.wover.feature.api.placed.modifiers;

import org.betterx.wover.feature.impl.placed.modifiers.PlacementModifiersImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Tests if the block at a given position is in a basin
 * <p>
 * A Block is inside a basin, when the block below and all blocks
 * to the sides are matching the given predicate (north, south, east, west and below)
 * match the predicate.
 * <p>
 * You may also specify a predicate for the top block, which (if defined) will be
 * tested as well.
 * <p>
 * The position will be accepted if all performed predicate tests succeed.
 */
public class IsBasin extends PlacementFilter {
    /**
     * Codec for this placement modifier.
     */
    public static final Codec<IsBasin> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(
                    BlockPredicate.CODEC
                            .fieldOf("predicate")
                            .forGetter(cfg -> cfg.predicate),
                    BlockPredicate.CODEC
                            .optionalFieldOf("top_predicate", null)
                            .forGetter(cfg -> cfg.topPredicate)
            )
            .apply(instance, IsBasin::new));

    @NotNull
    private final BlockPredicate predicate;
    @Nullable
    private final BlockPredicate topPredicate;

    /**
     * Constructs a new instance.
     *
     * @param predicate    The predicate to test the surrounding blocks with
     * @param topPredicate predicate to test the top block with or {@code null} if no top
     *                     predicate should be tested
     */
    private IsBasin(@NotNull BlockPredicate predicate, @Nullable BlockPredicate topPredicate) {
        this.predicate = predicate;
        this.topPredicate = topPredicate;
    }

    /**
     * Constructs a modifier that will only test the basin blocks with the given predicate,
     * but will not perform a test against the top block.
     *
     * @param predicate The predicate to test the basin blocks with
     */
    public static PlacementFilter simple(BlockPredicate predicate) {
        return new IsBasin(predicate, null);
    }

    /**
     * Constructs a modifier that will test the basin blocks with the given predicate, and
     * also checks if the  top block is air.
     *
     * @param predicate The predicate to test the basin blocks with
     * @return a new instance
     */
    public static IsBasin openTop(BlockPredicate predicate) {
        return new IsBasin(predicate, BlockPredicate.ONLY_IN_AIR_PREDICATE);
    }

    /**
     * Constructs a modifier that will test the basin blocks with the given predicate
     *
     * @param ctx    The placement context
     * @param random The random source
     * @param pos    The position to test
     * @return {@code true} if the position is in a basin, {@code false} otherwise
     */
    @Override
    protected boolean shouldPlace(PlacementContext ctx, RandomSource random, BlockPos pos) {
        WorldGenLevel level = ctx.getLevel();
        if (topPredicate != null && !topPredicate.test(level, pos.above())) return false;

        return predicate.test(level, pos.below())
                && predicate.test(level, pos.west())
                && predicate.test(level, pos.east())
                && predicate.test(level, pos.north())
                && predicate.test(level, pos.south());
    }


    /**
     * Gets the type of this placement modifier.
     *
     * @return the type
     */
    @Override
    public @NotNull PlacementModifierType<?> type() {
        return PlacementModifiersImpl.IS_BASIN;
    }
}
