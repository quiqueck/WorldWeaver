package org.betterx.wover.feature.api.placed.modifiers;

import org.betterx.wover.feature.impl.placed.modifiers.PlacementModifiersImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;


/**
 * Extends the input position in the given direction by the given length. All extended positions
 * will be emitted. The input position will be emitted as well.
 * <p>
 * This will basically generate a colum of positions in the given direction.
 */
public class Extend extends PlacementModifier {
    /**
     * The codec for this placement modifier.
     */
    public static final Codec<Extend> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(
                    Direction.CODEC
                            .fieldOf("direction")
                            .orElse(Direction.DOWN)
                            .forGetter(cfg -> cfg.direction),
                    IntProvider.codec(0, 16)
                               .fieldOf("length")
                               .orElse(UniformInt.of(0, 3))
                               .forGetter(cfg -> cfg.length)
            )
            .apply(instance, Extend::new));

    private final Direction direction;
    private final IntProvider length;

    /**
     * Constructs a new extend placement modifier.
     *
     * @param direction The direction to extend in.
     * @param length    The length to extend by.
     */
    public Extend(Direction direction, IntProvider length) {
        this.direction = direction;
        this.length = length;
    }

    /**
     * Calculates the positions that this placement modifier will emit.
     *
     * @param placementContext The placement context.
     * @param random           The random source.
     * @param blockPos         The input position.
     * @return The stream of new positions.
     */
    @Override
    public @NotNull Stream<BlockPos> getPositions(
            PlacementContext placementContext,
            RandomSource random,
            BlockPos blockPos
    ) {
        var builder = Stream.<BlockPos>builder();
        final int count = length.sample(random);
        builder.add(blockPos);
        for (int y = 1; y < count + 1; y++) {
            builder.add(blockPos.relative(direction, y));
        }
        return builder.build();
    }

    /**
     * Gets the type of this placement modifier.
     *
     * @return The type of this placement modifier.
     */
    @Override
    public @NotNull PlacementModifierType<?> type() {
        return PlacementModifiersImpl.EXTEND;
    }
}