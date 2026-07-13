package org.betterx.wover.generator.api.biomesource.end;

import org.betterx.wover.common.generator.api.biomesource.BiomeSourceConfig;
import org.betterx.wover.core.api.IntegrationCore;
import org.betterx.wover.generator.api.map.MapBuilderFunction;
import org.betterx.wover.generator.impl.biomesource.end.WoverEndBiomeSource;
import org.betterx.wover.generator.impl.map.hex.HexBiomeMap;
import org.betterx.wover.generator.impl.map.square.SquareBiomeMap;

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
 * int, int, int) the constructor} to build a fully custom configuration.
 */
public class WoverEndConfig implements BiomeSourceConfig<WoverEndBiomeSource> {
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
            128
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
            256
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
            MINECRAFT_17.barrensBiomesSize
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
            MINECRAFT_18.barrensBiomesSize * 2
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
            MINECRAFT_18.barrensBiomesSize
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
            MINECRAFT_17.barrensBiomesSize
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
            MINECRAFT_18.barrensBiomesSize * 2
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
            MINECRAFT_18.barrensBiomesSize
    );
    /**
     * The configuration used by {@link org.betterx.wover.generator.api.preset.WorldPresets#WOVER_WORLD}.
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
                            .forGetter(o -> o.barrensBiomesSize)
            )
            .apply(instance, WoverEndConfig::new));

    /**
     * Creates a new instance.
     * <p>
     * The four biome ring sizes are clamped to {@code [1, 8192]}.
     *
     * @param mapVersion             The map algorithm used to distribute biomes within each ring.
     * @param generatorVersion       The algorithm used to decide which ring a position belongs to.
     * @param withVoidBiomes         Whether small End islands (void biomes) generate at all.
     * @param innerVoidRadiusSquared The squared radius (in blocks) of the center island ring.
     * @param centerBiomesSize       The biome size of the center island ring.
     * @param voidBiomesSize         The biome size of the small-island (void) ring.
     * @param landBiomesSize         The biome size of the highlands/midlands ring.
     * @param barrensBiomesSize      The biome size of the barrens ring.
     */
    public WoverEndConfig(
            @NotNull EndBiomeMapType mapVersion,
            @NotNull EndBiomeGeneratorType generatorVersion,
            boolean withVoidBiomes,
            int innerVoidRadiusSquared,
            int centerBiomesSize,
            int voidBiomesSize,
            int landBiomesSize,
            int barrensBiomesSize
    ) {
        this.mapVersion = mapVersion;
        this.generatorVersion = generatorVersion;
        this.withVoidBiomes = withVoidBiomes;
        this.innerVoidRadiusSquared = innerVoidRadiusSquared;
        this.barrensBiomesSize = Mth.clamp(barrensBiomesSize, 1, 8192);
        this.voidBiomesSize = Mth.clamp(voidBiomesSize, 1, 8192);
        this.centerBiomesSize = Mth.clamp(centerBiomesSize, 1, 8192);
        this.landBiomesSize = Mth.clamp(landBiomesSize, 1, 8192);
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
         * The factory used to build the {@link org.betterx.wover.generator.api.map.BiomeMap BiomeMap} for
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
        return withVoidBiomes == that.withVoidBiomes && innerVoidRadiusSquared == that.innerVoidRadiusSquared && voidBiomesSize == that.voidBiomesSize && centerBiomesSize == that.centerBiomesSize && landBiomesSize == that.landBiomesSize && barrensBiomesSize == that.barrensBiomesSize && mapVersion == that.mapVersion && generatorVersion == that.generatorVersion;
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
                barrensBiomesSize
        );
    }
}
