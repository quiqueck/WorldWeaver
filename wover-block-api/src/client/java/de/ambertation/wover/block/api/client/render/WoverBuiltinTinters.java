package de.ambertation.wover.block.api.client.render;

import de.ambertation.wover.block.api.render.TinterKeys;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;
import java.util.Set;

/**
 * Registers the {@link TintSourceFactory} for every key in {@link TinterKeys}. Invoked from wover's
 * {@code wover.client.traits} entrypoint, so the built-in catalogue is present in both the client and the
 * datagen launch.
 */
@Environment(EnvType.CLIENT)
public final class WoverBuiltinTinters {
    private static final int ALPHA = 0xFF000000;

    private WoverBuiltinTinters() {
    }

    /** Registers every built-in tint shape. Idempotent - later registrations replace earlier ones. */
    public static void register() {
        ClientTinterRegistry.register(TinterKeys.CONST_COLOR, (color, block) -> state -> color);

        ClientTinterRegistry.register(TinterKeys.GRADIENT, (gradient, block) -> new BlockTintSource() {
            @Override
            public int color(BlockState state) {
                final float delta = (float) state.getValue(gradient.property()) / gradient.maxValue();
                return lerpColor(delta, gradient.fromColor(), gradient.toColor());
            }

            @Override
            public Set<Property<?>> relevantProperties() {
                return Set.of(gradient.property());
            }
        });

        ClientTinterRegistry.register(TinterKeys.PALETTE_CYCLE, (palette, block) -> new BlockTintSource() {
            @Override
            public int color(BlockState state) {
                // The item model bakes whatever this returns, and a palette has no single "right" colour - use
                // the origin, which is what the pre-1.21.4 null-position item colour path produced.
                return colorInWorld(state, null, BlockPos.ZERO);
            }

            @Override
            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                if (pos == null) pos = BlockPos.ZERO;

                final List<Integer> colors = palette.colors();
                final long scalar = switch (palette.mode()) {
                    case SUM_XYZ -> (long) pos.getX() + (long) pos.getY() + (long) pos.getZ();
                    case HASH_XZ_PLUS_Y -> (hash(pos.getX(), pos.getZ()) & 63) + pos.getY();
                };

                double delta = scalar * 0.1;
                int index = Mth.floor(delta);
                delta -= index;

                final int a = Math.floorMod(index, colors.size());
                final int b = Math.floorMod(index + 1, colors.size());
                final int color = lerpColor((float) delta, colors.get(a), colors.get(b));

                if (palette.minSaturation() <= 0) return color;
                return withMinSaturation(color, palette.minSaturation());
            }
        });
    }

    /** Per-channel linear interpolation between two packed ARGB colours, opaque result. */
    private static int lerpColor(float delta, int from, int to) {
        final int r = Mth.floor(Mth.lerp(delta, (from >> 16) & 255, (to >> 16) & 255));
        final int g = Mth.floor(Mth.lerp(delta, (from >> 8) & 255, (to >> 8) & 255));
        final int b = Mth.floor(Mth.lerp(delta, from & 255, to & 255));
        return ALPHA | (r << 16) | (g << 8) | b;
    }

    /**
     * Lifts a colour's HSB saturation to at least {@code minSaturation}, leaving hue and brightness alone.
     * <p>
     * This is the standard {@code java.awt.Color} RGB/HSB conversion, reimplemented rather than imported:
     * touching {@code java.awt} from a mod can spin up AWT on some platforms, and this is pure arithmetic.
     */
    private static int withMinSaturation(int color, float minSaturation) {
        final int r = (color >> 16) & 255;
        final int g = (color >> 8) & 255;
        final int b = color & 255;

        int cmax = Math.max(r, Math.max(g, b));
        int cmin = Math.min(r, Math.min(g, b));

        final float brightness = cmax / 255.0F;
        final float saturation = cmax != 0 ? ((float) (cmax - cmin)) / ((float) cmax) : 0;
        float hue;
        if (saturation == 0) {
            hue = 0;
        } else {
            final float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
            final float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
            final float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
            if (r == cmax) hue = bluec - greenc;
            else if (g == cmax) hue = 2.0F + redc - bluec;
            else hue = 4.0F + greenc - redc;
            hue = hue / 6.0F;
            if (hue < 0) hue = hue + 1.0F;
        }

        return hsbToRgb(hue, Math.max(minSaturation, saturation), brightness);
    }

    private static int hsbToRgb(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0F + 0.5F);
        } else {
            final float h = (hue - (float) Math.floor(hue)) * 6.0F;
            final float f = h - (float) Math.floor(h);
            final float p = brightness * (1.0F - saturation);
            final float q = brightness * (1.0F - saturation * f);
            final float t = brightness * (1.0F - (saturation * (1.0F - f)));
            switch ((int) h) {
                case 0 -> {
                    r = (int) (brightness * 255.0F + 0.5F);
                    g = (int) (t * 255.0F + 0.5F);
                    b = (int) (p * 255.0F + 0.5F);
                }
                case 1 -> {
                    r = (int) (q * 255.0F + 0.5F);
                    g = (int) (brightness * 255.0F + 0.5F);
                    b = (int) (p * 255.0F + 0.5F);
                }
                case 2 -> {
                    r = (int) (p * 255.0F + 0.5F);
                    g = (int) (brightness * 255.0F + 0.5F);
                    b = (int) (t * 255.0F + 0.5F);
                }
                case 3 -> {
                    r = (int) (p * 255.0F + 0.5F);
                    g = (int) (q * 255.0F + 0.5F);
                    b = (int) (brightness * 255.0F + 0.5F);
                }
                case 4 -> {
                    r = (int) (t * 255.0F + 0.5F);
                    g = (int) (p * 255.0F + 0.5F);
                    b = (int) (brightness * 255.0F + 0.5F);
                }
                case 5 -> {
                    r = (int) (brightness * 255.0F + 0.5F);
                    g = (int) (p * 255.0F + 0.5F);
                    b = (int) (q * 255.0F + 0.5F);
                }
            }
        }
        return ALPHA | (r << 16) | (g << 8) | b;
    }

    /** The xxHash-style position hash the pre-trait flower tint used; kept bit-identical so colours do not shift. */
    private static int hash(int x, int z) {
        int h = x * 374761393 + z * 668265263;
        h = (h ^ (h >> 13)) * 1274126177;
        return h ^ (h >> 16);
    }
}
