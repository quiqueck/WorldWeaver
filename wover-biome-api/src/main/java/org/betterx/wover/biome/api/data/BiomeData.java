package org.betterx.wover.biome.api.data;

import org.betterx.wover.state.api.WorldState;

import com.mojang.datafixers.util.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BiomeData {
    public static final Codec<BiomeData> CODEC = codec(BiomeData::new);
    public static final KeyDispatchDataCodec<BiomeData> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);
    public final ResourceKey<Biome> biomeKey;

    public final float fogDensity;

    public final List<Climate.ParameterPoint> parameterPoints;

    public @Nullable Holder<Biome> biomeHolder() {
        if (WorldState.registryAccess() == null) return null;
        return WorldState.registryAccess().registryOrThrow(Registries.BIOME).getHolder(biomeKey).orElse(null);
    }

    public @Nullable Biome biome() {
        if (WorldState.registryAccess() == null) return null;
        return WorldState.registryAccess().registryOrThrow(Registries.BIOME).getOptional(biomeKey).orElse(null);
    }

    public BiomeData(
            float fogDensity,
            @NotNull ResourceKey<Biome> biome,
            @NotNull List<Climate.ParameterPoint> parameterPoints
    ) {
        this.fogDensity = fogDensity;
        biomeKey = biome;
        this.parameterPoints = parameterPoints;
    }

    public KeyDispatchDataCodec<? extends BiomeData> codec() {
        return KEY_CODEC;
    }

    private static class CodecAttributes<T extends BiomeData> {
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

    public static <T extends BiomeData> Codec<T> codec(
            final Function3<Float, ResourceKey<Biome>, List<Climate.ParameterPoint>, T> factory
    ) {
        CodecAttributes<T> a = new CodecAttributes<>();
        return RecordCodecBuilder.create(
                instance -> instance.group(a.t0, a.t1, a.t2)
                                    .apply(instance, factory)
        );
    }

    public static <T extends BiomeData, P4> Codec<T> codec(
            final RecordCodecBuilder<T, P4> p4,
            final Function4<Float, ResourceKey<Biome>, List<Climate.ParameterPoint>, P4, T> factory
    ) {
        CodecAttributes<T> a = new CodecAttributes<>();
        return RecordCodecBuilder.create(
                instance -> instance.group(a.t0, a.t1, a.t2, p4)
                                    .apply(instance, factory)
        );
    }

    public static <T extends BiomeData, P4, P5> Codec<T> codec(
            final RecordCodecBuilder<T, P4> p4,
            final RecordCodecBuilder<T, P5> p5,
            final Function5<Float, ResourceKey<Biome>, List<Climate.ParameterPoint>, P4, P5, T> factory
    ) {
        CodecAttributes<T> a = new CodecAttributes<>();
        return RecordCodecBuilder.create(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5)
                                    .apply(instance, factory)
        );
    }

    public static <T extends BiomeData, P4, P5, P6> Codec<T> codec(
            final RecordCodecBuilder<T, P4> p4,
            final RecordCodecBuilder<T, P5> p5,
            final RecordCodecBuilder<T, P6> p6,
            final Function6<Float, ResourceKey<Biome>, List<Climate.ParameterPoint>, P4, P5, P6, T> factory
    ) {
        CodecAttributes<T> a = new CodecAttributes<>();
        return RecordCodecBuilder.create(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6)
                                    .apply(instance, factory)
        );
    }

    public static <T extends BiomeData, P4, P5, P6, P7> Codec<T> codec(
            final RecordCodecBuilder<T, P4> p4,
            final RecordCodecBuilder<T, P5> p5,
            final RecordCodecBuilder<T, P6> p6,
            final RecordCodecBuilder<T, P7> p7,
            final Function7<Float, ResourceKey<Biome>, List<Climate.ParameterPoint>, P4, P5, P6, P7, T> factory
    ) {
        CodecAttributes<T> a = new CodecAttributes<>();
        return RecordCodecBuilder.create(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7)
                                    .apply(instance, factory)
        );
    }

    public static <T extends BiomeData, P4, P5, P6, P7, P8> Codec<T> codec(
            final RecordCodecBuilder<T, P4> p4,
            final RecordCodecBuilder<T, P5> p5,
            final RecordCodecBuilder<T, P6> p6,
            final RecordCodecBuilder<T, P7> p7,
            final RecordCodecBuilder<T, P8> p8,
            final Function8<Float, ResourceKey<Biome>, List<Climate.ParameterPoint>, P4, P5, P6, P7, P8, T> factory
    ) {
        CodecAttributes<T> a = new CodecAttributes<>();
        return RecordCodecBuilder.create(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8)
                                    .apply(instance, factory)
        );
    }

    public static <T extends BiomeData, P4, P5, P6, P7, P8, P9> Codec<T> codec(
            final RecordCodecBuilder<T, P4> p4,
            final RecordCodecBuilder<T, P5> p5,
            final RecordCodecBuilder<T, P6> p6,
            final RecordCodecBuilder<T, P7> p7,
            final RecordCodecBuilder<T, P8> p8,
            final RecordCodecBuilder<T, P9> p9,
            final Function9<Float, ResourceKey<Biome>, List<Climate.ParameterPoint>, P4, P5, P6, P7, P8, P9, T> factory
    ) {
        CodecAttributes<T> a = new CodecAttributes<>();
        return RecordCodecBuilder.create(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9)
                                    .apply(instance, factory)
        );
    }

    public static <T extends BiomeData, P4, P5, P6, P7, P8, P9, P10> Codec<T> codec(
            final RecordCodecBuilder<T, P4> p4,
            final RecordCodecBuilder<T, P5> p5,
            final RecordCodecBuilder<T, P6> p6,
            final RecordCodecBuilder<T, P7> p7,
            final RecordCodecBuilder<T, P8> p8,
            final RecordCodecBuilder<T, P9> p9,
            final RecordCodecBuilder<T, P10> p10,
            final Function10<Float, ResourceKey<Biome>, List<Climate.ParameterPoint>, P4, P5, P6, P7, P8, P9, P10, T> factory
    ) {
        CodecAttributes<T> a = new CodecAttributes<>();
        return RecordCodecBuilder.create(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9, p10)
                                    .apply(instance, factory)
        );
    }

    public static <T extends BiomeData, P4, P5, P6, P7, P8, P9, P10, P11> Codec<T> codec(
            final RecordCodecBuilder<T, P4> p4,
            final RecordCodecBuilder<T, P5> p5,
            final RecordCodecBuilder<T, P6> p6,
            final RecordCodecBuilder<T, P7> p7,
            final RecordCodecBuilder<T, P8> p8,
            final RecordCodecBuilder<T, P9> p9,
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final Function11<Float, ResourceKey<Biome>, List<Climate.ParameterPoint>, P4, P5, P6, P7, P8, P9, P10, P11, T> factory
    ) {
        CodecAttributes<T> a = new CodecAttributes<>();
        return RecordCodecBuilder.create(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9, p10, p11)
                                    .apply(instance, factory)
        );
    }

    public static <T extends BiomeData, P4, P5, P6, P7, P8, P9, P10, P11, P12> Codec<T> codec(
            final RecordCodecBuilder<T, P4> p4,
            final RecordCodecBuilder<T, P5> p5,
            final RecordCodecBuilder<T, P6> p6,
            final RecordCodecBuilder<T, P7> p7,
            final RecordCodecBuilder<T, P8> p8,
            final RecordCodecBuilder<T, P9> p9,
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final RecordCodecBuilder<T, P12> p12,
            final Function12<Float, ResourceKey<Biome>, List<Climate.ParameterPoint>, P4, P5, P6, P7, P8, P9, P10, P11, P12, T> factory
    ) {
        CodecAttributes<T> a = new CodecAttributes<>();
        return RecordCodecBuilder.create(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9, p10, p11, p12)
                                    .apply(instance, factory)
        );
    }

    public static <T extends BiomeData, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13> Codec<T> codec(
            final RecordCodecBuilder<T, P4> p4,
            final RecordCodecBuilder<T, P5> p5,
            final RecordCodecBuilder<T, P6> p6,
            final RecordCodecBuilder<T, P7> p7,
            final RecordCodecBuilder<T, P8> p8,
            final RecordCodecBuilder<T, P9> p9,
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final RecordCodecBuilder<T, P12> p12,
            final RecordCodecBuilder<T, P13> p13,
            final Function13<Float, ResourceKey<Biome>, List<Climate.ParameterPoint>, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, T> factory
    ) {
        CodecAttributes<T> a = new CodecAttributes<>();
        return RecordCodecBuilder.create(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13)
                                    .apply(instance, factory)
        );
    }

    public static <T extends BiomeData, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14> Codec<T> codec(
            final RecordCodecBuilder<T, P4> p4,
            final RecordCodecBuilder<T, P5> p5,
            final RecordCodecBuilder<T, P6> p6,
            final RecordCodecBuilder<T, P7> p7,
            final RecordCodecBuilder<T, P8> p8,
            final RecordCodecBuilder<T, P9> p9,
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final RecordCodecBuilder<T, P12> p12,
            final RecordCodecBuilder<T, P13> p13,
            final RecordCodecBuilder<T, P14> p14,
            final Function14<Float, ResourceKey<Biome>, List<Climate.ParameterPoint>, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, T> factory
    ) {
        CodecAttributes<T> a = new CodecAttributes<>();
        return RecordCodecBuilder.create(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14)
                                    .apply(instance, factory)
        );
    }

    public static <T extends BiomeData, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15> Codec<T> codec(
            final RecordCodecBuilder<T, P4> p4,
            final RecordCodecBuilder<T, P5> p5,
            final RecordCodecBuilder<T, P6> p6,
            final RecordCodecBuilder<T, P7> p7,
            final RecordCodecBuilder<T, P8> p8,
            final RecordCodecBuilder<T, P9> p9,
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final RecordCodecBuilder<T, P12> p12,
            final RecordCodecBuilder<T, P13> p13,
            final RecordCodecBuilder<T, P14> p14,
            final RecordCodecBuilder<T, P15> p15,
            final Function15<Float, ResourceKey<Biome>, List<Climate.ParameterPoint>, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, T> factory
    ) {
        CodecAttributes<T> a = new CodecAttributes<>();
        return RecordCodecBuilder.create(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15)
                                    .apply(instance, factory)
        );
    }

    public static <T extends BiomeData, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16> Codec<T> codec(
            final RecordCodecBuilder<T, P4> p4,
            final RecordCodecBuilder<T, P5> p5,
            final RecordCodecBuilder<T, P6> p6,
            final RecordCodecBuilder<T, P7> p7,
            final RecordCodecBuilder<T, P8> p8,
            final RecordCodecBuilder<T, P9> p9,
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final RecordCodecBuilder<T, P12> p12,
            final RecordCodecBuilder<T, P13> p13,
            final RecordCodecBuilder<T, P14> p14,
            final RecordCodecBuilder<T, P15> p15,
            final RecordCodecBuilder<T, P16> p16,
            final Function16<Float, ResourceKey<Biome>, List<Climate.ParameterPoint>, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, T> factory
    ) {
        CodecAttributes<T> a = new CodecAttributes<>();
        return RecordCodecBuilder.create(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16)
                                    .apply(instance, factory)
        );
    }
}
