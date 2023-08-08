package org.betterx.wover.feature.api.placed.modifiers;

import org.betterx.wover.block.api.BlockHelper;
import org.betterx.wover.feature.impl.placed.modifiers.PlacementModifiersImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Searches for all layers from the maximum height down to the minimum
 * height at the input xz-coordinate and emits one resulting position on top or under
 * every layer. All emitted positions will have the same xz-coordinate,
 * but different y-coordinates.
 * <p>
 * Minimum and maximum height are clamped to the build height of the world.
 */
public class EveryLayer
        extends PlacementModifier {
    private static final EveryLayer INSTANCE = new EveryLayer(Integer.MIN_VALUE, Integer.MAX_VALUE, true);
    private static final EveryLayer INSTANCE_MIN_4 = new EveryLayer(4, Integer.MAX_VALUE, true);
    private static final EveryLayer UNDER_INSTANCE = new EveryLayer(Integer.MIN_VALUE, Integer.MAX_VALUE, false);
    private static final EveryLayer UNDER_INSTANCE_MIN_4 = new EveryLayer(4, Integer.MAX_VALUE, false);
    /**
     * The codec for this placement modifier.
     */
    public static final Codec<EveryLayer> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    Codec.INT.optionalFieldOf("min", Integer.MIN_VALUE).forGetter(o -> o.minHeight),
                    Codec.INT.optionalFieldOf("max", Integer.MAX_VALUE).forGetter(o -> o.maxHeight),
                    Codec.BOOL.optionalFieldOf("top", true).forGetter(o -> o.onTop)
            ).apply(instance, EveryLayer::new));

    /**
     * For internal use only. Supposed to help migration of old bclib json files to wover
     */
    @ApiStatus.Internal
    public static final Codec<EveryLayer> CODEC_LEGACY_UNDER = RecordCodecBuilder.create(instance -> instance
            .group(
                    Codec.INT.optionalFieldOf("min", Integer.MIN_VALUE).forGetter(o -> o.minHeight),
                    Codec.INT.optionalFieldOf("max", Integer.MAX_VALUE).forGetter(o -> o.maxHeight),
                    Codec.BOOL.optionalFieldOf("top", false).forGetter(o -> o.onTop)
            ).apply(instance, EveryLayer::new));


    private final int minHeight;
    private final int maxHeight;
    private final boolean onTop;

    private EveryLayer(int minHeight, int maxHeight, boolean onTop) {
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.onTop = onTop;
    }

    /**
     * The returned instance will generate one position on top of each layer
     * found at the input xz-coordinate. The check is performed from the height
     * found in the {@link Heightmap.Types#MOTION_BLOCKING} down to the
     * minimum build height.
     *
     * @return a new instance of this placement modifier.
     */
    public static EveryLayer on() {
        return INSTANCE;
    }

    /**
     * The returned instance will generate one position on top of each layer
     * found at the input xz-coordinate. The check is performed from the height
     * found in the {@link Heightmap.Types#MOTION_BLOCKING} down to y=4
     *
     * @return a new instance of this placement modifier.
     */
    public static EveryLayer onTopMin4() {
        return INSTANCE_MIN_4;
    }

    /**
     * The returned instance will generate one position on top of each layer
     * found at the input xz-coordinate. The check is performed from {@code maxHeight}
     * (clamped to the height found in height found in the {@link Heightmap.Types#MOTION_BLOCKING})
     * down to {@code minHeight} (clamped to the minimum build height)
     *
     * @param minHeight the minimum height to check
     * @param maxHeight the maximum height to check
     * @return a new instance of this placement modifier.
     */
    public static EveryLayer onTopInRange(int minHeight, int maxHeight) {
        return new EveryLayer(minHeight, maxHeight, true);
    }

    /**
     * The returned instance will generate one position underneath each layer
     * found at the input xz-coordinate. The check is performed from the height
     * found in the {@link Heightmap.Types#MOTION_BLOCKING} down to the
     * minimum build height.
     *
     * @return a new instance of this placement modifier.
     */
    public static EveryLayer underneath() {
        return UNDER_INSTANCE;
    }

    /**
     * The returned instance will generate one position underneath each layer
     * found at the input xz-coordinate. The check is performed from the height
     * found in the {@link Heightmap.Types#MOTION_BLOCKING} down to y=4
     *
     * @return a new instance of this placement modifier.
     */
    public static EveryLayer underneathMin4() {
        return UNDER_INSTANCE_MIN_4;
    }

    /**
     * The returned instance will generate one position underneath each layer
     * found at the input xz-coordinate. The check is performed from {@code maxHeight}
     * (clamped to the height found in height found in the {@link Heightmap.Types#MOTION_BLOCKING})
     * down to {@code minHeight} (clamped to the minimum build height)
     *
     * @param minHeight the minimum height to check
     * @param maxHeight the maximum height to check
     * @return a new instance of this placement modifier.
     */
    public static EveryLayer underneathInRange(int minHeight, int maxHeight) {
        return new EveryLayer(minHeight, maxHeight, false);
    }

    /**
     * Calculates the positions that this placement modifier will emit.
     *
     * @param ctx    The placement context.
     * @param random The random source.
     * @param pos    The input position.
     * @return The stream of new positions.
     */
    @Override
    public @NotNull Stream<BlockPos> getPositions(
            PlacementContext ctx,
            RandomSource random,
            BlockPos pos
    ) {

        Stream.Builder<BlockPos> builder = Stream.builder();

        final int z = pos.getZ();
        final int x = pos.getX();
        final int levelHeight = ctx.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
        final int minLevelHeight = ctx.getMinBuildHeight();
        int y = Math.min(levelHeight, this.maxHeight);
        final int minHeight = Math.max(minLevelHeight, this.minHeight);

        int layerY;
        do {
            layerY = onTop
                    ? findOnGroundYPosition(ctx, x, y, z, minHeight)
                    : findUnderGroundYPosition(ctx, x, y, z, minHeight);
            if (layerY != Integer.MAX_VALUE) {
                builder.add(new BlockPos(x, layerY, z));
                y = layerY - 1;
            }

        } while (layerY != Integer.MAX_VALUE);
        return builder.build();
    }

    /**
     * Gets the type of this placement modifier.
     *
     * @return The type of this placement modifier.
     */
    @Override
    public @NotNull PlacementModifierType<EveryLayer> type() {
        return PlacementModifiersImpl.EVERY_LAYER;
    }

    private static int findOnGroundYPosition(PlacementContext ctx, int x, int startY, int z, int minHeight) {
        BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos(x, startY, z);
        BlockState nowState = ctx.getBlockState(mPos);
        for (int y = startY; y >= minHeight + 1; --y) {
            mPos.setY(y - 1);
            BlockState belowState = ctx.getBlockState(mPos);
            if (BlockHelper.isTerrain(belowState) && BlockHelper.isFreeOrFluid(nowState) && !belowState.is(Blocks.BEDROCK)) {
                return mPos.getY() + 1;
            }
            nowState = belowState;
        }
        return Integer.MAX_VALUE;
    }

    private static int findUnderGroundYPosition(PlacementContext ctx, int x, int startY, int z, int minHeight) {
        BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos(x, startY, z);
        BlockState nowState = ctx.getBlockState(mPos);
        for (int y = startY; y >= minHeight + 1; --y) {
            mPos.setY(y - 1);
            BlockState belowState = ctx.getBlockState(mPos);
            if (BlockHelper.isTerrain(nowState) && BlockHelper.isFreeOrFluid(belowState) && !nowState.is(Blocks.BEDROCK)) {
                return mPos.getY();
            }
            nowState = belowState;
        }
        return Integer.MAX_VALUE;
    }
}
