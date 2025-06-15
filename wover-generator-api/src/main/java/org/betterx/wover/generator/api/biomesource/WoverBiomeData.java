package org.betterx.wover.generator.api.biomesource;

import org.betterx.wover.biome.api.data.BiomeData;
import org.betterx.wover.biome.api.data.BiomeDataRegistry;
import org.betterx.wover.biome.api.data.BiomeGenerationDataContainer;
import org.betterx.wover.entrypoint.LibWoverBiome;
import org.betterx.wover.generator.impl.biomesource.WoverBiomeDataImpl;
import org.betterx.wover.state.api.WorldState;

import com.mojang.datafixers.util.*;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;

import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WoverBiomeData extends BiomeData {
    public static final MapCodec<WoverBiomeData> CODEC = codec(WoverBiomeData::new);
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
            @NotNull BiomeGenerationDataContainer generationData,
            float terrainHeight,
            float genChance,
            int edgeSize,
            boolean vertical,
            @Nullable ResourceKey<Biome> edge,
            @Nullable ResourceKey<Biome> parent
    ) {
        super(fogDensity, biome, generationData);

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
        return new WoverBiomeData(1.0f, biome, BiomeGenerationDataContainer.EMPTY, 0.1f, 1.0f, 0, false, null, null);
    }

    public static WoverBiomeData withEdge(ResourceKey<Biome> biome, ResourceKey<Biome> edge) {
        return new WoverBiomeData(1.0f, biome, BiomeGenerationDataContainer.EMPTY, 0.1f, 1.0f, 4, false, edge, null);
    }

    public static WoverBiomeData tempWithEdge(ResourceKey<Biome> biome, ResourceKey<Biome> edge) {
        return new WoverBiomeData.InMemoryWoverBiomeData(1.0f, biome, BiomeGenerationDataContainer.EMPTY, 0.1f, 1.0f, 4, false, edge, null);
    }

    public static <T extends WoverBiomeData> MapCodec<T> codec(
            final Function9<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, T> factory
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

    public static <T extends WoverBiomeData, P10> MapCodec<T> codec(
            final RecordCodecBuilder<T, P10> p10,
            final Function10<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, T> factory
    ) {
        WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
        return codec(a.t0, a.t1, a.t2, a.t3, a.t4, a.t5, p10,
                (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9) -> factory.apply(
                        w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null), w9
                )
        );
    }

    public static <T extends WoverBiomeData, P10, P11> MapCodec<T> codec(
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final Function11<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, P11, T> factory
    ) {
        WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
        return codec(a.t0, a.t1, a.t2, a.t3, a.t4, a.t5, p10, p11,
                (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10) -> factory.apply(
                        w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null), w9, w10
                )
        );
    }

    public static <T extends WoverBiomeData, P10, P11, P12> MapCodec<T> codec(
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final RecordCodecBuilder<T, P12> p12,
            final Function12<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, P11, P12, T> factory
    ) {
        WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
        return codec(a.t0, a.t1, a.t2, a.t3, a.t4, a.t5, p10, p11, p12,
                (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11) -> factory.apply(
                        w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null), w9, w10, w11
                )
        );
    }

    public static <T extends WoverBiomeData, P10, P11, P12, P13> MapCodec<T> codec(
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final RecordCodecBuilder<T, P12> p12,
            final RecordCodecBuilder<T, P13> p13,
            final Function13<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, P11, P12, P13, T> factory
    ) {
        WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
        return codec(a.t0, a.t1, a.t2, a.t3, a.t4, a.t5, p10, p11, p12, p13,
                (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12) -> factory.apply(
                        w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null), w9, w10, w11, w12
                )
        );
    }

    public static <T extends WoverBiomeData, P10, P11, P12, P13, P14> MapCodec<T> codec(
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final RecordCodecBuilder<T, P12> p12,
            final RecordCodecBuilder<T, P13> p13,
            final RecordCodecBuilder<T, P14> p14,
            final Function14<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, P11, P12, P13, P14, T> factory
    ) {
        WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
        return codec(a.t0, a.t1, a.t2, a.t3, a.t4, a.t5, p10, p11, p12, p13, p14,
                (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12, w13) -> factory.apply(
                        w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null),
                        w9, w10, w11, w12, w13
                )
        );
    }

    public static <T extends WoverBiomeData, P10, P11, P12, P13, P14, P15> MapCodec<T> codec(
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final RecordCodecBuilder<T, P12> p12,
            final RecordCodecBuilder<T, P13> p13,
            final RecordCodecBuilder<T, P14> p14,
            final RecordCodecBuilder<T, P15> p15,
            final Function15<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, P11, P12, P13, P14, P15, T> factory
    ) {
        WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
        return codec(a.t0, a.t1, a.t2, a.t3, a.t4, a.t5, p10, p11, p12, p13, p14, p15,
                (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12, w13, w14) -> factory.apply(
                        w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null),
                        w9, w10, w11, w12, w13, w14
                )
        );
    }

    public static <T extends WoverBiomeData, P10, P11, P12, P13, P14, P15, P16> MapCodec<T> codec(
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final RecordCodecBuilder<T, P12> p12,
            final RecordCodecBuilder<T, P13> p13,
            final RecordCodecBuilder<T, P14> p14,
            final RecordCodecBuilder<T, P15> p15,
            final RecordCodecBuilder<T, P16> p16,
            final Function16<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, P11, P12, P13, P14, P15, P16, T> factory
    ) {
        WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
        return codec(a.t0, a.t1, a.t2, a.t3, a.t4, a.t5, p10, p11, p12, p13, p14, p15, p16,
                (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12, w13, w14, w15) -> factory.apply(
                        w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null),
                        w9, w10, w11, w12, w13, w14, w15
                )
        );
    }


    public static @NotNull Registry<BiomeData> getDataRegistry(
            String forWhat,
            ResourceKey<Biome> ofBiome
    ) throws IllegalStateException {
        RegistryAccess acc = WorldState.registryAccess();

        if (acc == null) {
            if (WorldState.allStageRegistryAccess() == null) {
                throw new IllegalStateException("Accessing " + forWhat + " of " + ofBiome + " before any registry is ready!");
            }
            if (preFinalAccessWarning++ < 5)
                LibWoverBiome.C.log.verboseWarning("Accessing " + forWhat + " of " + ofBiome + " before registry is ready!");
            acc = WorldState.allStageRegistryAccess();
        }
        final Registry<BiomeData> reg = acc != null
                ? acc.registry(BiomeDataRegistry.BIOME_DATA_REGISTRY).orElse(null)
                : null;

        if (reg == null)
            throw new IllegalStateException("Accessing " + forWhat + " of " + ofBiome + " before biome data registry is ready!");

        return reg;
    }

    private @Nullable Optional<WoverBiomeData> edgeParent = null;

    public WoverBiomeData findEdgeParent() {
        //null means, that we did not yet check for an edge parent
        if (edgeParent != null) return edgeParent.orElse(null);

        final Registry<BiomeData> reg = getDataRegistry("edge parent", biomeKey);

        for (Map.Entry<ResourceKey<BiomeData>, BiomeData> entry : reg.entrySet()) {
            if (entry.getValue() instanceof WoverBiomeData b && this.isSame(b.edge)) {
                edgeParent = Optional.of(b);
                return b;
            }
        }

        edgeParent = Optional.empty();
        return null;
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

    @Override
    public float genChance(){
        return this.genChance;
    }

    public BiomeData getEdgeData() {
        if (edgeData == null) return null;
        final Registry<BiomeData> reg = getDataRegistry("edge biome", biomeKey);
        return reg.get(edgeData);
    }

    public BiomeData getParentData() {
        if (edgeData == null) return null;
        final Registry<BiomeData> reg = getDataRegistry("parent biome", biomeKey);
        return reg.get(parentData);
    }

    public KeyDispatchDataCodec<? extends WoverBiomeData> codec() {
        return KEY_CODEC;
    }

    public static class InMemoryWoverBiomeData extends WoverBiomeData {
        private InMemoryWoverBiomeData(
                float fogDensity,
                @NotNull ResourceKey<Biome> biome,
                @NotNull BiomeGenerationDataContainer generationData,
                float terrainHeight,
                float genChance,
                int edgeSize,
                boolean vertical,
                @Nullable ResourceKey<Biome> edge,
                @Nullable ResourceKey<Biome> parent
        ) {
            super(fogDensity, biome, generationData, terrainHeight, genChance, edgeSize, vertical, edge, parent);
        }

        @Override
        public boolean isTemp() {
            return true;
        }
    }
}
