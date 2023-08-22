package org.betterx.wover.generator.api.biomesource;

import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeDataRegistry;
import org.betterx.wover.entrypoint.WoverBiome;
import org.betterx.wover.generator.impl.biomesource.WoverBiomeDataImpl;
import org.betterx.wover.state.api.WorldState;
import org.betterx.wover.util.RandomizedWeightedList;

import com.mojang.datafixers.util.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WoverBiomeData extends BiomeData {
    public static final Codec<WoverBiomeData> CODEC = codec(WoverBiomeData::new);
    public static final KeyDispatchDataCodec<WoverBiomeData> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);

    public final float terrainHeight;
    public final float genChance;
    public final int edgeSize;
    public final boolean vertical;
    public final @Nullable ResourceKey<Biome> edge;
    public final @Nullable ResourceKey<BiomeData> edgeData;
    public final @Nullable ResourceKey<Biome> parent;
    public final @Nullable ResourceKey<BiomeData> parentData;

    public WoverBiomeData(
            float fogDensity,
            @NotNull ResourceKey<Biome> biome,
            @NotNull List<Climate.ParameterPoint> parameterPoints,
            float terrainHeight,
            float genChance,
            int edgeSize,
            boolean vertical,
            @Nullable ResourceKey<Biome> edge,
            @Nullable ResourceKey<Biome> parent
    ) {
        super(fogDensity, biome, parameterPoints);

        this.terrainHeight = terrainHeight;
        this.genChance = genChance;
        this.edgeSize = edgeSize;
        this.vertical = vertical;
        this.edge = edge;
        this.parent = parent;

        this.edgeData = edge == null ? null : BiomeDataRegistry.createKey(edge.location());
        this.parentData = parent == null ? null : BiomeDataRegistry.createKey(parent.location());
    }

    public static WoverBiomeData of(ResourceKey<Biome> biome) {
        return new WoverBiomeData(1.0f, biome, List.of(), 0.1f, 1.0f, 0, false, null, null);
    }

    public static WoverBiomeData withEdge(ResourceKey<Biome> biome, ResourceKey<Biome> edge) {
        return new WoverBiomeData(1.0f, biome, List.of(), 0.1f, 1.0f, 4, false, edge, null);
    }

    public static <T extends WoverBiomeData> Codec<T> codec(
            final Function9<Float, ResourceKey<Biome>, List<Climate.ParameterPoint>, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, T> factory
    ) {
        WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
        return codec(
                a.t0,
                a.t1,
                a.t2,
                a.t3,
                a.t4,
                a.t5,
                (w0, w1, w2, w3, w4, w5, w6, w7, w8) -> factory.apply(
                        w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null)
                )
        );
    }

    public static <T extends WoverBiomeData, P10> Codec<T> codec(
            final RecordCodecBuilder<T, P10> p10,
            final Function10<Float, ResourceKey<Biome>, List<Climate.ParameterPoint>, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, T> factory
    ) {
        WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
        return codec(a.t0, a.t1, a.t2, a.t3, a.t4, a.t5, p10,
                (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9) -> factory.apply(
                        w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null), w9
                )
        );
    }

    public static <T extends WoverBiomeData, P10, P11> Codec<T> codec(
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final Function11<Float, ResourceKey<Biome>, List<Climate.ParameterPoint>, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, P11, T> factory
    ) {
        WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
        return codec(a.t0, a.t1, a.t2, a.t3, a.t4, a.t5, p10, p11,
                (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10) -> factory.apply(
                        w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null), w9, w10
                )
        );
    }

    public static <T extends WoverBiomeData, P10, P11, P12> Codec<T> codec(
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final RecordCodecBuilder<T, P12> p12,
            final Function12<Float, ResourceKey<Biome>, List<Climate.ParameterPoint>, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, P11, P12, T> factory
    ) {
        WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
        return codec(a.t0, a.t1, a.t2, a.t3, a.t4, a.t5, p10, p11, p12,
                (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11) -> factory.apply(
                        w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null), w9, w10, w11
                )
        );
    }

    public static <T extends WoverBiomeData, P10, P11, P12, P13> Codec<T> codec(
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final RecordCodecBuilder<T, P12> p12,
            final RecordCodecBuilder<T, P13> p13,
            final Function13<Float, ResourceKey<Biome>, List<Climate.ParameterPoint>, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, P11, P12, P13, T> factory
    ) {
        WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
        return codec(a.t0, a.t1, a.t2, a.t3, a.t4, a.t5, p10, p11, p12, p13,
                (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12) -> factory.apply(
                        w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null), w9, w10, w11, w12
                )
        );
    }

    public static <T extends WoverBiomeData, P10, P11, P12, P13, P14> Codec<T> codec(
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final RecordCodecBuilder<T, P12> p12,
            final RecordCodecBuilder<T, P13> p13,
            final RecordCodecBuilder<T, P14> p14,
            final Function14<Float, ResourceKey<Biome>, List<Climate.ParameterPoint>, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, P11, P12, P13, P14, T> factory
    ) {
        WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
        return codec(a.t0, a.t1, a.t2, a.t3, a.t4, a.t5, p10, p11, p12, p13, p14,
                (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12, w13) -> factory.apply(
                        w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null),
                        w9, w10, w11, w12, w13
                )
        );
    }

    public static <T extends WoverBiomeData, P10, P11, P12, P13, P14, P15> Codec<T> codec(
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final RecordCodecBuilder<T, P12> p12,
            final RecordCodecBuilder<T, P13> p13,
            final RecordCodecBuilder<T, P14> p14,
            final RecordCodecBuilder<T, P15> p15,
            final Function15<Float, ResourceKey<Biome>, List<Climate.ParameterPoint>, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, P11, P12, P13, P14, P15, T> factory
    ) {
        WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
        return codec(a.t0, a.t1, a.t2, a.t3, a.t4, a.t5, p10, p11, p12, p13, p14, p15,
                (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12, w13, w14) -> factory.apply(
                        w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null),
                        w9, w10, w11, w12, w13, w14
                )
        );
    }

    public static <T extends WoverBiomeData, P10, P11, P12, P13, P14, P15, P16> Codec<T> codec(
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final RecordCodecBuilder<T, P12> p12,
            final RecordCodecBuilder<T, P13> p13,
            final RecordCodecBuilder<T, P14> p14,
            final RecordCodecBuilder<T, P15> p15,
            final RecordCodecBuilder<T, P16> p16,
            final Function16<Float, ResourceKey<Biome>, List<Climate.ParameterPoint>, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, P11, P12, P13, P14, P15, P16, T> factory
    ) {
        WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
        return codec(a.t0, a.t1, a.t2, a.t3, a.t4, a.t5, p10, p11, p12, p13, p14, p15, p16,
                (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12, w13, w14, w15) -> factory.apply(
                        w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null),
                        w9, w10, w11, w12, w13, w14, w15
                )
        );
    }


    public @NotNull Registry<BiomeData> getDataRegistry(String forWhat) throws IllegalStateException {
        RegistryAccess acc = WorldState.registryAccess();

        if (acc == null) {
            if (WorldState.allStageRegistryAccess() == null) {
                throw new IllegalStateException("Accessing " + forWhat + " of " + biomeKey + " before any registry is ready!");
            }
            if (preFinalAccessWarning++ < 5)
                WoverBiome.C.log.verboseWarning("Accessing " + forWhat + " of " + biomeKey + " before registry is ready!");
            acc = WorldState.allStageRegistryAccess();
        }
        final Registry<BiomeData> reg = acc != null
                ? acc.registry(BiomeDataRegistry.BIOME_DATA_REGISTRY).orElse(null)
                : null;
        if (reg == null)
            throw new IllegalStateException("Accessing " + forWhat + " of " + biomeKey + " before biome data registry is ready!");

        return reg;
    }

    private @Nullable Optional<WoverBiomeData> edgeParent = null;

    public WoverBiomeData findEdgeParent() {
        //null means, that we did not yet check for an edge parent
        if (edgeParent != null) return edgeParent.orElse(null);

        final Registry<BiomeData> reg = getDataRegistry("edge parent");

        for (Map.Entry<ResourceKey<BiomeData>, BiomeData> entry : reg.entrySet()) {
            if (entry.getValue() instanceof WoverBiomeData b && this.isSame(b.edge)) {
                edgeParent = Optional.of(b);
                return b;
            }
        }

        edgeParent = Optional.empty();
        return null;
    }

    public RandomizedWeightedList<BiomeData> createBiomeAlternatives(Predicate<WoverBiomeData> isAllowed) {
        final Registry<BiomeData> reg = getDataRegistry("biome alternatives");
        final RandomizedWeightedList<BiomeData> alternatives = new RandomizedWeightedList<>();

        alternatives.add(this, genChance);
        for (Map.Entry<ResourceKey<BiomeData>, BiomeData> entry : reg.entrySet()) {
            if (entry.getValue() instanceof WoverBiomeData b && this.isSame(b.parent) && isAllowed.test(b)) {
                alternatives.add(b, b.genChance);
            }
        }

        return alternatives;
    }

    /**
     * Will return @code{true} if this biome is neither an edge nor does it have a parent.
     *
     * @return Whether this biome is pickable.
     */
    @Override
    public boolean isPickable() {
        return parent == null && findEdgeParent() == null;
    }

    public BiomeData getEdgeData() {
        if (edgeData == null) return null;
        final Registry<BiomeData> reg = getDataRegistry("edge biome");
        return reg.get(edgeData);
    }

    public BiomeData getParentData() {
        if (edgeData == null) return null;
        final Registry<BiomeData> reg = getDataRegistry("parent biome");
        return reg.get(parentData);
    }

    public KeyDispatchDataCodec<? extends WoverBiomeData> codec() {
        return KEY_CODEC;
    }

}
