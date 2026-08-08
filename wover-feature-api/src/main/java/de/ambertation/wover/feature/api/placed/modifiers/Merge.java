package de.ambertation.wover.feature.api.placed.modifiers;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

/**
 * A placement modifier that applies all of its sub-modifiers to the same
 * position, merges their result and emits all merged positions.
 */
public class Merge implements PlacementModifier {
    /**
     * Codec for this placement modifier.
     */
    public static final MapCodec<Merge> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
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
     * @param consumer         Receives every emitted position.
     */
    @Override
    public void modify(
            PlacementContext placementContext,
            RandomSource randomSource,
            BlockPos blockPos,
            Consumer<BlockPos> consumer
    ) {
        for (PlacementModifier p : modifiers) {
            p.modify(placementContext, randomSource, blockPos, consumer);
        }
    }

    /**
     * The {@link MapCodec} that serializes this placement modifier. 26.3 replaced the
     * {@code PlacementModifierType} indirection with the {@link MapCodec} itself.
     *
     * @return the codec of this placement modifier
     */
    @Override
    public @NotNull MapCodec<Merge> codec() {
        return CODEC;
    }
}
