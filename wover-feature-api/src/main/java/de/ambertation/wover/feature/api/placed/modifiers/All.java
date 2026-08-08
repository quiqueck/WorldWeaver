package de.ambertation.wover.feature.api.placed.modifiers;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

/**
 * Offsets the input position between 0 and 15 in the xz-Plane and generates a new
 * Position for each generated position.
 * <p>
 * The minimum position that is emitted is the input position, the largest position is
 * the input offset by {@code (15, 0, 15)}.
 */
public class All implements PlacementModifier {
    /**
     * The instance of this placement modifier.
     */
    private static final All INSTANCE = new All();
    /**
     * The codec for this placement modifier.
     */
    public static final MapCodec<All> CODEC = MapCodec.unit(All::new);

    /**
     * Emits all positions that are offset by 0 to 15 in the xz-Plane.
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
        for (int i = 0; i < 16 * 16 - 1; i++) {
            consumer.accept(blockPos.offset(i & 0xF, 0, i >> 4));
        }
    }

    /**
     * Returns the instance of this placement modifier.
     *
     * @return the instance of this placement modifier.
     */
    public static PlacementModifier simple() {
        return INSTANCE;
    }

    /**
     * The {@link MapCodec} that serializes this placement modifier. 26.3 replaced the
     * {@code PlacementModifierType} indirection with the {@link MapCodec} itself.
     *
     * @return the codec of this placement modifier
     */
    @Override
    public @NotNull MapCodec<All> codec() {
        return CODEC;
    }
}
