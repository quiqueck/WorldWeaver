package org.betterx.wover.biome.impl.data;

import org.betterx.wover.biome.api.data.BiomeData;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class BiomeDataImpl {
    public static class CodecAttributes<T extends BiomeData> {
        public RecordCodecBuilder<T, Float> t0 = Codec.FLOAT.optionalFieldOf("fogDensity", 1.0f)
                                                            .forGetter((T o1) -> o1.fogDensity);
        public RecordCodecBuilder<T, ResourceKey<Biome>> t1 =
                ResourceKey.codec(Registries.BIOME).fieldOf("biome")
                           .forGetter((T o) -> o.biomeKey);
        public RecordCodecBuilder<T, List<Climate.ParameterPoint>> t2 =
                Climate.ParameterPoint.CODEC.listOf()
                                            .optionalFieldOf("parameter_points", List.of())
                                            .forGetter((T o) -> o.parameterPoints);
    }

    public static class InMemoryBiomeData extends BiomeData {

        public InMemoryBiomeData(
                float fogDensity,
                @NotNull ResourceKey<Biome> biome,
                @NotNull List<Climate.ParameterPoint> parameterPoints
        ) {
            super(fogDensity, biome, parameterPoints);
        }

        public boolean isTemp() {
            return true;
        }
    }
}
