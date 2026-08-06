package de.ambertation.wover.block.api.render;

import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.entrypoint.LibWoverBlock;

import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.List;

/**
 * The catalogue of ready-made {@link TinterKey}s wover ships, together with their payload records. A mod that
 * needs a colour none of these describe registers a {@code TinterKey} of its own and binds a
 * {@code TintSourceFactory} for it through the {@code wover.client.traits} entrypoint.
 * <p>
 * Every payload here is common-safe (ints, a vanilla {@link IntegerProperty}, a {@code List<Integer>}), so
 * bindings can be attached from common block-creation code.
 */
public class TinterKeys {
    /**
     * A single fixed colour for the whole block, ignoring state and position. Payload: the packed ARGB colour.
     * <p>
     * Use this for anything whose colour is decided once at definition time - including colours that are
     * <em>computed</em> then (a dye colour, a saturation-boosted variant of one), since baking the result into
     * the payload is cheaper than recomputing it per query.
     */
    public static final TinterKey<Integer> CONST_COLOR = key("const_color");

    /**
     * A linear colour ramp across an {@link IntegerProperty}. Payload: {@link Gradient}.
     */
    public static final TinterKey<Gradient> GRADIENT = key("gradient");

    /**
     * A small palette cycled by block position, as used for crystal/flower blocks that shimmer across a region
     * rather than varying per state. Payload: {@link PaletteCycle}.
     */
    public static final TinterKey<PaletteCycle> PALETTE_CYCLE = key("palette_cycle");

    private TinterKeys() {
    }

    private static <P> TinterKey<P> key(String path) {
        return new TinterKey<>(BlockTraitKey.ofUnique(LibWoverBlock.C, "tint/" + path));
    }

    /**
     * Payload for {@link #GRADIENT}: lerps from {@code fromColor} to {@code toColor} as {@code property} runs
     * from {@code 0} to {@code maxValue}.
     *
     * @param property  the state property driving the ramp
     * @param maxValue  the property value that maps to {@code toColor}
     * @param fromColor packed ARGB colour at property value {@code 0}
     * @param toColor   packed ARGB colour at property value {@code maxValue}
     */
    public record Gradient(IntegerProperty property, int maxValue, int fromColor, int toColor) {
    }

    /**
     * Payload for {@link #PALETTE_CYCLE}: walks {@code colors} by a position-derived index, lerping between
     * neighbouring entries, then optionally lifts the result's saturation to a floor.
     *
     * @param colors        the palette, cycled modulo its own size
     * @param mode          how a block position maps to a position in the palette
     * @param minSaturation HSB saturation floor applied to the result, or {@code 0} to leave it alone
     */
    public record PaletteCycle(List<Integer> colors, IndexMode mode, float minSaturation) {
        /** How a block position is reduced to a scalar index into the palette. */
        public enum IndexMode {
            /** {@code x + y + z} - a diagonal sweep, so neighbouring blocks differ. */
            SUM_XYZ,
            /** A hash of {@code x}/{@code z} plus {@code y} - scatters horizontally, ramps vertically. */
            HASH_XZ_PLUS_Y
        }
    }
}
