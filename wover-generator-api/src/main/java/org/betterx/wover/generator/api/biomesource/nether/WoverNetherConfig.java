package org.betterx.wover.generator.api.biomesource.nether;

import org.betterx.wover.common.generator.api.biomesource.BiomeSourceConfig;
import org.betterx.wover.generator.api.map.MapBuilderFunction;
import org.betterx.wover.generator.impl.biomesource.nether.WoverNetherBiomeSource;
import org.betterx.wover.generator.impl.map.hex.HexBiomeMap;
import org.betterx.wover.generator.impl.map.square.SquareBiomeMap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

/**
 * The {@link BiomeSourceConfig} of {@link WoverNetherBiomeSource}, matching the {@code config} field of the
 * {@code wover:nether_biome_source} {@code biome_source} type.
 * <p>
 * Controls the {@link NetherBiomeMapType map algorithm}, the horizontal biome size, and — if
 * {@link #useVerticalBiomes} is set — the vertical biome size used to stack biome layers on top of each
 * other. A handful of presets matching the behavior of past BCLib/WoVer versions are provided as constants
 * ({@link #VANILLA}, {@link #MINECRAFT_17}, ..., {@link #DEFAULT}) — use
 * {@link #WoverNetherConfig(NetherBiomeMapType, int, int, boolean) the constructor} to build a fully custom
 * configuration.
 */
public class WoverNetherConfig implements BiomeSourceConfig<WoverNetherBiomeSource> {
    /**
     * The behavior of vanilla's Nether (before WoVer/BCLib biome placement existed): a hex-grid map without
     * vertical biome layers.
     */
    public static final WoverNetherConfig VANILLA = new WoverNetherConfig(
            NetherBiomeMapType.VANILLA,
            256,
            86,
            false
    );
    /**
     * The behavior of BCLib 1.17: a {@link NetherBiomeMapType#SQUARE} map with vertical biome layers
     * enabled.
     */
    public static final WoverNetherConfig MINECRAFT_17 = new WoverNetherConfig(
            NetherBiomeMapType.SQUARE,
            256,
            86,
            true
    );
    /**
     * The behavior of BCLib 1.18+: a {@link NetherBiomeMapType#HEX} map, otherwise matching
     * {@link #MINECRAFT_17}.
     */
    public static final WoverNetherConfig MINECRAFT_18 = new WoverNetherConfig(
            NetherBiomeMapType.HEX,
            MINECRAFT_17.biomeSize,
            MINECRAFT_17.biomeSizeVertical,
            MINECRAFT_17.useVerticalBiomes
    );

    /**
     * A larger-biomes variant of {@link #MINECRAFT_18}.
     */
    public static final WoverNetherConfig MINECRAFT_18_LARGE = new WoverNetherConfig(
            NetherBiomeMapType.HEX,
            MINECRAFT_18.biomeSize * 4,
            MINECRAFT_18.biomeSizeVertical * 2,
            MINECRAFT_18.useVerticalBiomes
    );

    /**
     * A variant of {@link #MINECRAFT_18} with a larger vertical biome size, for the amplified world preset.
     */
    public static final WoverNetherConfig MINECRAFT_18_AMPLIFIED = new WoverNetherConfig(
            NetherBiomeMapType.HEX,
            MINECRAFT_18.biomeSize,
            128,
            true
    );

    /**
     * The configuration used by {@link org.betterx.wover.generator.api.preset.WorldPresets#WOVER_WORLD}.
     */
    public static final WoverNetherConfig DEFAULT = MINECRAFT_18;

    /**
     * The {@link Codec} for this class, matching the {@code config} field of the
     * {@code wover:nether_biome_source} {@code biome_source} type. Every field is optional and falls back
     * to {@link #DEFAULT}.
     */
    public static final Codec<WoverNetherConfig> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    WoverNetherConfig.NetherBiomeMapType.CODEC
                            .fieldOf("map_type").orElse(DEFAULT.mapVersion)
                            .forGetter(o -> o.mapVersion),
                    Codec.INT.fieldOf("biome_size").orElse(DEFAULT.biomeSize)
                             .forGetter(o -> o.biomeSize),
                    Codec.INT.fieldOf("biome_size_vertical").orElse(DEFAULT.biomeSizeVertical)
                             .forGetter(o -> o.biomeSizeVertical),
                    Codec.BOOL.fieldOf("use_vertical_biomes").orElse(DEFAULT.useVerticalBiomes)
                              .forGetter(o -> o.useVerticalBiomes)
            )
            .apply(instance, WoverNetherConfig::new));
    /**
     * The map algorithm used to distribute biomes spatially.
     */
    public final @NotNull NetherBiomeMapType mapVersion;
    /**
     * The horizontal biome size.
     */
    public final int biomeSize;
    /**
     * The vertical biome size, used to stack biome layers on top of each other when
     * {@link #useVerticalBiomes} is set.
     */
    public final int biomeSizeVertical;
    /**
     * Whether biomes are additionally stacked vertically (in layers of {@link #biomeSizeVertical}) instead
     * of only being distributed horizontally.
     */
    public final boolean useVerticalBiomes;

    /**
     * Creates a new instance.
     * <p>
     * {@code biomeSize} and {@code biomeSizeVertical} are clamped to {@code [1, 8192]}.
     *
     * @param mapVersion        The map algorithm used to distribute biomes spatially.
     * @param biomeSize         The horizontal biome size.
     * @param biomeSizeVertical The vertical biome size.
     * @param useVerticalBiomes Whether biomes are additionally stacked vertically.
     */
    public WoverNetherConfig(
            @NotNull NetherBiomeMapType mapVersion,
            int biomeSize,
            int biomeSizeVertical,
            boolean useVerticalBiomes
    ) {
        this.mapVersion = mapVersion;
        this.biomeSize = Mth.clamp(biomeSize, 1, 8192);
        this.biomeSizeVertical = Mth.clamp(biomeSizeVertical, 1, 8192);
        this.useVerticalBiomes = useVerticalBiomes;
    }

    @Override
    public String toString() {
        return "NetherConfig{" +
                "mapVersion=" + mapVersion +
                ", useVerticalBiomes=" + useVerticalBiomes +
                ", biomeSize=" + biomeSize / 16 +
                ", biomeSizeVertical=" + biomeSizeVertical / 16 +
                '}';
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns {@code true} if {@code input} is also a {@link WoverNetherConfig} using the same
     * {@link #mapVersion} — the biome sizes and {@link #useVerticalBiomes} may differ without requiring a
     * chunk repair.
     */
    @Override
    public boolean couldSetWithoutRepair(BiomeSourceConfig<?> input) {
        if (input instanceof WoverNetherConfig cfg) {
            return mapVersion == cfg.mapVersion;
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
        if (!(o instanceof WoverNetherConfig that)) return false;
        return mapVersion == that.mapVersion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mapVersion);
    }

    /**
     * The algorithm used to distribute biomes spatially, matching the {@code map_type} field of the
     * {@link #CODEC}.
     */
    public enum NetherBiomeMapType implements StringRepresentable {
        /**
         * A hex-grid based map, matching vanilla's own Nether biome distribution.
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
        public static final Codec<NetherBiomeMapType> CODEC = StringRepresentable.fromEnum(NetherBiomeMapType::values);
        /**
         * The serialized name of this value.
         */
        public final String name;
        /**
         * The factory used to build the {@link org.betterx.wover.generator.api.map.BiomeMap BiomeMap} for
         * this map type.
         */
        public final MapBuilderFunction mapBuilder;

        NetherBiomeMapType(String name, MapBuilderFunction mapBuilder) {
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
}
