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

public class WoverNetherConfig implements BiomeSourceConfig<WoverNetherBiomeSource> {
    public static final WoverNetherConfig VANILLA = new WoverNetherConfig(
            NetherBiomeMapType.VANILLA,
            256,
            86,
            false
    );
    public static final WoverNetherConfig MINECRAFT_17 = new WoverNetherConfig(
            NetherBiomeMapType.SQUARE,
            256,
            86,
            true
    );
    public static final WoverNetherConfig MINECRAFT_18 = new WoverNetherConfig(
            NetherBiomeMapType.HEX,
            MINECRAFT_17.biomeSize,
            MINECRAFT_17.biomeSizeVertical,
            MINECRAFT_17.useVerticalBiomes
    );

    public static final WoverNetherConfig MINECRAFT_18_LARGE = new WoverNetherConfig(
            NetherBiomeMapType.HEX,
            MINECRAFT_18.biomeSize * 4,
            MINECRAFT_18.biomeSizeVertical * 2,
            MINECRAFT_18.useVerticalBiomes
    );

    public static final WoverNetherConfig MINECRAFT_18_AMPLIFIED = new WoverNetherConfig(
            NetherBiomeMapType.HEX,
            MINECRAFT_18.biomeSize,
            128,
            true
    );

    public static final WoverNetherConfig DEFAULT = MINECRAFT_18;

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
    public final @NotNull NetherBiomeMapType mapVersion;
    public final int biomeSize;
    public final int biomeSizeVertical;
    public final boolean useVerticalBiomes;

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

    public enum NetherBiomeMapType implements StringRepresentable {
        VANILLA("vanilla", (seed, biomeSize, picker) -> new HexBiomeMap(seed, biomeSize, picker)),
        SQUARE("square", (seed, biomeSize, picker) -> new SquareBiomeMap(seed, biomeSize, picker)),
        HEX("hex", (seed, biomeSize, picker) -> new HexBiomeMap(seed, biomeSize, picker));

        public static final Codec<NetherBiomeMapType> CODEC = StringRepresentable.fromEnum(NetherBiomeMapType::values);
        public final String name;
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
