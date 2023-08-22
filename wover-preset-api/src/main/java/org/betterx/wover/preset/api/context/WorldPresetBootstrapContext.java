package org.betterx.wover.preset.api.context;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import org.jetbrains.annotations.NotNull;

/**
 * Special bootstrap context for world presets.
 * <p>
 * The context is created from a regular {@link BootstapContext} and provides
 * a few helper methods specialized for world presets as well as access to some
 * data relevant when creating a world preset.
 */
public class WorldPresetBootstrapContext implements BootstapContext<WorldPreset> {
    /**
     * Read-only access to the {@link NoiseGeneratorSettings} registry
     */
    public final HolderGetter<NoiseGeneratorSettings> noiseSettings;
    /**
     * Read-only access to the {@link Biome} registry
     */
    public final HolderGetter<Biome> biomes;

    /**
     * Read-only access to the {@link PlacedFeature} registry
     */
    public final HolderGetter<PlacedFeature> placedFeatures;

    /**
     * Read-only access to the {@link StructureSet} registry
     */
    public final HolderGetter<StructureSet> structureSets;

    /**
     * Read-only access to the {@link MultiNoiseBiomeSourceParameterList} registry
     */
    public final HolderGetter<MultiNoiseBiomeSourceParameterList> parameterLists;


    /**
     * The default overworld stem.
     */
    public final LevelStem overworldStem;
    /**
     * The default overworld dimension type.
     */
    public final Holder<DimensionType> overworldDimensionType;

    /**
     * The default nether stem.
     */
    public final LevelStem netherStem;
    /**
     * The builtin {@link DimensionType} for the nether.
     */
    public final Holder<DimensionType> netherDimensionType;
    /**
     * Additional Data for the nether stem.
     */
    public final StemContext netherContext;

    /**
     * The default end stem.
     */
    public final LevelStem endStem;
    /**
     * The builtin {@link DimensionType} for the end.
     */
    public final Holder<DimensionType> endDimensionType;
    /**
     * Additional Data for the end stem.
     */
    public final StemContext endContext;

    private final BootstapContext<WorldPreset> context;

    /**
     * Creates a new world preset bootstrap context from the given context.
     *
     * @param bootstapContext the original context to wrap
     */
    public WorldPresetBootstrapContext(BootstapContext<WorldPreset> bootstapContext) {
        this.context = bootstapContext;

        this.parameterLists = bootstapContext.lookup(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST);
        final HolderGetter<DimensionType> dimensionTypes = bootstapContext.lookup(Registries.DIMENSION_TYPE);

        this.noiseSettings = bootstapContext.lookup(Registries.NOISE_SETTINGS);
        this.biomes = bootstapContext.lookup(Registries.BIOME);
        this.placedFeatures = bootstapContext.lookup(Registries.PLACED_FEATURE);
        this.structureSets = bootstapContext.lookup(Registries.STRUCTURE_SET);

        this.overworldDimensionType = dimensionTypes.getOrThrow(BuiltinDimensionTypes.OVERWORLD);
        Holder.Reference<MultiNoiseBiomeSourceParameterList> overworldParameters = parameterLists
                .getOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD);
        MultiNoiseBiomeSource overworldBiomeSource = MultiNoiseBiomeSource.createFromPreset(overworldParameters);
        Holder<NoiseGeneratorSettings> defaultOverworldNoise = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.OVERWORLD);
        this.overworldStem = makeNoiseBasedOverworld(overworldBiomeSource, defaultOverworldNoise);

        this.netherDimensionType = dimensionTypes.getOrThrow(BuiltinDimensionTypes.NETHER);
        Holder.Reference<MultiNoiseBiomeSourceParameterList> netherParameters = parameterLists
                .getOrThrow(MultiNoiseBiomeSourceParameterLists.NETHER);
        Holder<NoiseGeneratorSettings> defaultNetherNoise = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.NETHER);
        this.netherStem = new LevelStem(
                netherDimensionType,
                new NoiseBasedChunkGenerator(
                        MultiNoiseBiomeSource.createFromPreset(netherParameters),
                        defaultNetherNoise
                )
        );

        this.endDimensionType = dimensionTypes.getOrThrow(BuiltinDimensionTypes.END);
        Holder<NoiseGeneratorSettings> defaultEndNoise = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.END);
        this.endStem = new LevelStem(
                endDimensionType,
                new NoiseBasedChunkGenerator(TheEndBiomeSource.create(this.biomes), defaultEndNoise)
        );

        Holder<NoiseGeneratorSettings> netherSettings, endSettings;
        if (this.netherStem.generator() instanceof NoiseBasedChunkGenerator nether) {
            netherSettings = nether.generatorSettings();
        } else {
            netherSettings = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.NETHER);
        }

        if (this.endStem.generator() instanceof NoiseBasedChunkGenerator nether) {
            endSettings = nether.generatorSettings();
        } else {
            endSettings = this.noiseSettings.getOrThrow(NoiseGeneratorSettings.END);
        }

        this.netherContext = new StemContext(
                this.netherStem.type(),
                this.structureSets,
                netherSettings
        );

        this.endContext = new StemContext(
                this.endStem.type(),
                this.structureSets,
                endSettings
        );
    }

    private LevelStem makeOverworld(ChunkGenerator chunkGenerator) {
        return new LevelStem(this.overworldDimensionType, chunkGenerator);
    }

    /**
     * Create a default overworld
     *
     * @param biomeSource the biome source to use
     * @param holder      the default generator settings
     * @return a new overworld stem
     */
    public LevelStem makeNoiseBasedOverworld(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> holder) {
        return this.makeOverworld(new NoiseBasedChunkGenerator(biomeSource, holder));
    }

    /**
     * Registers a new world preset.
     *
     * @param resourceKey the key of the preset
     * @param preset      the preset to register
     * @param lifecycle   the lifecycle of the preset
     * @return a reference to the registered preset
     */
    @Override
    public Holder.@NotNull Reference<WorldPreset> register(
            ResourceKey<WorldPreset> resourceKey,
            WorldPreset preset,
            Lifecycle lifecycle
    ) {
        return context.register(resourceKey, preset, lifecycle);
    }

    /**
     * Looks up a holder for the given registry.
     *
     * @param resourceKey the key of the registry
     * @param <S>         the type of the registry
     * @return a holder for the registry
     */
    @Override
    public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> resourceKey) {
        return context.lookup(resourceKey);
    }

    /**
     * Additional data for used when creating a {@link LevelStem} for a dimension.
     */
    public static class StemContext {
        /**
         * The suggested dimension type of the stem.
         */
        public final Holder<DimensionType> dimension;
        /**
         * Read-only access to the {@link StructureSet} registry
         */
        public final HolderGetter<StructureSet> structureSets;
        /**
         * The default {@link NoiseGeneratorSettings} for the stem.
         */
        public final Holder<NoiseGeneratorSettings> generatorSettings;

        private StemContext(
                Holder<DimensionType> dimension,
                HolderGetter<StructureSet> structureSets,
                Holder<NoiseGeneratorSettings> generatorSettings
        ) {
            this.dimension = dimension;
            this.structureSets = structureSets;
            this.generatorSettings = generatorSettings;
        }

        /**
         * Creates a new stem context.
         *
         * @param dimension         the dimension type
         * @param structureSets     the structure sets that are allowed in the dimension
         * @param generatorSettings the default generator settings for the dimension
         * @return a new stem context
         */
        public static StemContext of(
                Holder<DimensionType> dimension,
                HolderGetter<StructureSet> structureSets,
                Holder<NoiseGeneratorSettings> generatorSettings
        ) {
            return new StemContext(dimension, structureSets, generatorSettings);
        }
    }
}
