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

public class WorldPresetBootstrapContext implements BootstapContext<WorldPreset> {
    public final HolderGetter<NoiseGeneratorSettings> noiseSettings;
    public final HolderGetter<Biome> biomes;
    public final HolderGetter<PlacedFeature> placedFeatures;
    public final HolderGetter<StructureSet> structureSets;
    public final HolderGetter<MultiNoiseBiomeSourceParameterList> parameterLists;


    public final LevelStem overworldStem;
    public final Holder<DimensionType> overworldDimensionType;

    public final LevelStem netherStem;
    public final Holder<DimensionType> netherDimensionType;
    public final StemContext netherContext;

    public final LevelStem endStem;
    public final Holder<DimensionType> endDimensionType;
    public final StemContext endContext;

    private final BootstapContext<WorldPreset> context;

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

    private LevelStem makeNoiseBasedOverworld(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> holder) {
        return this.makeOverworld(new NoiseBasedChunkGenerator(biomeSource, holder));
    }

    @Override
    public Holder.Reference<WorldPreset> register(
            ResourceKey<WorldPreset> resourceKey,
            WorldPreset object,
            Lifecycle lifecycle
    ) {
        return context.register(resourceKey, object, lifecycle);
    }

    @Override
    public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> resourceKey) {
        return context.lookup(resourceKey);
    }

    public static class StemContext {
        public final Holder<DimensionType> dimension;
        public final HolderGetter<StructureSet> structureSets;
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
    }
}
