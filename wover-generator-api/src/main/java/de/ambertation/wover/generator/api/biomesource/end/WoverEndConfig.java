package de.ambertation.wover.generator.api.biomesource.end;

import de.ambertation.wover.common.generator.api.biomesource.BiomeSourceConfig;
import de.ambertation.wover.core.api.IntegrationCore;
import de.ambertation.wover.generator.api.map.MapBuilderFunction;
import de.ambertation.wover.generator.impl.biomesource.end.WoverEndBiomeSource;
import de.ambertation.wover.generator.impl.map.hex.HexBiomeMap;
import de.ambertation.wover.generator.impl.map.square.SquareBiomeMap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * The {@link BiomeSourceConfig} of {@link WoverEndBiomeSource}, matching the {@code config} field of the
 * {@code wover:end_biome_source} {@code biome_source} type.
 * <p>
 * Controls the {@link EndBiomeMapType map algorithm} and {@link EndBiomeGeneratorType placement algorithm}
 * used, and the size of the four biome "rings" the End biome source places: center island, land (highlands
 * and midlands), void (small islands) and barrens. A handful of presets matching the behavior of past
 * BCLib/WoVer versions are provided as constants ({@link #VANILLA}, {@link #MINECRAFT_17}, ...,
 * {@link #DEFAULT}) — use {@link #WoverEndConfig(EndBiomeMapType, EndBiomeGeneratorType, boolean, int, int,
 * int, int, int, int, int, int) the constructor} to build a fully custom configuration.
 */
public class WoverEndConfig implements BiomeSourceConfig<WoverEndBiomeSource> {
    /**
     * The default {@link #caveBiomesSize} (in blocks). Was {@code 32} - deliberately much smaller than the
     * other biome-ring size defaults, preserving the scale of BetterEnd's legacy cave-biome map, which fed
     * {@code 32} into the same {@code HexBiomeMap} size slot the End rings feed {@code 256} into. That ratio
     * turned out too fine-grained for the vertical cave-biome band this decider actually drives (unlike the
     * legacy 2D cave map, a single cave can now span many chunks of open space and cross several biome
     * cells while doing it): players reported most caves changing biome roughly every other chunk instead
     * of staying one biome for a whole cave system. Raised 4x to give cave-biome regions a size closer to a
     * handful of chunks, still well below the 256 land-biome scale (cave biomes are meant to stay more
     * varied over distance than land), but no longer switching within a single cavern.
     */
    public static final int DEFAULT_CAVE_BIOMES_SIZE = 128;
    /**
     * The default {@link #caveBiomesTopY}: the top of the vertical cave-biome band.
     * <p>
     * Was {@code 32} (the legacy pre-WoVer value) until the port to real per-column carvers exposed a
     * mismatch with the island generator: {@code GeneratorConfig}'s {@code LayerOptions} center even the
     * smallest islands at Y&ge;40 (big islands Y60-80, medium Y50-90, small Y40-100, on a 128-tall noise
     * column), so a band topping out at 32 (&plusmn; jitter, i.e. 24-40) sat almost entirely below where
     * island rock actually is, leaving caverns with a ceiling but no real floor. That was first raised to
     * 56 (&plusmn; jitter 48-64), which fixed the missing floors but overshot the other way: with small
     * islands centered as low as Y40, a band reaching up to 64 pushed into or above their surface in many
     * places, breaching the top with only a block or two of roof left. 48 (&plusmn; jitter 40-56) pulls the
     * top back below that breach zone while staying well above the original 24-40 band.
     */
    public static final int DEFAULT_CAVE_BIOMES_TOP_Y = 48;
    /**
     * The default {@link #caveBiomesTopJitter}: the noise amplitude applied to the band top.
     */
    public static final int DEFAULT_CAVE_BIOMES_TOP_JITTER = 8;

    /**
     * The behavior of vanilla's End (before WoVer/BCLib biome placement existed): a {@link HexBiomeMap} with
     * void biomes enabled and a small inner void radius.
     */
    public static final WoverEndConfig VANILLA = new WoverEndConfig(
            EndBiomeMapType.VANILLA,
            EndBiomeGeneratorType.VANILLA,
            true,
            4096,
            128,
            128,
            128,
            128,
            DEFAULT_CAVE_BIOMES_SIZE,
            DEFAULT_CAVE_BIOMES_TOP_Y,
            DEFAULT_CAVE_BIOMES_TOP_JITTER
    );
    /**
     * The behavior of BCLib 1.17: a {@link EndBiomeMapType#SQUARE} map with the {@link EndBiomeGeneratorType#PAULEVS}
     * placement algorithm and larger biome rings than {@link #VANILLA}.
     */
    public static final WoverEndConfig MINECRAFT_17 = new WoverEndConfig(
            EndBiomeMapType.SQUARE,
            EndBiomeGeneratorType.PAULEVS,
            true,
            VANILLA.innerVoidRadiusSquared * 16 * 16,
            256,
            256,
            256,
            256,
            DEFAULT_CAVE_BIOMES_SIZE,
            DEFAULT_CAVE_BIOMES_TOP_Y,
            DEFAULT_CAVE_BIOMES_TOP_JITTER
    );
    /**
     * The behavior of BCLib 1.18: a {@link EndBiomeMapType#HEX} map, otherwise matching {@link #MINECRAFT_17}
     * (or, if running alongside Nullscape, the {@link EndBiomeGeneratorType#VANILLA} algorithm without void
     * biomes).
     */
    public static final WoverEndConfig MINECRAFT_18 = new WoverEndConfig(
            EndBiomeMapType.HEX,
            IntegrationCore.RUNS_NULLSCAPE ? EndBiomeGeneratorType.VANILLA : EndBiomeGeneratorType.PAULEVS,
            IntegrationCore.RUNS_NULLSCAPE ? false : true,
            MINECRAFT_17.innerVoidRadiusSquared,
            MINECRAFT_17.centerBiomesSize,
            MINECRAFT_17.voidBiomesSize,
            MINECRAFT_17.landBiomesSize,
            MINECRAFT_17.barrensBiomesSize,
            MINECRAFT_17.caveBiomesSize,
            MINECRAFT_17.caveBiomesTopY,
            MINECRAFT_17.caveBiomesTopJitter
    );

    /**
     * A larger-biomes variant of {@link #MINECRAFT_18}.
     */
    public static final WoverEndConfig MINECRAFT_18_LARGE = new WoverEndConfig(
            EndBiomeMapType.HEX,
            IntegrationCore.RUNS_NULLSCAPE ? EndBiomeGeneratorType.VANILLA : EndBiomeGeneratorType.PAULEVS,
            IntegrationCore.RUNS_NULLSCAPE ? false : true,
            MINECRAFT_18.innerVoidRadiusSquared,
            MINECRAFT_18.centerBiomesSize,
            MINECRAFT_18.voidBiomesSize * 2,
            MINECRAFT_18.landBiomesSize * 4,
            MINECRAFT_18.barrensBiomesSize * 2,
            MINECRAFT_18.caveBiomesSize,
            MINECRAFT_18.caveBiomesTopY,
            MINECRAFT_18.caveBiomesTopJitter
    );

    /**
     * A variant of {@link #MINECRAFT_18} that always uses the {@link EndBiomeGeneratorType#PAULEVS}
     * placement algorithm with void biomes enabled, for the amplified world preset.
     */
    public static final WoverEndConfig MINECRAFT_18_AMPLIFIED = new WoverEndConfig(
            EndBiomeMapType.HEX,
            EndBiomeGeneratorType.PAULEVS,
            true,
            MINECRAFT_18.innerVoidRadiusSquared,
            MINECRAFT_18.centerBiomesSize,
            MINECRAFT_18.voidBiomesSize,
            MINECRAFT_18.landBiomesSize,
            MINECRAFT_18.barrensBiomesSize,
            MINECRAFT_18.caveBiomesSize,
            MINECRAFT_18.caveBiomesTopY,
            MINECRAFT_18.caveBiomesTopJitter
    );

    /**
     * The current default behavior: a {@link EndBiomeMapType#HEX} map with the vanilla
     * ({@link EndBiomeGeneratorType#VANILLA}) placement algorithm and {@link #MINECRAFT_17}'s biome ring
     * sizes.
     */
    public static final WoverEndConfig MINECRAFT_20 = new WoverEndConfig(
            EndBiomeMapType.HEX,
            EndBiomeGeneratorType.VANILLA,
            IntegrationCore.RUNS_NULLSCAPE ? false : true,
            MINECRAFT_17.innerVoidRadiusSquared,
            MINECRAFT_17.centerBiomesSize,
            MINECRAFT_17.voidBiomesSize,
            MINECRAFT_17.landBiomesSize,
            MINECRAFT_17.barrensBiomesSize,
            MINECRAFT_17.caveBiomesSize,
            MINECRAFT_17.caveBiomesTopY,
            MINECRAFT_17.caveBiomesTopJitter
    );

    /**
     * A larger-biomes variant of {@link #MINECRAFT_20}.
     */
    public static final WoverEndConfig MINECRAFT_20_LARGE = new WoverEndConfig(
            EndBiomeMapType.HEX,
            EndBiomeGeneratorType.VANILLA,
            IntegrationCore.RUNS_NULLSCAPE ? false : true,
            MINECRAFT_18.innerVoidRadiusSquared,
            MINECRAFT_18.centerBiomesSize,
            MINECRAFT_18.voidBiomesSize * 2,
            MINECRAFT_18.landBiomesSize * 4,
            MINECRAFT_18.barrensBiomesSize * 2,
            MINECRAFT_18.caveBiomesSize,
            MINECRAFT_18.caveBiomesTopY,
            MINECRAFT_18.caveBiomesTopJitter
    );

    /**
     * A variant of {@link #MINECRAFT_20} that always enables void biomes, for the amplified world preset.
     */
    public static final WoverEndConfig MINECRAFT_20_AMPLIFIED = new WoverEndConfig(
            EndBiomeMapType.HEX,
            EndBiomeGeneratorType.VANILLA,
            true,
            MINECRAFT_18.innerVoidRadiusSquared,
            MINECRAFT_18.centerBiomesSize,
            MINECRAFT_18.voidBiomesSize,
            MINECRAFT_18.landBiomesSize,
            MINECRAFT_18.barrensBiomesSize,
            MINECRAFT_18.caveBiomesSize,
            MINECRAFT_18.caveBiomesTopY,
            MINECRAFT_18.caveBiomesTopJitter
    );
    /**
     * The configuration used by {@link de.ambertation.wover.generator.api.preset.WorldPresets#WOVER_WORLD}.
     */
    public static final WoverEndConfig DEFAULT = MINECRAFT_20;

    /**
     * The {@link Codec} for this class, matching the {@code config} field of the
     * {@code wover:end_biome_source} {@code biome_source} type. Every field is optional and falls back to
     * {@link #DEFAULT}.
     */
    public static final Codec<WoverEndConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    EndBiomeMapType.CODEC
                            .fieldOf("map_type")
                            .orElse(DEFAULT.mapVersion)
                            .forGetter(o -> o.mapVersion),
                    EndBiomeGeneratorType.CODEC
                            .fieldOf("generator_version")
                            .orElse(DEFAULT.generatorVersion)
                            .forGetter(o -> o.generatorVersion),
                    Codec.BOOL
                            .fieldOf("with_void_biomes")
                            .orElse(DEFAULT.withVoidBiomes)
                            .forGetter(o -> o.withVoidBiomes),
                    Codec.INT
                            .fieldOf("inner_void_radius_squared")
                            .orElse(DEFAULT.innerVoidRadiusSquared)
                            .forGetter(o -> o.innerVoidRadiusSquared),
                    Codec.INT
                            .fieldOf("center_biomes_size")
                            .orElse(DEFAULT.centerBiomesSize)
                            .forGetter(o -> o.centerBiomesSize),
                    Codec.INT
                            .fieldOf("void_biomes_size")
                            .orElse(DEFAULT.voidBiomesSize)
                            .forGetter(o -> o.voidBiomesSize),
                    Codec.INT
                            .fieldOf("land_biomes_size")
                            .orElse(DEFAULT.landBiomesSize)
                            .forGetter(o -> o.landBiomesSize),
                    Codec.INT
                            .fieldOf("barrens_biomes_size")
                            .orElse(DEFAULT.barrensBiomesSize)
                            .forGetter(o -> o.barrensBiomesSize),
                    Codec.INT
                            .optionalFieldOf("cave_biomes_size", DEFAULT.caveBiomesSize)
                            .forGetter(o -> o.caveBiomesSize),
                    Codec.INT
                            .optionalFieldOf("cave_biomes_top_y", DEFAULT.caveBiomesTopY)
                            .forGetter(o -> o.caveBiomesTopY),
                    Codec.INT
                            .optionalFieldOf("cave_biomes_top_jitter", DEFAULT.caveBiomesTopJitter)
                            .forGetter(o -> o.caveBiomesTopJitter)
            )
            .apply(instance, WoverEndConfig::new));

    /**
     * Creates a new instance.
     * <p>
     * The four biome ring sizes and {@link #caveBiomesSize} are clamped to {@code [1, 8192]},
     * {@link #caveBiomesTopY} to {@code [0, 256]} and {@link #caveBiomesTopJitter} to {@code [0, 16]}.
     *
     * @param mapVersion             The map algorithm used to distribute biomes within each ring.
     * @param generatorVersion       The algorithm used to decide which ring a position belongs to.
     * @param withVoidBiomes         Whether small End islands (void biomes) generate at all.
     * @param innerVoidRadiusSquared The squared radius (in blocks) of the center island ring.
     * @param centerBiomesSize       The biome size of the center island ring.
     * @param voidBiomesSize         The biome size of the small-island (void) ring.
     * @param landBiomesSize         The biome size of the highlands/midlands ring.
     * @param barrensBiomesSize      The biome size of the barrens ring.
     * @param caveBiomesSize         The biome size of the cave-biome band (in blocks).
     * @param caveBiomesTopY         The top of the vertical cave-biome band ({@code 0} disables cave biomes).
     * @param caveBiomesTopJitter    The noise amplitude applied to the cave-biome band top.
     */
    public WoverEndConfig(
            @NotNull EndBiomeMapType mapVersion,
            @NotNull EndBiomeGeneratorType generatorVersion,
            boolean withVoidBiomes,
            int innerVoidRadiusSquared,
            int centerBiomesSize,
            int voidBiomesSize,
            int landBiomesSize,
            int barrensBiomesSize,
            int caveBiomesSize,
            int caveBiomesTopY,
            int caveBiomesTopJitter
    ) {
        this.mapVersion = mapVersion;
        this.generatorVersion = generatorVersion;
        this.withVoidBiomes = withVoidBiomes;
        this.innerVoidRadiusSquared = innerVoidRadiusSquared;
        this.barrensBiomesSize = Mth.clamp(barrensBiomesSize, 1, 8192);
        this.voidBiomesSize = Mth.clamp(voidBiomesSize, 1, 8192);
        this.centerBiomesSize = Mth.clamp(centerBiomesSize, 1, 8192);
        this.landBiomesSize = Mth.clamp(landBiomesSize, 1, 8192);
        this.caveBiomesSize = Mth.clamp(caveBiomesSize, 1, 8192);
        this.caveBiomesTopY = Mth.clamp(caveBiomesTopY, 0, 256);
        this.caveBiomesTopJitter = Mth.clamp(caveBiomesTopJitter, 0, 16);
    }

    /**
     * The algorithm used to distribute biomes spatially within a single biome ring, matching the
     * {@code map_type} field of the {@link #CODEC}.
     */
    public enum EndBiomeMapType implements StringRepresentable {
        /**
         * A hex-grid based map, matching vanilla's own End biome distribution.
         */
        VANILLA("vanilla", (seed, biomeSize, picker) -> new HexBiomeMap(seed, biomeSize, picker)),
        /**
         * A square-grid based map (BCLib 1.17 behavior).
         */
        SQUARE("square", (seed, biomeSize, picker) -> new SquareBiomeMap(seed, biomeSize, picker)),
        /**
         * A hex-grid based map (BCLib 1.18+ behavior).
         */
        HEX("hex", (seed, biomeSize, picker) -> new HexBiomeMap(seed, biomeSize, picker));

        /**
         * The {@link Codec} for this enum.
         */
        public static final Codec<EndBiomeMapType> CODEC = StringRepresentable.fromEnum(EndBiomeMapType::values);
        /**
         * The serialized name of this value.
         */
        public final String name;
        /**
         * The factory used to build the {@link de.ambertation.wover.generator.api.map.BiomeMap BiomeMap} for
         * this map type.
         */
        public final @NotNull MapBuilderFunction mapBuilder;

        EndBiomeMapType(String name, @NotNull MapBuilderFunction mapBuilder) {
            this.name = name;
            this.mapBuilder = mapBuilder;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * The algorithm used to decide which biome ring (center/highland/midland/void/barrens) a position
     * belongs to, matching the {@code generator_version} field of the {@link #CODEC}.
     */
    public enum EndBiomeGeneratorType implements StringRepresentable {
        /**
         * Vanilla's own erosion-based ring placement (see {@code WoverEndBiomeSource#getNoiseBiome}).
         */
        VANILLA("vanilla"),
        /**
         * BCLib's PaulEvs placement algorithm.
         */
        PAULEVS("paulevs");

        /**
         * The {@link Codec} for this enum.
         */
        public static final Codec<EndBiomeGeneratorType> CODEC = StringRepresentable.fromEnum(EndBiomeGeneratorType::values);
        /**
         * The serialized name of this value.
         */
        public final String name;

        EndBiomeGeneratorType(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }


    /**
     * The map algorithm used to distribute biomes within each ring.
     */
    public final @NotNull EndBiomeMapType mapVersion;
    /**
     * The algorithm used to decide which ring a position belongs to.
     */
    public final @NotNull EndBiomeGeneratorType generatorVersion;
    /**
     * Whether small End islands (void biomes) generate at all.
     */
    public final boolean withVoidBiomes;
    /**
     * The squared radius (in blocks) of the center island ring.
     */
    public final int innerVoidRadiusSquared;

    /**
     * The biome size of the small-island (void) ring.
     */
    public final int voidBiomesSize;
    /**
     * The biome size of the center island ring.
     */
    public final int centerBiomesSize;
    /**
     * The biome size of the highlands/midlands ring.
     */
    public final int landBiomesSize;
    /**
     * The biome size of the barrens ring.
     */
    public final int barrensBiomesSize;

    /**
     * The biome size of the cave-biome band, in blocks (like the other {@code *BiomesSize} fields; the
     * config UI shows this value divided by {@code 16}). Clamped to {@code [1, 8192]}. Defaults to
     * {@link #DEFAULT_CAVE_BIOMES_SIZE} ({@code 32}), preserving the legacy cave-biome scale.
     */
    public final int caveBiomesSize;
    /**
     * The top of the vertical cave-biome band (in blocks). A value of {@code 0} disables cave biomes.
     * Clamped to {@code [0, 256]}. Defaults to {@link #DEFAULT_CAVE_BIOMES_TOP_Y} ({@code 32}).
     */
    public final int caveBiomesTopY;
    /**
     * The noise amplitude (in blocks) applied to the {@link #caveBiomesTopY cave-biome band top}. Clamped to
     * {@code [0, 16]}. Defaults to {@link #DEFAULT_CAVE_BIOMES_TOP_JITTER} ({@code 8}).
     */
    public final int caveBiomesTopJitter;

    @Override
    public String toString() {
        return "EndConfig{" +
                "mapVersion=" + mapVersion +
                ", generatorVersion=" + generatorVersion +
                ", withVoidBiomes=" + withVoidBiomes +
                ", innerVoidRadius=" + (int) Math.sqrt(innerVoidRadiusSquared) +
                ", voidBiomesSize=" + voidBiomesSize / 16 +
                ", centerBiomesSize=" + centerBiomesSize / 16 +
                ", landBiomesSize=" + landBiomesSize / 16 +
                ", barrensBiomesSize=" + barrensBiomesSize / 16 +
                ", caveBiomesSize=" + caveBiomesSize / 16 +
                ", caveBiomesTopY=" + caveBiomesTopY +
                ", caveBiomesTopJitter=" + caveBiomesTopJitter +
                '}';
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns {@code true} if {@code input} is also a {@link WoverEndConfig} using the same
     * {@link #mapVersion}, {@link #generatorVersion} and {@link #withVoidBiomes} setting — the biome ring
     * sizes may differ without requiring a chunk repair.
     */
    @Override
    public boolean couldSetWithoutRepair(BiomeSourceConfig<?> input) {
        if (input instanceof WoverEndConfig cfg) {
            return withVoidBiomes == cfg.withVoidBiomes && mapVersion == cfg.mapVersion && generatorVersion == cfg.generatorVersion;
        }
        return false;
    }

    @Override
    public boolean sameConfig(BiomeSourceConfig<?> input) {
        return this.equals(input);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WoverEndConfig that = (WoverEndConfig) o;
        return withVoidBiomes == that.withVoidBiomes && innerVoidRadiusSquared == that.innerVoidRadiusSquared && voidBiomesSize == that.voidBiomesSize && centerBiomesSize == that.centerBiomesSize && landBiomesSize == that.landBiomesSize && barrensBiomesSize == that.barrensBiomesSize && caveBiomesSize == that.caveBiomesSize && caveBiomesTopY == that.caveBiomesTopY && caveBiomesTopJitter == that.caveBiomesTopJitter && mapVersion == that.mapVersion && generatorVersion == that.generatorVersion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                mapVersion,
                generatorVersion,
                withVoidBiomes,
                innerVoidRadiusSquared,
                voidBiomesSize,
                centerBiomesSize,
                landBiomesSize,
                barrensBiomesSize,
                caveBiomesSize,
                caveBiomesTopY,
                caveBiomesTopJitter
        );
    }
}
