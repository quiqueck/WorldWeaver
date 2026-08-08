package de.ambertation.wover.feature.api.placed.modifiers;


import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

/**
 * Moves the input position by a given offset.
 */
public class Offset implements PlacementModifier {
    private static final Map<Direction, Offset> DIRECTIONS = Maps.newHashMap();
    /**
     * Codec for this placement modifier.
     */
    public static final MapCodec<Offset> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
            .group(
                    Vec3i.CODEC
                            .fieldOf("blocks")
                            .forGetter(cfg -> cfg.offset)
            )
            .apply(instance, Offset::new));

    private final Vec3i offset;

    /**
     * Creates a new instance.
     *
     * @param offset The offset to apply
     */
    public Offset(Vec3i offset) {
        this.offset = offset;
    }

    /**
     * Creates a new instance that will move the input position one Block in the given direction
     *
     * @param dir The direction to move in
     * @return A new instance
     */
    public static Offset inDirection(Direction dir) {
        return DIRECTIONS.get(dir);
    }

    /**
     * Calculates the positions that this placement modifier will emit.
     *
     * @param placementContext The placement context.
     * @param randomSource     The random source.
     * @param blockPos         The input position.
     * @param consumer         Receives every emitted position.
     */
    @Override
    public void modify(
            PlacementContext placementContext,
            RandomSource randomSource,
            BlockPos blockPos,
            Consumer<BlockPos> consumer
    ) {
        consumer.accept(blockPos.offset(offset));
    }

    /**
     * The {@link MapCodec} that serializes this placement modifier. 26.3 replaced the
     * {@code PlacementModifierType} indirection with the {@link MapCodec} itself.
     *
     * @return the codec of this placement modifier
     */
    @Override
    public @NotNull MapCodec<Offset> codec() {
        return CODEC;
    }

    static {
        for (Direction d : Direction.values())
            DIRECTIONS.put(d, new Offset(d.getUnitVec3i()));
    }
}
