package de.ambertation.wover.biome.api.data;

import de.ambertation.wover.biome.impl.data.BiomeDataImpl;
import de.ambertation.wover.entrypoint.LibWoverBiome;
import de.ambertation.wover.state.api.WorldState;

import com.mojang.datafixers.util.*;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.biome.Biome;

import java.util.Objects;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Additional, non-vanilla data that can be attached to a {@link Biome}.
 * <p>
 * {@link BiomeData} is managed in the Datapack backed {@link BiomeDataRegistry}, using the same location as
 * the {@link Biome} it belongs to (see {@link BiomeDataRegistry#createKey(ResourceKey)}). It currently
 * stores the fog density and the {@link BiomeGenerationDataContainer} (climate parameters and intended
 * placement tag) that were used to place the Biome, but can be subclassed to attach arbitrary additional
 * data — see the {@link #codec} family of methods and {@link BiomeCodecRegistry}.
 * <p>
 * Instances are normally not created directly; use {@link de.ambertation.wover.biome.api.builder.BiomeBuilder}
 * (through {@link de.ambertation.wover.biome.api.BiomeManager#vanilla(net.minecraft.resources.Identifier)}
 * or {@link de.ambertation.wover.biome.api.BiomeManager#wrapped(ResourceKey)}) instead. Once the world is
 * loaded, look up the {@link BiomeData} for a Biome using
 * {@link de.ambertation.wover.biome.api.BiomeManager#biomeData(Identifier)} or
 * {@link de.ambertation.wover.biome.api.BiomeManager#biomeDataForHolder(Holder)}.
 */
public class BiomeData {
    /**
     * The {@link MapCodec} for the base {@link BiomeData} class.
     */
    public static final MapCodec<BiomeData> CODEC = codec(BiomeData::new);
    /**
     * The {@link KeyDispatchDataCodec} for the base {@link BiomeData} class, as returned by {@link #codec()}.
     */
    public static final KeyDispatchDataCodec<BiomeData> KEY_CODEC = KeyDispatchDataCodec.of(CODEC);
    /**
     * The key of the {@link Biome} this data belongs to.
     */
    @NotNull
    public final ResourceKey<Biome> biomeKey;

    /**
     * The fog density that was configured for the Biome.
     */
    public final float fogDensity;

    /**
     * The climate parameters and intended placement tag that were used to place the Biome.
     */
    @NotNull
    public final BiomeGenerationDataContainer generationData;


    /**
     * Counts how often {@link #biomeHolder()}/{@link #biome()} were accessed before the registry was ready,
     * so the resulting warning is only logged a limited number of times.
     */
    protected static int preFinalAccessWarning = 0;

    /**
     * Creates a new instance.
     *
     * @param fogDensity     The fog density of the Biome.
     * @param biome          The key of the Biome this data belongs to.
     * @param generationData The climate parameters and intended placement tag of the Biome.
     */
    public BiomeData(
            float fogDensity,
            @NotNull ResourceKey<Biome> biome,
            @NotNull BiomeGenerationDataContainer generationData
    ) {
        this.fogDensity = fogDensity;
        biomeKey = biome;
        this.generationData = generationData;
    }

    /**
     * Creates a plain {@link BiomeData} instance for the given Biome, with a fog density of {@code 1.0} and
     * no climate parameters/intended placement.
     *
     * @param biome The key of the Biome.
     * @return The new instance.
     */
    public static @NotNull BiomeData of(ResourceKey<Biome> biome) {
        return new BiomeData(1.0f, biome, BiomeGenerationDataContainer.EMPTY);
    }

    /**
     * Creates a temporary, in-memory {@link BiomeData} instance for the given Biome that is not backed by
     * the {@link BiomeDataRegistry}.
     * <p>
     * This is mainly useful for testing or as a placeholder, before the actual {@link BiomeData} was loaded.
     *
     * @param biome The key of the Biome.
     * @return The new instance.
     */
    public static @NotNull BiomeData tempOf(ResourceKey<Biome> biome) {
        return new BiomeDataImpl.InMemoryBiomeData(1.0f, biome, BiomeGenerationDataContainer.EMPTY);
    }

    /**
     * Creates a {@link MapCodec} for a {@link BiomeData} subtype that adds no additional fields on top of
     * the base {@link #fogDensity}, {@link #biomeKey} and {@link #generationData}.
     * <p>
     * This is the base overload of a family of {@code codec} methods that let a {@link BiomeData} subclass
     * add up to thirteen additional {@link RecordCodecBuilder} fields ({@code p4}...{@code p16}) on top of
     * the base fields, matched by a factory function taking the corresponding number of arguments. Use
     * {@link BiomeCodecRegistry#register(Identifier, KeyDispatchDataCodec)} to make the resulting codec
     * available for datapack loading.
     *
     * @param factory The factory that creates the subtype instance from the decoded fields.
     * @param <T>     The concrete {@link BiomeData} subtype.
     * @return The map codec.
     */
    public static <T extends BiomeData> MapCodec<T> codec(
            final Function3<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, T> factory
    ) {
        BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
        return RecordCodecBuilder.mapCodec(
                instance -> instance.group(a.t0, a.t1, a.t2)
                                    .apply(instance, factory)
        );
    }

    /**
     * Creates a {@link MapCodec} for a {@link BiomeData} subtype that adds one additional field on top of the
     * base fields.
     *
     * @param p4      The {@link RecordCodecBuilder} for the additional field.
     * @param factory The factory that creates the subtype instance from the decoded fields.
     * @param <T>     The concrete {@link BiomeData} subtype.
     * @param <P4>    The type of the additional field.
     * @return The map codec.
     * @see #codec(Function3)
     */
    public static <T extends BiomeData, P4> MapCodec<T> codec(
            final RecordCodecBuilder<T, P4> p4,
            final Function4<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, T> factory
    ) {
        BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
        return RecordCodecBuilder.mapCodec(
                instance -> instance.group(a.t0, a.t1, a.t2, p4)
                                    .apply(instance, factory)
        );
    }

    /**
     * Creates a {@link MapCodec} for a {@link BiomeData} subtype that adds 2 additional fields on top of
     * the base fields.
     *
     * @param p4     The {@link RecordCodecBuilder} for additional field 1.
     * @param p5     The {@link RecordCodecBuilder} for additional field 2.
     * @param factory The factory that creates the subtype instance from the decoded fields.
     * @param <T>     The concrete {@link BiomeData} subtype.
     * @param <P4>   The type of field P4.
     * @param <P5>   The type of field P5.
     * @return The map codec.
     * @see #codec(Function3)
     */
    public static <T extends BiomeData, P4, P5> MapCodec<T> codec(
            final RecordCodecBuilder<T, P4> p4,
            final RecordCodecBuilder<T, P5> p5,
            final Function5<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, T> factory
    ) {
        BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
        return RecordCodecBuilder.mapCodec(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5)
                                    .apply(instance, factory)
        );
    }

    /**
     * Creates a {@link MapCodec} for a {@link BiomeData} subtype that adds 3 additional fields on top of
     * the base fields.
     *
     * @param p4     The {@link RecordCodecBuilder} for additional field 1.
     * @param p5     The {@link RecordCodecBuilder} for additional field 2.
     * @param p6     The {@link RecordCodecBuilder} for additional field 3.
     * @param factory The factory that creates the subtype instance from the decoded fields.
     * @param <T>     The concrete {@link BiomeData} subtype.
     * @param <P4>   The type of field P4.
     * @param <P5>   The type of field P5.
     * @param <P6>   The type of field P6.
     * @return The map codec.
     * @see #codec(Function3)
     */
    public static <T extends BiomeData, P4, P5, P6> MapCodec<T> codec(
            final RecordCodecBuilder<T, P4> p4,
            final RecordCodecBuilder<T, P5> p5,
            final RecordCodecBuilder<T, P6> p6,
            final Function6<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, P6, T> factory
    ) {
        BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
        return RecordCodecBuilder.mapCodec(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6)
                                    .apply(instance, factory)
        );
    }

    /**
     * Creates a {@link MapCodec} for a {@link BiomeData} subtype that adds 4 additional fields on top of
     * the base fields.
     *
     * @param p4     The {@link RecordCodecBuilder} for additional field 1.
     * @param p5     The {@link RecordCodecBuilder} for additional field 2.
     * @param p6     The {@link RecordCodecBuilder} for additional field 3.
     * @param p7     The {@link RecordCodecBuilder} for additional field 4.
     * @param factory The factory that creates the subtype instance from the decoded fields.
     * @param <T>     The concrete {@link BiomeData} subtype.
     * @param <P4>   The type of field P4.
     * @param <P5>   The type of field P5.
     * @param <P6>   The type of field P6.
     * @param <P7>   The type of field P7.
     * @return The map codec.
     * @see #codec(Function3)
     */
    public static <T extends BiomeData, P4, P5, P6, P7> MapCodec<T> codec(
            final RecordCodecBuilder<T, P4> p4,
            final RecordCodecBuilder<T, P5> p5,
            final RecordCodecBuilder<T, P6> p6,
            final RecordCodecBuilder<T, P7> p7,
            final Function7<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, P6, P7, T> factory
    ) {
        BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
        return RecordCodecBuilder.mapCodec(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7)
                                    .apply(instance, factory)
        );
    }

    /**
     * Creates a {@link MapCodec} for a {@link BiomeData} subtype that adds 5 additional fields on top of
     * the base fields.
     *
     * @param p4     The {@link RecordCodecBuilder} for additional field 1.
     * @param p5     The {@link RecordCodecBuilder} for additional field 2.
     * @param p6     The {@link RecordCodecBuilder} for additional field 3.
     * @param p7     The {@link RecordCodecBuilder} for additional field 4.
     * @param p8     The {@link RecordCodecBuilder} for additional field 5.
     * @param factory The factory that creates the subtype instance from the decoded fields.
     * @param <T>     The concrete {@link BiomeData} subtype.
     * @param <P4>   The type of field P4.
     * @param <P5>   The type of field P5.
     * @param <P6>   The type of field P6.
     * @param <P7>   The type of field P7.
     * @param <P8>   The type of field P8.
     * @return The map codec.
     * @see #codec(Function3)
     */
    public static <T extends BiomeData, P4, P5, P6, P7, P8> MapCodec<T> codec(
            final RecordCodecBuilder<T, P4> p4,
            final RecordCodecBuilder<T, P5> p5,
            final RecordCodecBuilder<T, P6> p6,
            final RecordCodecBuilder<T, P7> p7,
            final RecordCodecBuilder<T, P8> p8,
            final Function8<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, P6, P7, P8, T> factory
    ) {
        BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
        return RecordCodecBuilder.mapCodec(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8)
                                    .apply(instance, factory)
        );
    }

    /**
     * Creates a {@link MapCodec} for a {@link BiomeData} subtype that adds 6 additional fields on top of
     * the base fields.
     *
     * @param p4     The {@link RecordCodecBuilder} for additional field 1.
     * @param p5     The {@link RecordCodecBuilder} for additional field 2.
     * @param p6     The {@link RecordCodecBuilder} for additional field 3.
     * @param p7     The {@link RecordCodecBuilder} for additional field 4.
     * @param p8     The {@link RecordCodecBuilder} for additional field 5.
     * @param p9     The {@link RecordCodecBuilder} for additional field 6.
     * @param factory The factory that creates the subtype instance from the decoded fields.
     * @param <T>     The concrete {@link BiomeData} subtype.
     * @param <P4>   The type of field P4.
     * @param <P5>   The type of field P5.
     * @param <P6>   The type of field P6.
     * @param <P7>   The type of field P7.
     * @param <P8>   The type of field P8.
     * @param <P9>   The type of field P9.
     * @return The map codec.
     * @see #codec(Function3)
     */
    public static <T extends BiomeData, P4, P5, P6, P7, P8, P9> MapCodec<T> codec(
            final RecordCodecBuilder<T, P4> p4,
            final RecordCodecBuilder<T, P5> p5,
            final RecordCodecBuilder<T, P6> p6,
            final RecordCodecBuilder<T, P7> p7,
            final RecordCodecBuilder<T, P8> p8,
            final RecordCodecBuilder<T, P9> p9,
            final Function9<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, P6, P7, P8, P9, T> factory
    ) {
        BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
        return RecordCodecBuilder.mapCodec(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9)
                                    .apply(instance, factory)
        );
    }

    /**
     * Creates a {@link MapCodec} for a {@link BiomeData} subtype that adds 7 additional fields on top of
     * the base fields.
     *
     * @param p4     The {@link RecordCodecBuilder} for additional field 1.
     * @param p5     The {@link RecordCodecBuilder} for additional field 2.
     * @param p6     The {@link RecordCodecBuilder} for additional field 3.
     * @param p7     The {@link RecordCodecBuilder} for additional field 4.
     * @param p8     The {@link RecordCodecBuilder} for additional field 5.
     * @param p9     The {@link RecordCodecBuilder} for additional field 6.
     * @param p10    The {@link RecordCodecBuilder} for additional field 7.
     * @param factory The factory that creates the subtype instance from the decoded fields.
     * @param <T>     The concrete {@link BiomeData} subtype.
     * @param <P4>   The type of field P4.
     * @param <P5>   The type of field P5.
     * @param <P6>   The type of field P6.
     * @param <P7>   The type of field P7.
     * @param <P8>   The type of field P8.
     * @param <P9>   The type of field P9.
     * @param <P10>  The type of field P10.
     * @return The map codec.
     * @see #codec(Function3)
     */
    public static <T extends BiomeData, P4, P5, P6, P7, P8, P9, P10> MapCodec<T> codec(
            final RecordCodecBuilder<T, P4> p4,
            final RecordCodecBuilder<T, P5> p5,
            final RecordCodecBuilder<T, P6> p6,
            final RecordCodecBuilder<T, P7> p7,
            final RecordCodecBuilder<T, P8> p8,
            final RecordCodecBuilder<T, P9> p9,
            final RecordCodecBuilder<T, P10> p10,
            final Function10<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, P6, P7, P8, P9, P10, T> factory
    ) {
        BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
        return RecordCodecBuilder.mapCodec(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9, p10)
                                    .apply(instance, factory)
        );
    }

    /**
     * Creates a {@link MapCodec} for a {@link BiomeData} subtype that adds 8 additional fields on top of
     * the base fields.
     *
     * @param p4     The {@link RecordCodecBuilder} for additional field 1.
     * @param p5     The {@link RecordCodecBuilder} for additional field 2.
     * @param p6     The {@link RecordCodecBuilder} for additional field 3.
     * @param p7     The {@link RecordCodecBuilder} for additional field 4.
     * @param p8     The {@link RecordCodecBuilder} for additional field 5.
     * @param p9     The {@link RecordCodecBuilder} for additional field 6.
     * @param p10    The {@link RecordCodecBuilder} for additional field 7.
     * @param p11    The {@link RecordCodecBuilder} for additional field 8.
     * @param factory The factory that creates the subtype instance from the decoded fields.
     * @param <T>     The concrete {@link BiomeData} subtype.
     * @param <P4>   The type of field P4.
     * @param <P5>   The type of field P5.
     * @param <P6>   The type of field P6.
     * @param <P7>   The type of field P7.
     * @param <P8>   The type of field P8.
     * @param <P9>   The type of field P9.
     * @param <P10>  The type of field P10.
     * @param <P11>  The type of field P11.
     * @return The map codec.
     * @see #codec(Function3)
     */
    public static <T extends BiomeData, P4, P5, P6, P7, P8, P9, P10, P11> MapCodec<T> codec(
            final RecordCodecBuilder<T, P4> p4,
            final RecordCodecBuilder<T, P5> p5,
            final RecordCodecBuilder<T, P6> p6,
            final RecordCodecBuilder<T, P7> p7,
            final RecordCodecBuilder<T, P8> p8,
            final RecordCodecBuilder<T, P9> p9,
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final Function11<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, P6, P7, P8, P9, P10, P11, T> factory
    ) {
        BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
        return RecordCodecBuilder.mapCodec(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9, p10, p11)
                                    .apply(instance, factory)
        );
    }

    /**
     * Creates a {@link MapCodec} for a {@link BiomeData} subtype that adds 9 additional fields on top of
     * the base fields.
     *
     * @param p4     The {@link RecordCodecBuilder} for additional field 1.
     * @param p5     The {@link RecordCodecBuilder} for additional field 2.
     * @param p6     The {@link RecordCodecBuilder} for additional field 3.
     * @param p7     The {@link RecordCodecBuilder} for additional field 4.
     * @param p8     The {@link RecordCodecBuilder} for additional field 5.
     * @param p9     The {@link RecordCodecBuilder} for additional field 6.
     * @param p10    The {@link RecordCodecBuilder} for additional field 7.
     * @param p11    The {@link RecordCodecBuilder} for additional field 8.
     * @param p12    The {@link RecordCodecBuilder} for additional field 9.
     * @param factory The factory that creates the subtype instance from the decoded fields.
     * @param <T>     The concrete {@link BiomeData} subtype.
     * @param <P4>   The type of field P4.
     * @param <P5>   The type of field P5.
     * @param <P6>   The type of field P6.
     * @param <P7>   The type of field P7.
     * @param <P8>   The type of field P8.
     * @param <P9>   The type of field P9.
     * @param <P10>  The type of field P10.
     * @param <P11>  The type of field P11.
     * @param <P12>  The type of field P12.
     * @return The map codec.
     * @see #codec(Function3)
     */
    public static <T extends BiomeData, P4, P5, P6, P7, P8, P9, P10, P11, P12> MapCodec<T> codec(
            final RecordCodecBuilder<T, P4> p4,
            final RecordCodecBuilder<T, P5> p5,
            final RecordCodecBuilder<T, P6> p6,
            final RecordCodecBuilder<T, P7> p7,
            final RecordCodecBuilder<T, P8> p8,
            final RecordCodecBuilder<T, P9> p9,
            final RecordCodecBuilder<T, P10> p10,
            final RecordCodecBuilder<T, P11> p11,
            final RecordCodecBuilder<T, P12> p12,
            final Function12<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, P6, P7, P8, P9, P10, P11, P12, T> factory
    ) {
        BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
        return RecordCodecBuilder.mapCodec(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9, p10, p11, p12)
                                    .apply(instance, factory)
        );
    }

    /**
     * Creates a {@link MapCodec} for a {@link BiomeData} subtype that adds 10 additional fields on top of
     * the base fields.
     *
     * @param p4     The {@link RecordCodecBuilder} for additional field 1.
     * @param p5     The {@link RecordCodecBuilder} for additional field 2.
     * @param p6     The {@link RecordCodecBuilder} for additional field 3.
     * @param p7     The {@link RecordCodecBuilder} for additional field 4.
     * @param p8     The {@link RecordCodecBuilder} for additional field 5.
     * @param p9     The {@link RecordCodecBuilder} for additional field 6.
     * @param p10    The {@link RecordCodecBuilder} for additional field 7.
     * @param p11    The {@link RecordCodecBuilder} for additional field 8.
     * @param p12    The {@link RecordCodecBuilder} for additional field 9.
     * @param p13    The {@link RecordCodecBuilder} for additional field 10.
     * @param factory The factory that creates the subtype instance from the decoded fields.
     * @param <T>     The concrete {@link BiomeData} subtype.
     * @param <P4>   The type of field P4.
     * @param <P5>   The type of field P5.
     * @param <P6>   The type of field P6.
     * @param <P7>   The type of field P7.
     * @param <P8>   The type of field P8.
     * @param <P9>   The type of field P9.
     * @param <P10>  The type of field P10.
     * @param <P11>  The type of field P11.
     * @param <P12>  The type of field P12.
     * @param <P13>  The type of field P13.
     * @return The map codec.
     * @see #codec(Function3)
     */
    public static <T extends BiomeData, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13> MapCodec<T> codec(
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
            final Function13<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, T> factory
    ) {
        BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
        return RecordCodecBuilder.mapCodec(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13)
                                    .apply(instance, factory)
        );
    }

    /**
     * Creates a {@link MapCodec} for a {@link BiomeData} subtype that adds 11 additional fields on top of
     * the base fields.
     *
     * @param p4     The {@link RecordCodecBuilder} for additional field 1.
     * @param p5     The {@link RecordCodecBuilder} for additional field 2.
     * @param p6     The {@link RecordCodecBuilder} for additional field 3.
     * @param p7     The {@link RecordCodecBuilder} for additional field 4.
     * @param p8     The {@link RecordCodecBuilder} for additional field 5.
     * @param p9     The {@link RecordCodecBuilder} for additional field 6.
     * @param p10    The {@link RecordCodecBuilder} for additional field 7.
     * @param p11    The {@link RecordCodecBuilder} for additional field 8.
     * @param p12    The {@link RecordCodecBuilder} for additional field 9.
     * @param p13    The {@link RecordCodecBuilder} for additional field 10.
     * @param p14    The {@link RecordCodecBuilder} for additional field 11.
     * @param factory The factory that creates the subtype instance from the decoded fields.
     * @param <T>     The concrete {@link BiomeData} subtype.
     * @param <P4>   The type of field P4.
     * @param <P5>   The type of field P5.
     * @param <P6>   The type of field P6.
     * @param <P7>   The type of field P7.
     * @param <P8>   The type of field P8.
     * @param <P9>   The type of field P9.
     * @param <P10>  The type of field P10.
     * @param <P11>  The type of field P11.
     * @param <P12>  The type of field P12.
     * @param <P13>  The type of field P13.
     * @param <P14>  The type of field P14.
     * @return The map codec.
     * @see #codec(Function3)
     */
    public static <T extends BiomeData, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14> MapCodec<T> codec(
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
            final Function14<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, T> factory
    ) {
        BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
        return RecordCodecBuilder.mapCodec(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14)
                                    .apply(instance, factory)
        );
    }

    /**
     * Creates a {@link MapCodec} for a {@link BiomeData} subtype that adds 12 additional fields on top of
     * the base fields.
     *
     * @param p4     The {@link RecordCodecBuilder} for additional field 1.
     * @param p5     The {@link RecordCodecBuilder} for additional field 2.
     * @param p6     The {@link RecordCodecBuilder} for additional field 3.
     * @param p7     The {@link RecordCodecBuilder} for additional field 4.
     * @param p8     The {@link RecordCodecBuilder} for additional field 5.
     * @param p9     The {@link RecordCodecBuilder} for additional field 6.
     * @param p10    The {@link RecordCodecBuilder} for additional field 7.
     * @param p11    The {@link RecordCodecBuilder} for additional field 8.
     * @param p12    The {@link RecordCodecBuilder} for additional field 9.
     * @param p13    The {@link RecordCodecBuilder} for additional field 10.
     * @param p14    The {@link RecordCodecBuilder} for additional field 11.
     * @param p15    The {@link RecordCodecBuilder} for additional field 12.
     * @param factory The factory that creates the subtype instance from the decoded fields.
     * @param <T>     The concrete {@link BiomeData} subtype.
     * @param <P4>   The type of field P4.
     * @param <P5>   The type of field P5.
     * @param <P6>   The type of field P6.
     * @param <P7>   The type of field P7.
     * @param <P8>   The type of field P8.
     * @param <P9>   The type of field P9.
     * @param <P10>  The type of field P10.
     * @param <P11>  The type of field P11.
     * @param <P12>  The type of field P12.
     * @param <P13>  The type of field P13.
     * @param <P14>  The type of field P14.
     * @param <P15>  The type of field P15.
     * @return The map codec.
     * @see #codec(Function3)
     */
    public static <T extends BiomeData, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15> MapCodec<T> codec(
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
            final Function15<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, T> factory
    ) {
        BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
        return RecordCodecBuilder.mapCodec(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15)
                                    .apply(instance, factory)
        );
    }

    /**
     * Creates a {@link MapCodec} for a {@link BiomeData} subtype that adds 13 additional fields on top of
     * the base fields.
     *
     * @param p4     The {@link RecordCodecBuilder} for additional field 1.
     * @param p5     The {@link RecordCodecBuilder} for additional field 2.
     * @param p6     The {@link RecordCodecBuilder} for additional field 3.
     * @param p7     The {@link RecordCodecBuilder} for additional field 4.
     * @param p8     The {@link RecordCodecBuilder} for additional field 5.
     * @param p9     The {@link RecordCodecBuilder} for additional field 6.
     * @param p10    The {@link RecordCodecBuilder} for additional field 7.
     * @param p11    The {@link RecordCodecBuilder} for additional field 8.
     * @param p12    The {@link RecordCodecBuilder} for additional field 9.
     * @param p13    The {@link RecordCodecBuilder} for additional field 10.
     * @param p14    The {@link RecordCodecBuilder} for additional field 11.
     * @param p15    The {@link RecordCodecBuilder} for additional field 12.
     * @param p16    The {@link RecordCodecBuilder} for additional field 13.
     * @param factory The factory that creates the subtype instance from the decoded fields.
     * @param <T>     The concrete {@link BiomeData} subtype.
     * @param <P4>   The type of field P4.
     * @param <P5>   The type of field P5.
     * @param <P6>   The type of field P6.
     * @param <P7>   The type of field P7.
     * @param <P8>   The type of field P8.
     * @param <P9>   The type of field P9.
     * @param <P10>  The type of field P10.
     * @param <P11>  The type of field P11.
     * @param <P12>  The type of field P12.
     * @param <P13>  The type of field P13.
     * @param <P14>  The type of field P14.
     * @param <P15>  The type of field P15.
     * @param <P16>  The type of field P16.
     * @return The map codec.
     * @see #codec(Function3)
     */
    public static <T extends BiomeData, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16> MapCodec<T> codec(
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
            final Function16<Float, ResourceKey<Biome>, BiomeGenerationDataContainer, P4, P5, P6, P7, P8, P9, P10, P11, P12, P13, P14, P15, P16, T> factory
    ) {
        BiomeDataImpl.CodecAttributes<T> a = new BiomeDataImpl.CodecAttributes<>();
        return RecordCodecBuilder.mapCodec(
                instance -> instance.group(a.t0, a.t1, a.t2, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15, p16)
                                    .apply(instance, factory)
        );
    }

    /**
     * The {@link KeyDispatchDataCodec} that is used by {@link BiomeCodecRegistry} to (de)serialize this
     * concrete {@link BiomeData} subtype. Subclasses need to override this to return their own codec,
     * created with one of the {@link #codec} factory methods.
     *
     * @return The dispatch codec for this subtype.
     */
    public KeyDispatchDataCodec<? extends BiomeData> codec() {
        return KEY_CODEC;
    }

    /**
     * Resolves the {@link Holder} of the {@link Biome} this data belongs to.
     * <p>
     * Falls back to {@link WorldState#allStageRegistryAccess()} (logging a warning, at most 5 times) while
     * the final {@link WorldState#registryAccess()} is not yet available.
     *
     * @return The holder, or {@code null} if it could not be resolved.
     */
    public @Nullable Holder<Biome> biomeHolder() {
        if (WorldState.registryAccess() == null) {
            if (WorldState.allStageRegistryAccess() == null) return null;
            if (preFinalAccessWarning++ < 5)
                LibWoverBiome.C.log.verboseWarning("Accessing biome holder for " + biomeKey + " before registry is ready!");
            return WorldState.allStageRegistryAccess()
                             .lookupOrThrow(Registries.BIOME)
                             .get(biomeKey)
                             .orElse(null);
        }
        return WorldState.registryAccess().lookupOrThrow(Registries.BIOME).get(biomeKey).orElse(null);
    }

    /**
     * Resolves the {@link Biome} this data belongs to.
     * <p>
     * Falls back to {@link WorldState#allStageRegistryAccess()} (logging a warning, at most 5 times) while
     * the final {@link WorldState#registryAccess()} is not yet available.
     *
     * @return The Biome, or {@code null} if it could not be resolved.
     */
    public @Nullable Biome biome() {
        if (WorldState.registryAccess() == null) {
            if (WorldState.allStageRegistryAccess() == null) return null;
            if (preFinalAccessWarning++ < 5)
                LibWoverBiome.C.log.verboseWarning("Accessing biome for " + biomeKey + " before registry is ready!");
            return WorldState.allStageRegistryAccess()
                             .lookupOrThrow(Registries.BIOME)
                             .getOptional(biomeKey)
                             .orElse(null);
        }
        return WorldState.registryAccess().lookupOrThrow(Registries.BIOME).getOptional(biomeKey).orElse(null);
    }

    /**
     * Used to determine wether or not a Biome is pickable. By default this method will return @{code true}.
     *
     * @return true if the Biome is pickable, false otherwise.
     */
    public boolean isPickable() {
        return true;
    }

    /**
     * Used to determine the chance of a Biome being picked. By default this method will return @{code 1.0f}.
     *
     * @return the chance of the Biome being picked.
     */
    public float genChance() {
        return 1.0f;
    }

    /**
     * For <b>internal</b> use only! Used to determine whether this instance is a temporary, in-memory
     * placeholder created by {@link #tempOf(ResourceKey)}, not backed by the {@link BiomeDataRegistry}.
     *
     * @return {@code true} if this is a temporary instance.
     */
    @ApiStatus.Internal
    public boolean isTemp() {
        return false;
    }

    /**
     * Tests if the given biome is the same as this one.
     *
     * @param biome the biome to test
     * @return true if the given biome is the same as this one, false otherwise.
     */
    public boolean isSame(ResourceKey<Biome> biome) {
        return BiomeData.isSame(this.biomeKey, biome);
    }

    /**
     * Tests if the given biome is the same as this one.
     *
     * @param biomeA the biome to test
     * @param biomeB the second biome to test
     * @return true if the given biome is the same as this one, false otherwise.
     */
    public static boolean isSame(ResourceKey<Biome> biomeA, ResourceKey<Biome> biomeB) {
        if (biomeA == null && biomeB == null) return true;
        if (biomeA == null || biomeB == null) return false;


        return biomeA.identifier().equals(biomeB.identifier());
    }

    /**
     * Tests if the given biome is the same as this one.
     *
     * @param biome the biome to test
     * @return true if the given biome is the same as this one, false otherwise.
     */
    public boolean isSame(BiomeData biome) {
        if (biome == null) return false;
        return isSame(biome.biomeKey);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BiomeData biomeData)) return false;
        return Objects.equals(biomeKey, biomeData.biomeKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(biomeKey);
    }

    /**
     * Tests if the given tag matches the {@link BiomeGenerationDataContainer#intendedPlacement()} of this
     * Biome.
     *
     * @param tag The tag to test, or {@code null} to test for "no intended placement".
     * @return {@code true} if the tag matches the intended placement of this Biome.
     */
    public boolean isIntendedFor(@Nullable TagKey<Biome> tag) {
        if (generationData.intendedPlacement() == null) return tag == null;
        return generationData.intendedPlacement().equals(tag);
    }
}
