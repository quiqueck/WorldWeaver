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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

/**
 * Re-emits the input positions offset to all positions in a 16x16 stencil. This will generate a
 * pseudo random distribution.
 */
public class Stencil extends PlacementModifier {
    /**
     * Codec for this placement modifier.
     */
    public static final Codec<Stencil> CODEC;
    private static final Boolean[] STENCIL;
    private static final Stencil DEFAULT;
    private static final Stencil DEFAULT4;
    private final List<Boolean> stencil;
    private final int selectOneIn;

    private static List<Boolean> convert(Boolean[] s) {
        return Arrays.stream(s).toList();
    }

    /**
     * Creates a new instance.
     *
     * @param stencil     The stencil to use. A stencil is a boolean array with 256 entries. All positions that
     *                    are true in the stencil will be emitted. The stencil is read as a
     * @param selectOneIn The chance that a position in the stencil is emitted. For example, if set to 5, there is only
     *                    a 20% chance for a position to be emitted.
     */
    public Stencil(Boolean[] stencil, int selectOneIn) {
        this(convert(stencil), selectOneIn);
    }

    private Stencil(List<Boolean> stencil, int selectOneIn) {
        if (stencil.size() != 16 * 16) {
            throw new IllegalArgumentException("Stencil must be 16x16");
        }
        this.stencil = stencil;
        this.selectOneIn = selectOneIn;
    }

    /**
     * Returns the default modifier using the builtin stencil and a selection chance of 100%
     *
     * @return The default modifier.
     */
    public static Stencil all() {
        return DEFAULT;
    }

    /**
     * Returns a modifier using the builtin stencil and a selection chance of 25%
     *
     * @return The default modifier.
     */
    public static Stencil oneIn4() {
        return DEFAULT4;
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
        List<BlockPos> pos = new ArrayList<>(16 * 16);
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                if (stencil.get(x << 4 | y)) {
                    if (selectOneIn > 1 && randomSource.nextInt(selectOneIn) != 0) {
                        continue;
                    }

                    pos.add(blockPos.offset(x, 0, y));
                }
            }
        }

        return pos.stream();
    }

    /**
     * Gets the type of this placement modifier.
     *
     * @return The type of this placement modifier.
     */
    @Override
    public @NotNull PlacementModifierType<?> type() {
        return PlacementModifiersImpl.STENCIL;
    }

    static {
        STENCIL = new Boolean[]{
                false, true, false, false, false, false, false, true,
                false, false, false, false, true, true, false, false,
                false, false, false, false, false, false, false, true,
                false, false, false, false, true, true, false, false,
                true, true, true, false, false, false, true, true,
                false, false, false, true, false, false, true, true,
                true, false, false, true, true, true, true, false,
                true, true, true, true, false, false, false, true,
                true, false, false, true, true, true, false, false,
                false, false, false, true, false, false, false, false,
                true, false, false, false, true, false, false, false,
                false, false, false, false, true, false, false, false,
                false, false, false, false, true, false, false, false,
                false, false, false, false, true, true, true, true,
                true, false, false, false, true, true, true, true,
                false, false, false, true, true, false, true, true,
                true, true, true, true, true, false, false, true,
                true, false, true, true, false, false, false, true,
                false, false, true, false, false, false, false, false,
                true, true, true, false, false, false, false, true,
                false, false, true, false, false, false, false, false,
                false, true, false, false, false, false, true, false,
                false, false, true, true, false, false, false, false,
                false, true, false, false, false, false, true, false,
                true, false, false, false, true, false, false, false,
                false, true, false, false, false, false, true, false,
                true, true, false, false, true, false, false, false,
                true, true, true, true, true, true, false, true,
                false, true, true, true, true, true, true, true,
                false, false, false, false, true, false, false, false,
                false, true, true, false, false, false, true, false,
                false, false, false, false, true, true, false, false
        };

        DEFAULT = new Stencil(STENCIL, 1);
        DEFAULT4 = new Stencil(STENCIL, 4);
        CODEC = RecordCodecBuilder.create((instance) -> instance
                .group(
                        ExtraCodecs.nonEmptyList(Codec.BOOL.listOf())
                                   .fieldOf("structures")
                                   .orElse(convert(STENCIL))
                                   .forGetter((Stencil a) -> a.stencil),
                        Codec.INT
                                .fieldOf("one_in")
                                .orElse(1)
                                .forGetter((Stencil a) -> a.selectOneIn)
                )
                .apply(instance, Stencil::new)
        );
    }
}
