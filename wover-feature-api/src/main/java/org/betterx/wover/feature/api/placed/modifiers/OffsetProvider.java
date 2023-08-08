package org.betterx.wover.feature.api.placed.modifiers;

import org.betterx.wover.feature.impl.placed.modifiers.PlacementModifiersImpl;
import org.betterx.wover.math.api.valueproviders.Vec3iProvider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

/**
 * Moves the input position by using a given offset provider.
 */
public class OffsetProvider extends PlacementModifier {
    /**
     * Codec for this placement modifier.
     */
    public static final Codec<OffsetProvider> CODEC = RecordCodecBuilder.create((instance) -> instance
            .group(
                    Vec3iProvider.codec(-16, 16)
                                 .fieldOf("offset")
                                 .forGetter(cfg -> cfg.offset)
            )
            .apply(instance, OffsetProvider::new));

    private final Vec3iProvider offset;


    /**
     * Creates a new instance.
     *
     * @param offset The offset provider to apply
     */
    public OffsetProvider(Vec3iProvider offset) {
        this.offset = offset;
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
        return Stream.of(blockPos.offset(offset.sample(randomSource)));
    }

    /**
     * Gets the type of this placement modifier.
     *
     * @return The type.
     */
    @Override
    public @NotNull PlacementModifierType<?> type() {
        return PlacementModifiersImpl.OFFSET_PROVIDER;
    }
}
