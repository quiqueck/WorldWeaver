package org.betterx.wover.feature.api.placed.modifiers;

import org.betterx.wover.feature.impl.placed.modifiers.PlacementModifiersImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.ConstantInt;
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
public class ExtendXYZ extends PlacementModifier {
    private interface PropagationFunction {
        void propagate(
                RandomSource randomSource,
                Stream.Builder<BlockPos> builder,
                float maxR2, int maxDepth, int scale, float d0, float d1,
                float currentR2, float density,
                BlockPos pos, boolean didAdd
        );
    }

    /**
     * Determines how the positions are extended in the y-direction.
     */
    public enum HeightPropagation implements StringRepresentable {
        /**
         * The positions will not be extended in the y-direction.
         */
        NONE(1, ((randomSource, builder, maxR2, maxDepth, scale1, d0, d1, currentR2, density, pos, didAdd) -> {
        })),

        /**
         * The positions will be extended in the <b>negative y-direction</b> by a constant height. The density is maximum at the
         * original height and decreases until the maximum height is reached.
         */
        BOX_DOWN(-1, ExtendXYZ::propagateSquare),

        /**
         * The positions will be extended in the <b>positive y-direction</b> by a constant height. The density is maximum at the
         * original height and decreases until the maximum height is reached.
         */
        BOX_UP(1, ExtendXYZ::propagateSquare),

        /**
         * The positions will be extended in the <b>negative y-direction</b>. The extension height is maximum at the center
         * and decreases spherical towards the edges. The density is maximum at the original height and decreases until
         * the maximum height is reached.
         */
        SPHERE_DOWN(-1, ExtendXYZ::propagateSphere),
        /**
         * The positions will be extended in the <b>positive y-direction</b>. The extension height is maximum at the center
         * and decreases spherical towards the edges. The density is maximum at the original height and decreases until
         * the maximum height is reached.
         */
        SPHERE_UP(1, ExtendXYZ::propagateSphere),

        /**
         * Every position at the surface is extended <b>down</b> until the density reaches zero. The start density is the
         * one at the surface.
         */
        SPIKES_DOWN(-1, ExtendXYZ::propagateSpikesConnected),
        /**
         * Every position at the surface is extended <b>up</b> until the density reaches zero. The start density is the
         * one at the surface.
         */
        SPIKES_UP(1, ExtendXYZ::propagateSpikesConnected);

        /**
         * The codec for this enum.
         */
        public static final Codec<HeightPropagation> CODEC = StringRepresentable.fromEnum(HeightPropagation::values);

        private final int scale;
        private final PropagationFunction propagationFunction;

        HeightPropagation(int scale, PropagationFunction propagationFunction) {
            this.scale = scale;
            this.propagationFunction = propagationFunction;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase();
        }
    }

    /**
     * The codec for this placement modifier.
     */
    public static final Codec<ExtendXYZ> CODEC = RecordCodecBuilder.create((instance) -> instance
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
                            .forGetter(cfg -> cfg.square),
                    IntProvider.codec(0, 16)
                               .optionalFieldOf("height", ConstantInt.of(0))
                               .forGetter(cfg -> cfg.height),
                    HeightPropagation.CODEC
                            .optionalFieldOf("height_propagation", HeightPropagation.NONE)
                            .forGetter(cfg -> cfg.heightPropagation)
            )
            .apply(instance, ExtendXYZ::new));
    private final IntProvider radius;
    private final FloatProvider centerDensity;
    private final FloatProvider borderDensity;
    private final boolean square;


    private final IntProvider height;
    private final HeightPropagation heightPropagation;

    /**
     * Creates a new placement modifier that extends the input direction in the
     * xz-plane by the given radius.
     *
     * @param radius            the radius to extend the input position by
     * @param centerDensity     the density at the input position
     * @param borderDensity     the density at the border of the radius
     * @param square            if {@code true}, the positions will be created in a square
     *                          with edge length {@code 2*xzSpread+1} centered on the input position.
     *                          If {@code false}, the positions will be created in a circle.
     * @param height            the height to extend the positions in the y-direction
     * @param heightPropagation the {@link HeightPropagation} to use
     */
    public ExtendXYZ(
            IntProvider radius,
            FloatProvider centerDensity,
            FloatProvider borderDensity,
            boolean square,
            IntProvider height,
            HeightPropagation heightPropagation
    ) {
        this.radius = radius;
        this.centerDensity = centerDensity;
        this.borderDensity = borderDensity;
        this.square = square;
        this.height = height;
        this.heightPropagation = heightPropagation;

    }

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
    public ExtendXYZ(IntProvider radius, FloatProvider centerDensity, FloatProvider borderDensity, boolean square) {
        this(radius, centerDensity, borderDensity, square, ConstantInt.of(0), HeightPropagation.NONE);
    }

    /**
     * Creates a new placement modifier that extends the input direction in the
     * xz-plane by the given radius in a circular shape.
     *
     * @param radius        the radius to extend the input position by
     * @param centerDensity the density at the input position
     * @param borderDensity the density at the border of the radius
     * @return a new instance
     */
    public static ExtendXYZ circle(IntProvider radius, FloatProvider centerDensity, FloatProvider borderDensity) {
        return new ExtendXYZ(radius, centerDensity, borderDensity, false);
    }

    /**
     * Creates a new placement modifier that extends the input direction in the
     * xz-plane by the given radius in a circular shape. Every generated position
     * will then be extended down to form spikes.
     *
     * @param radius        the radius to extend the input position by
     * @param centerDensity the density at the input position
     * @param borderDensity the density at the border of the radius
     * @param height        the height to extend the positions in the y-direction
     * @return a new instance
     */
    public static ExtendXYZ spikedCircle(
            IntProvider radius,
            FloatProvider centerDensity,
            FloatProvider borderDensity,
            IntProvider height
    ) {
        return new ExtendXYZ(radius, centerDensity, borderDensity, false, height, HeightPropagation.SPIKES_DOWN);
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
    public static ExtendXYZ square(IntProvider radius, FloatProvider centerDensity, FloatProvider borderDensity) {
        return new ExtendXYZ(radius, centerDensity, borderDensity, true);
    }

    private static void propagateSphere(
            RandomSource randomSource,
            Stream.Builder<BlockPos> builder,
            float maxR2, int maxDepth, int scale, float d0, float d1,
            float currentR2, float currentDensity,
            BlockPos pos, boolean didAdd
    ) {
        final int depth = (int) (maxDepth * (1 - (currentR2 / maxR2)));
        propagateDown(randomSource, builder, d0, d1, currentDensity, pos, depth, scale);
    }

    private static void propagateSquare(
            RandomSource randomSource,
            Stream.Builder<BlockPos> builder,
            float maxR2, int maxDepth, int scale, float d0, float d1,
            float currentR2, float currentDensity,
            BlockPos pos, boolean didAdd
    ) {
        propagateDown(randomSource, builder, d0, d1, currentDensity, pos, maxDepth, scale);
    }

    private static void propagateSpikesConnected(
            RandomSource randomSource,
            Stream.Builder<BlockPos> builder,
            float maxR2, int maxDepth, int scale, float d0, float d1,
            float currentR2, float currentDensity,
            BlockPos pos, boolean didAdd
    ) {
        if (!didAdd) return;
        propagateDownConnected(randomSource, builder, d0, d1, currentDensity * currentDensity, pos, maxDepth, scale);
    }

    private static void propagateDownConnected(
            RandomSource randomSource,
            Stream.Builder<BlockPos> builder,
            float d0, float d1,
            float currentDensity,
            BlockPos pos, int depth, int scale
    ) {
        final int depth2 = depth * depth;
        float lambda, densityDown;
        for (int y = 1; y <= depth; y++) {
            lambda = (float) (y * y) / depth2;
            densityDown = (d0 + (d1 - d0) * lambda) * currentDensity;

            //basically a 0 density, so discard
            if (densityDown <= 0.001) return;

            //if densityDown is >=1 the condition will always be false
            if (randomSource.nextFloat() > densityDown) return;

            builder.add(pos.above(scale * y));
        }
    }

    private static void propagateDown(
            RandomSource randomSource,
            Stream.Builder<BlockPos> builder,
            float d0, float d1,
            float currentDensity,
            BlockPos pos, int depth, int scale
    ) {
        final int depth2 = depth * depth;
        float lambda, densityDown;
        for (int y = 1; y <= depth; y++) {
            lambda = (float) (y * y) / depth2;
            densityDown = (d0 + (d1 - d0) * lambda) * currentDensity;

            //basically a 0 density, so discard
            if (densityDown <= 0.001) continue;

            //if densityDown is >=1 the condition will always be false
            if (randomSource.nextFloat() > densityDown) continue;

            builder.add(pos.above(y * scale));
        }
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
        final Stream.Builder<BlockPos> builder = Stream.<BlockPos>builder();
        final int r = radius.sample(randomSource);
        final float r2 = r * r;
        final float d0 = centerDensity.sample(randomSource);
        final float d1 = borderDensity.sample(randomSource);
        final int height = this.height.sample(randomSource);

        float currentR2, density, lambda;
        BlockPos now;
        for (int x = -r; x <= r; x++) {
            for (int z = -r; z <= r; z++) {
                currentR2 = x * x + z * z;
                //only consider blocks within a circular area
                if (!square && currentR2 > r2) continue;

                lambda = currentR2 / r2;
                density = d0 + (d1 - d0) * lambda;
                now = blockPos.offset(x, 0, z);

                if (density >= 1.0f || ( //if density is >=1 the condition will always be true
                        density > 0.001f && density >= randomSource.nextFloat()
                )) {
                    builder.add(now);
                    heightPropagation.propagationFunction.propagate(
                            randomSource, builder,
                            r2, height, heightPropagation.scale, d0, d1,
                            currentR2, density, now, true
                    );
                } else {
                    heightPropagation.propagationFunction.propagate(
                            randomSource, builder,
                            r2, height, heightPropagation.scale, d0, d1,
                            currentR2, density, now, false
                    );
                }
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
    public @NotNull PlacementModifierType<ExtendXYZ> type() {
        return PlacementModifiersImpl.EXTEND_XZ;
    }
}
