package org.betterx.wover.feature.api.placed.modifiers;

import org.betterx.wover.feature.impl.placed.modifiers.PlacementModifiersImpl;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

/**
 * Offsets the input position between 0 and 15 in the xz-Plane and generates a new
 * Position for each generated position.
 * <p>
 * The minimum position that is emitted is the input position, the largest position is
 * the input offset by {@code (15, 0, 15)}.
 */
public class All extends PlacementModifier {
    /**
     * The instance of this placement modifier.
     */
    private static final All INSTANCE = new All();
    /**
     * The codec for this placement modifier.
     */
    public static final Codec<All> CODEC = Codec.unit(All::new);

    /**
     * Returns a stream of all positions that are offset by 0 to 15 in the xz-Plane.
     *
     * @param placementContext The placement context.
     * @param randomSource     The random source.
     * @param blockPos         The input position.
     * @return The stream of new positions.
     */
    @Override
    public @NotNull Stream<BlockPos> getPositions(
            PlacementContext placementContext,
            RandomSource randomSource,
            BlockPos blockPos
    ) {
        return IntStream.range(0, 16 * 16 - 1).mapToObj(i -> blockPos.offset(i & 0xF, 0, i >> 4));
    }

    /**
     * Returns the instance of this placement modifier.
     *
     * @return the instance of this placement modifier.
     */
    public static PlacementModifier simple() {
        return INSTANCE;
    }

    /**
     * The type of this placement modifier.
     *
     * @return the type of this placement modifier.
     */
    @Override
    public @NotNull PlacementModifierType<?> type() {
        return PlacementModifiersImpl.ALL;
    }
}
