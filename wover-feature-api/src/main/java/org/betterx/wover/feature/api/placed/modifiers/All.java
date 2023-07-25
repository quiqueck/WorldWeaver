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

public class All extends PlacementModifier {
    private static final All INSTANCE = new All();
    public static final Codec<All> CODEC = Codec.unit(All::new);

    @Override
    public @NotNull Stream<BlockPos> getPositions(
            PlacementContext placementContext,
            RandomSource randomSource,
            BlockPos blockPos
    ) {
        return IntStream.range(0, 16 * 16 - 1).mapToObj(i -> blockPos.offset(i & 0xF, 0, i >> 4));
    }

    public static PlacementModifier simple() {
        return INSTANCE;
    }

    @Override
    public @NotNull PlacementModifierType<?> type() {
        return PlacementModifiersImpl.ALL;
    }
}
