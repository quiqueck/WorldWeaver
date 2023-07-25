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

public class ForAll extends PlacementModifier {
    public static final Codec<ForAll> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    ExtraCodecs.nonEmptyList(PlacementModifier.CODEC.listOf())
                               .fieldOf("modifiers")
                               .forGetter(a -> a.modifiers)
            )
            .apply(instance, ForAll::new));

    private final List<PlacementModifier> modifiers;

    public ForAll(List<PlacementModifier> modifiers) {
        this.modifiers = modifiers;
    }

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

    @Override
    public @NotNull PlacementModifierType<?> type() {
        return PlacementModifiersImpl.FOR_ALL;
    }
}
