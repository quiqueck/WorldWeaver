package org.betterx.wover.biome.impl.data;

import org.betterx.wover.biome.api.data.BiomeData;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.List;

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
//    private static class CodecAttributes<T extends BiomeData> {
//        public RecordCodecBuilder<T, Float> t0 = Codec.FLOAT.optionalFieldOf("terrainHeight"), 0.1f)
//                                                            .forGetter((T o1) -> o1.settings.terrainHeight);
//
//        public RecordCodecBuilder<T, Float> t1 = Codec.FLOAT.optionalFieldOf("fogDensity", 1.0f)
//                                                            .forGetter((T o1) -> o1.settings.fogDensity);
//        public RecordCodecBuilder<T, Float> t2 = Codec.FLOAT.optionalFieldOf("genChance", 1.0f)
//                                                            .forGetter((T o1) -> o1.settings.genChance);
//        public RecordCodecBuilder<T, Integer> t3 = Codec.INT.optionalFieldOf("edgeSize", 0)
//                                                            .forGetter((T o1) -> o1.settings.edgeSize);
//        public RecordCodecBuilder<T, Boolean> t4 = Codec.BOOL.optionalFieldOf("vertical", false)
//                                                             .forGetter((T o1) -> o1.settings.vertical);
//        public RecordCodecBuilder<T, Optional<ResourceLocation>> t5 =
//                ResourceLocation.CODEC
//                        .optionalFieldOf("edge")
//                        .orElse(Optional.empty())
//                        .forGetter((T o1) -> ((BCLBiome) o1).edge == null
//                                ? Optional.empty()
//                                : Optional.of(((BCLBiome) o1).edge));
//        public RecordCodecBuilder<T, ResourceLocation> t6 =
//                ResourceLocation.CODEC.fieldOf("biome")
//                                      .forGetter((T o) -> ((BCLBiome) o).biomeID);
//        public RecordCodecBuilder<T, Optional<List<Climate.ParameterPoint>>> t7 =
//                Climate.ParameterPoint.CODEC.listOf()
//                                            .optionalFieldOf("parameter_points")
//                                            .orElse(Optional.of(List.of()))
//                                            .forGetter((T o) ->
//                                                    o.parameterPoints == null || o.parameterPoints.isEmpty()
//                                                            ? Optional.empty()
//                                                            : Optional.of(o.parameterPoints));
//
//        public RecordCodecBuilder<T, Optional<ResourceLocation>> t8 =
//                ResourceLocation.CODEC.optionalFieldOf("parent")
//                                      .orElse(Optional.empty())
//                                      .forGetter(
//                                              (T o1) ->
//                                                      ((BCLBiome) o1).biomeParent == null
//                                                              ? Optional.empty()
//                                                              : Optional.of(
//                                                                      ((BCLBiome) o1).biomeParent));
//        public RecordCodecBuilder<T, Optional<String>> t10 =
//                Codec.STRING.optionalFieldOf("intended_for")
//                            .orElse(Optional.of(BiomeAPI.BiomeType.NONE.getName()))
//                            .forGetter((T o) ->
//                                    ((BCLBiome) o).intendedType == null
//                                            ? Optional.empty()
//                                            : Optional.of(((BCLBiome) o).intendedType.getName()));
//    }
}
