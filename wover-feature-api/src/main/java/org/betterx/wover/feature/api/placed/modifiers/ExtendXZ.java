package org.betterx.wover.feature.api.placed.modifiers;

import org.betterx.wover.feature.impl.placed.modifiers.PlacementModifiersImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

/**
 * Extends the input direction in the xz-plane by the given radius. This will
 * create a new position for every block in the radius around the input.
 * <p>
 * The density of the positions can be controlled by the {@code centerDensity} and
 * {@code borderDensity} parameters. The {@code centerDensity} controls the density
 * at the input position, while the {@code borderDensity} controls the density
 * for blocks at radius distance from the input position. Positions in between
 * will be interpolated linearly. The density is the chance of a position to be
 * accepted. If the density for a position is {@code 0} the position will be
 * rejected. If the density is {@code 1} the position will be accepted.
 * <p>
 * If {@code square} is {@code true}, the positions will be created in a square
 * with edge length {@code 2*xzSpread+1} centered on the input position.
 * If {@code false}, the positions will be created in a circle.
 */
public class ExtendXZ extends PlacementModifier {
    /**
     * The codec for this placement modifier.
     */
    public static final Codec<ExtendXZ> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(
                    IntProvider.codec(0, 16)
                               .fieldOf("radius")
                               .forGetter(cfg -> cfg.radius),
                    FloatProvider.codec(0, 1)
                                 .optionalFieldOf("center_density", ConstantFloat.of(1))
                                 .forGetter(cfg -> cfg.centerDensity),
                    FloatProvider.codec(0, 1)
                                 .optionalFieldOf("border_density", ConstantFloat.of(0.05f))
                                 .forGetter(cfg -> cfg.borderDensity),
                    Codec.BOOL
                            .optionalFieldOf("square", false)
                            .forGetter(cfg -> cfg.square)
            )
            .apply(instance, ExtendXZ::new));
    private final IntProvider radius;
    private final FloatProvider centerDensity;
    private final FloatProvider borderDensity;
    private final boolean square;

    /**
     * Creates a new placement modifier that extends the input direction in the
     * xz-plane by the given radius.
     *
     * @param radius        the radius to extend the input position by
     * @param centerDensity the density at the input position
     * @param borderDensity the density at the border of the radius
     * @param square        if {@code true}, the positions will be created in a square
     *                      with edge length {@code 2*xzSpread+1} centered on the input position.
     *                      If {@code false}, the positions will be created in a circle.
     */
    public ExtendXZ(IntProvider radius, FloatProvider centerDensity, FloatProvider borderDensity, boolean square) {
        this.radius = radius;
        this.centerDensity = centerDensity;
        this.borderDensity = borderDensity;
        this.square = square;
    }

    /**
     * Creates a new placement modifier that extends the input direction in the
     * xz-plane by the given radius in a circular shape.
     *
     * @param radius        the radius to extend the input position by
     * @param centerDensity the density at the input position
     * @param borderDensity the density at the border of the radius
     * @return
     */
    public static ExtendXZ circle(IntProvider radius, FloatProvider centerDensity, FloatProvider borderDensity) {
        return new ExtendXZ(radius, centerDensity, borderDensity, false);
    }

    /**
     * Creates a new placement modifier that extends the input direction in the
     * xz-plane by the given radius in a square shape.
     *
     * @param radius        the radius to extend the input position by
     * @param centerDensity the density at the input position
     * @param borderDensity the density at the border of the radius
     * @return
     */
    public static ExtendXZ square(IntProvider radius, FloatProvider centerDensity, FloatProvider borderDensity) {
        return new ExtendXZ(radius, centerDensity, borderDensity, true);
    }

    /**
     * Calculates the positions that this placement modifier will emit.
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
        final var builder = Stream.<BlockPos>builder();
        final int r = radius.sample(randomSource);
        final float r2 = r * r;
        final float d0 = centerDensity.sample(randomSource);
        final float d1 = borderDensity.sample(randomSource);


        float currentR2, density, lambda;
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                currentR2 = x * x + z * z;
                //only consider blocks within a circular area
                if (!square && currentR2 > r2) continue;

                lambda = currentR2 / r2;
                density = d0 + (d1 - d0) * lambda;

                //basically a 0 density, so discard
                if (density <= 0.001) continue;

                //if density is >=1 the condition will always be false
                if (randomSource.nextFloat() > density) continue;

                builder.add(blockPos.offset(x, 0, z));
            }
        }
        return builder.build();
    }

    /**
     * Gets the type of this placement modifier.
     *
     * @return the type of this placement modifier
     */
    @Override
    public @NotNull PlacementModifierType<ExtendXZ> type() {
        return PlacementModifiersImpl.EXTEND_XZ;
    }
}
