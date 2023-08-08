package org.betterx.wover.feature.api.placed.modifiers;

import org.betterx.wover.feature.impl.placed.modifiers.PlacementModifiersImpl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.List;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

/**
 * A placement modifier that applies all of its sub-modifiers to the same
 * position, merges their result and emits all merged positions.
 */
public class Merge extends PlacementModifier {
    /**
     * Codec for this placement modifier.
     */
    public static final Codec<Merge> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    ExtraCodecs.nonEmptyList(PlacementModifier.CODEC.listOf())
                               .fieldOf("modifiers")
                               .forGetter(a -> a.modifiers)
            )
            .apply(instance, Merge::new));

    private final List<PlacementModifier> modifiers;

    /**
     * Constructs a new instance.
     *
     * @param subModifiers The sub-modifiers to apply.
     */
    public Merge(List<PlacementModifier> subModifiers) {
        this.modifiers = subModifiers;
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
        Stream.Builder<BlockPos> stream = Stream.builder();
        for (PlacementModifier p : modifiers) {
            p.getPositions(placementContext, randomSource, blockPos).forEach(stream::add);
        }
        return stream.build();
    }

    /**
     * Gets the type of this placement modifier.
     *
     * @return The type
     */
    @Override
    public @NotNull PlacementModifierType<?> type() {
        return PlacementModifiersImpl.FOR_ALL;
    }
}
