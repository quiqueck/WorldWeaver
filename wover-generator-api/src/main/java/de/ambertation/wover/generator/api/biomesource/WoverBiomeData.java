package de.ambertation.wover.generator.api.biomesource;

import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.biome.api.data.BiomeDataRegistry;
import de.ambertation.wover.biome.api.data.BiomeGenerationDataContainer;
import de.ambertation.wover.entrypoint.LibWoverBiome;
import de.ambertation.wover.generator.impl.biomesource.WoverBiomeDataImpl;
import de.ambertation.wover.state.api.WorldState;

import com.mojang.datafixers.util.*;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The {@link BiomeData} subtype used by {@link WoverBiomeBuilder} (registered as the {@code wover:wover_data}
 * {@link BiomeData} type, see {@link de.ambertation.wover.biome.api.data.BiomeCodecRegistry}). Adds the
 * placement data WoVer's own {@link de.ambertation.wover.generator.api.biomesource.WoverBiomeSource
 * WoverBiomeSource} implementations understand on top of the base {@link BiomeData} fields: an
 * {@link #edge edge} biome, a {@link #parent parent} biome for sub-biome placement, and the
 * {@link #terrainHeight}/{@link #genChance}/{@link #edgeSize}/{@link #vertical} tuning values.
 * <p>
 * Instances are normally produced by {@link WoverBiomeBuilder} through datagen rather than constructed
 * directly.
 */
public class WoverBiomeData extends BiomeData {
    /**
     * The {@link MapCodec} for the base {@link WoverBiomeData} class.
     */
    public static final MapCodec<WoverBiomeData> CODEC = codec(WoverBiomeData::new);
    /**
     * The {@link KeyDispatchDataCodec} for the base {@link WoverBiomeData} class, as returned by
     * {@link #codec()}.
     */
    public static final KeyDispatchDataCodec<WoverBiomeData> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);

    /**
     * The terrain height hint this Biome was placed with.
     */
    public final float terrainHeight;
    /**
     * The relative weight this Biome (or, if {@link #parent} is set, this sub-biome) is picked with.
     */
    public final float genChance;
    /**
     * The size of the {@link #edge} biome border, in the same units as the BiomeSource's biome size.
     */
    public final int edgeSize;
    /**
     * Whether the {@link #edge} biome is generated as a vertical (height-based) transition instead of a
     * horizontal one.
     */
    public final boolean vertical;
    /**
     * The key of the edge biome that generates at the border of this Biome, or {@code null} if this Biome
     * has no edge.
     */
    public final @Nullable ResourceKey<Biome> edge;
    /**
     * The {@link BiomeData} key derived from {@link #edge}, or {@code null} if this Biome has no edge.
     */
    public final @Nullable ResourceKey<BiomeData> edgeData;
    /**
     * The key of the parent biome this Biome is a sub-biome (alternative) of, or {@code null} if this
     * Biome is not a sub-biome.
     */
    public final @Nullable ResourceKey<Biome> parent;
    /**
     * The {@link BiomeData} key derived from {@link #parent}, or {@code null} if this Biome is not a
     * sub-biome.
     */
    public final @Nullable ResourceKey<BiomeData> parentData;

    /**
     * Creates a new instance.
     *
     * @param fogDensity     The fog density of the Biome.
     * @param biome          The key of the Biome this data belongs to.
     * @param generationData The climate parameters and intended placement tag of the Biome.
     * @param terrainHeight  The terrain height hint of the Biome.
     * @param genChance      The relative weight the Biome (or sub-biome) is picked with.
     * @param edgeSize       The size of the edge biome border.
     * @param vertical       Whether the edge biome is a vertical transition.
     * @param edge           The key of the edge biome, or {@code null} for none.
     * @param parent         The key of the parent biome, or {@code null} if this is not a sub-biome.
     */
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

    /**
     * Creates a plain {@link WoverBiomeData} instance for the given Biome, with default fog density,
     * terrain height and gen chance, no edge and no parent.
     *
     * @param biome The key of the Biome.
     * @return The new instance.
     */
    public static WoverBiomeData of(ResourceKey<Biome> biome) {
        return new WoverBiomeData(1.0f, biome, BiomeGenerationDataContainer.EMPTY, 0.1f, 1.0f, 0, false, null, null);
    }

    /**
     * Creates a {@link WoverBiomeData} instance for the given Biome with an {@link #edge} biome and a
     * default {@link #edgeSize} of {@code 4}.
     *
     * @param biome The key of the Biome.
     * @param edge  The key of the edge biome.
     * @return The new instance.
     */
    public static WoverBiomeData withEdge(ResourceKey<Biome> biome, ResourceKey<Biome> edge) {
        return new WoverBiomeData(1.0f, biome, BiomeGenerationDataContainer.EMPTY, 0.1f, 1.0f, 4, false, edge, null);
    }

    /**
     * Creates a temporary, in-memory {@link WoverBiomeData} instance (see {@link #isTemp()}) for the given
     * Biome with an {@link #edge} biome and a default {@link #edgeSize} of {@code 4}, not backed by the
     * {@link de.ambertation.wover.biome.api.data.BiomeDataRegistry BiomeDataRegistry}.
     *
     * @param biome The key of the Biome.
     * @param edge  The key of the edge biome.
     * @return The new instance.
     */
    public static WoverBiomeData tempWithEdge(ResourceKey<Biome> biome, ResourceKey<Biome> edge) {
        return new WoverBiomeData.InMemoryWoverBiomeData(
                1.0f,
                biome,
                BiomeGenerationDataContainer.EMPTY,
                0.1f,
                1.0f,
                4,
                false,
                edge,
                null
        );
    }

    /**
     * Creates a {@link MapCodec} for a {@link WoverBiomeData} subtype that adds no additional fields on top
     * of the base {@link #terrainHeight}, {@link #genChance}, {@link #edgeSize}, {@link #vertical},
     * {@link #edge} and {@link #parent} fields.
     * <p>
     * This is the base overload of a family of {@code codec} methods that let a {@link WoverBiomeData}
     * subclass add up to seven additional {@link RecordCodecBuilder} fields ({@code p10}...{@code p16}) on
     * top of the base fields, matched by a factory function taking the corresponding number of arguments —
     * mirroring the {@link BiomeData#codec} family this class is itself built on. Use
     * {@link de.ambertation.wover.biome.api.data.BiomeCodecRegistry#register(net.minecraft.resources.ResourceLocation, net.minecraft.util.KeyDispatchDataCodec)}
     * to make the resulting codec usable from a {@code type} field.
     *
     * @param factory the factory used to construct the subclass from the decoded fields
     * @param <T>     the {@link WoverBiomeData} subtype
     * @return the {@link MapCodec}
     */
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

    /**
     * Overload of {@link #codec(Function9)} that adds one extra field ({@code p10}) on top of the base
     * fields.
     *
     * @param p10     the extra field
     * @param factory the factory used to construct the subclass from the decoded fields
     * @param <T>     the {@link WoverBiomeData} subtype
     * @param <P10>   the type of the extra field
     * @return the {@link MapCodec}
     */
    public static <T extends WoverBiomeData, P10> MapCodec<T> codec(
            final RecordCodecBuilder<T, P10> p10,
            final Function10<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, T> factory
    ) {
        WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
        return codec(
                a.t0, a.t1, a.t2, a.t3, a.t4, a.t5, p10,
                (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9) -> factory.apply(
                        w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null), w9
                )
        );
    }

    /**
     * Overload of {@link #codec(Function9)} that adds two extra fields ({@code p10}, {@code p11}) on top of
     * the base fields.
     *
     * @param p10     the first extra field
     * @param p11     the second extra field
     * @param factory the factory used to construct the subclass from the decoded fields
     * @param <T>     the {@link WoverBiomeData} subtype
     * @param <P10>   the type of the first extra field
     * @param <P11>   the type of the second extra field
     * @return the {@link MapCodec}
     */
    public static <T extends WoverBiomeData, P10, P11> MapCodec<T> codec(
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final Function11<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, P11, T> factory
    ) {
        WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
        return codec(
                a.t0, a.t1, a.t2, a.t3, a.t4, a.t5, p10, p11,
                (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10) -> factory.apply(
                        w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null), w9, w10
                )
        );
    }

    /**
     * Overload of {@link #codec(Function9)} that adds three extra fields ({@code p10}-{@code p12}) on top
     * of the base fields.
     *
     * @param p10     the first extra field
     * @param p11     the second extra field
     * @param p12     the third extra field
     * @param factory the factory used to construct the subclass from the decoded fields
     * @param <T>     the {@link WoverBiomeData} subtype
     * @return the {@link MapCodec}
     */
    public static <T extends WoverBiomeData, P10, P11, P12> MapCodec<T> codec(
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final RecordCodecBuilder<T, P12> p12,
            final Function12<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, P11, P12, T> factory
    ) {
        WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
        return codec(
                a.t0, a.t1, a.t2, a.t3, a.t4, a.t5, p10, p11, p12,
                (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11) -> factory.apply(
                        w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null), w9, w10, w11
                )
        );
    }

    /**
     * Overload of {@link #codec(Function9)} that adds four extra fields ({@code p10}-{@code p13}) on top of
     * the base fields.
     *
     * @param p10     the first extra field
     * @param p11     the second extra field
     * @param p12     the third extra field
     * @param p13     the fourth extra field
     * @param factory the factory used to construct the subclass from the decoded fields
     * @param <T>     the {@link WoverBiomeData} subtype
     * @return the {@link MapCodec}
     */
    public static <T extends WoverBiomeData, P10, P11, P12, P13> MapCodec<T> codec(
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final RecordCodecBuilder<T, P12> p12,
            final RecordCodecBuilder<T, P13> p13,
            final Function13<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, P11, P12, P13, T> factory
    ) {
        WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
        return codec(
                a.t0, a.t1, a.t2, a.t3, a.t4, a.t5, p10, p11, p12, p13,
                (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12) -> factory.apply(
                        w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null), w9, w10, w11, w12
                )
        );
    }

    /**
     * Overload of {@link #codec(Function9)} that adds five extra fields ({@code p10}-{@code p14}) on top of
     * the base fields.
     *
     * @param p10     the first extra field
     * @param p11     the second extra field
     * @param p12     the third extra field
     * @param p13     the fourth extra field
     * @param p14     the fifth extra field
     * @param factory the factory used to construct the subclass from the decoded fields
     * @param <T>     the {@link WoverBiomeData} subtype
     * @return the {@link MapCodec}
     */
    public static <T extends WoverBiomeData, P10, P11, P12, P13, P14> MapCodec<T> codec(
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final RecordCodecBuilder<T, P12> p12,
            final RecordCodecBuilder<T, P13> p13,
            final RecordCodecBuilder<T, P14> p14,
            final Function14<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, Float, Float, Integer, Boolean, ResourceKey<Biome>, ResourceKey<Biome>, P10, P11, P12, P13, P14, T> factory
    ) {
        WoverBiomeDataImpl.CodecAttributes<T> a = new WoverBiomeDataImpl.CodecAttributes<>();
        return codec(
                a.t0, a.t1, a.t2, a.t3, a.t4, a.t5, p10, p11, p12, p13, p14,
                (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12, w13) -> factory.apply(
                        w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null),
                        w9, w10, w11, w12, w13
                )
        );
    }

    /**
     * Overload of {@link #codec(Function9)} that adds six extra fields ({@code p10}-{@code p15}) on top of
     * the base fields.
     *
     * @param p10     the first extra field
     * @param p11     the second extra field
     * @param p12     the third extra field
     * @param p13     the fourth extra field
     * @param p14     the fifth extra field
     * @param p15     the sixth extra field
     * @param factory the factory used to construct the subclass from the decoded fields
     * @param <T>     the {@link WoverBiomeData} subtype
     * @return the {@link MapCodec}
     */
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
        return codec(
                a.t0, a.t1, a.t2, a.t3, a.t4, a.t5, p10, p11, p12, p13, p14, p15,
                (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12, w13, w14) -> factory.apply(
                        w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null),
                        w9, w10, w11, w12, w13, w14
                )
        );
    }

    /**
     * Overload of {@link #codec(Function9)} that adds seven extra fields ({@code p10}-{@code p16}) on top
     * of the base fields.
     *
     * @param p10     the first extra field
     * @param p11     the second extra field
     * @param p12     the third extra field
     * @param p13     the fourth extra field
     * @param p14     the fifth extra field
     * @param p15     the sixth extra field
     * @param p16     the seventh extra field
     * @param factory the factory used to construct the subclass from the decoded fields
     * @param <T>     the {@link WoverBiomeData} subtype
     * @return the {@link MapCodec}
     */
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
        return codec(
                a.t0, a.t1, a.t2, a.t3, a.t4, a.t5, p10, p11, p12, p13, p14, p15, p16,
                (w0, w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12, w13, w14, w15) -> factory.apply(
                        w0, w1, w2, w3, w4, w5, w6, w7.orElse(null), w8.orElse(null),
                        w9, w10, w11, w12, w13, w14, w15
                )
        );
    }


    /**
     * Looks up the current {@link BiomeDataRegistry#BIOME_DATA_REGISTRY}, falling back to the
     * not-yet-finalized {@link WorldState#allStageRegistryAccess()} (and logging a one-time warning) if the
     * finalized {@link WorldState#registryAccess()} is not ready yet.
     *
     * @param forWhat  A short description of why the registry is being accessed, used in log/error messages.
     * @param ofBiome  The Biome the access is performed for, used in log/error messages.
     * @return The {@link BiomeData} registry.
     * @throws IllegalStateException if no registry access (finalized or not) is available yet.
     */
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
                ? acc.lookup(BiomeDataRegistry.BIOME_DATA_REGISTRY).orElse(null)
                : null;

        if (reg == null)
            throw new IllegalStateException("Accessing " + forWhat + " of " + ofBiome + " before biome data registry is ready!");

        return reg;
    }

    private @Nullable Optional<WoverBiomeData> edgeParent = null;

    /**
     * Searches the {@link BiomeData} registry for the {@link WoverBiomeData} that uses this Biome as its
     * {@link #edge}, if any. The result is cached after the first lookup.
     *
     * @return The {@link WoverBiomeData} this Biome is the edge of, or {@code null} if this Biome is not
     * used as an edge.
     */
    public WoverBiomeData findEdgeParent() {
        //null means, that we did not yet check for an edge parent
        if (edgeParent != null) return edgeParent.orElse(null);

        final Registry<BiomeData> reg = getDataRegistry("edge parent", biomeKey);

        // Registry#entrySet() iterates MappedRegistry.byKey, a HashMap<ResourceKey, ...>, and ResourceKey
        // hashes by JVM identity - so with more than one candidate, "the first match" used to be a
        // different Biome on every boot. isPickable() only looks at null/non-null, but this is public API
        // and the instance itself is cached, so pick the lowest ResourceLocation deterministically.
        final WoverBiomeData found = reg
                .entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .filter(data -> data instanceof WoverBiomeData b && this.isSame(b.edge))
                .map(data -> (WoverBiomeData) data)
                .min(Comparator.comparing(b -> b.biomeKey.location().toString()))
                .orElse(null);

        edgeParent = Optional.ofNullable(found);
        return found;
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

    /**
     * {@inheritDoc}
     *
     * @return {@link #genChance}.
     */
    @Override
    public float genChance() {
        return this.genChance;
    }

    /**
     * Resolves the {@link BiomeData} of this Biome's {@link #edge} biome.
     *
     * @return The {@link BiomeData} of {@link #edge}, or {@code null} if this Biome has no edge, or the
     * edge's data could not be resolved.
     */
    public @Nullable BiomeData getEdgeData() {
        if (edgeData == null) return null;
        final Registry<BiomeData> reg = getDataRegistry("edge biome", biomeKey);
        return reg.get(edgeData).map(Holder.Reference::value).orElse(null);
    }

    /**
     * Resolves the {@link BiomeData} of this Biome's {@link #parent} biome.
     *
     * @return The {@link BiomeData} of {@link #parent}, or {@code null} if this Biome has no parent, or the
     * parent's data could not be resolved.
     */
    public @Nullable BiomeData getParentData() {
        if (parentData == null) return null;
        final Registry<BiomeData> reg = getDataRegistry("parent biome", biomeKey);
        return reg.get(parentData).map(Holder.Reference::value).orElse(null);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link #KEY_CODEC}.
     */
    public KeyDispatchDataCodec<? extends WoverBiomeData> codec() {
        return KEY_CODEC;
    }

    /**
     * A temporary, in-memory {@link WoverBiomeData} instance that is not backed by the
     * {@link BiomeDataRegistry}, returned by {@link #tempWithEdge(ResourceKey, ResourceKey)}.
     */
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
