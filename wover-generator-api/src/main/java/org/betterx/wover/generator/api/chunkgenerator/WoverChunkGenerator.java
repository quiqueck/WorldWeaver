package org.betterx.wover.generator.api.chunkgenerator;

import org.betterx.wover.common.generator.api.biomesource.BiomeSourceWithNoiseRelatedSettings;
import org.betterx.wover.common.generator.api.biomesource.MergeableBiomeSource;
import org.betterx.wover.common.generator.api.biomesource.NoiseGeneratorSettingsProvider;
import org.betterx.wover.common.generator.api.biomesource.ReloadableBiomeSource;
import org.betterx.wover.common.generator.api.chunkgenerator.EnforceableChunkGenerator;
import org.betterx.wover.common.generator.api.chunkgenerator.RestorableBiomeSource;
import org.betterx.wover.common.surface.api.InjectableSurfaceRules;
import org.betterx.wover.core.api.IntegrationCore;
import org.betterx.wover.entrypoint.WoverWorldGenerator;
import org.betterx.wover.generator.impl.chunkgenerator.WoverChunkGeneratorImpl;
import org.betterx.wover.generator.mixin.generator.ChunkGeneratorAccessor;
import org.betterx.wover.surface.impl.SurfaceRuleUtil;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;

import com.google.common.base.Suppliers;

import java.util.List;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public class WoverChunkGenerator extends NoiseBasedChunkGenerator implements
        RestorableBiomeSource<WoverChunkGenerator>,
        InjectableSurfaceRules<WoverChunkGenerator>,
        EnforceableChunkGenerator<WoverChunkGenerator> {
    public static final ResourceLocation ID = WoverWorldGenerator.C.id("betterx");

    protected static final NoiseSettings NETHER_NOISE_SETTINGS_AMPLIFIED = NoiseSettings.create(0, 256, 1, 4);
    public static final ResourceKey<NoiseGeneratorSettings> AMPLIFIED_NETHER = ResourceKey.create(
            Registries.NOISE_SETTINGS,
            WoverWorldGenerator.C.id("amplified_nether")
    );

    public static final Codec<WoverChunkGenerator> CODEC = RecordCodecBuilder
            .create((RecordCodecBuilder.Instance<WoverChunkGenerator> builderInstance) -> {

                RecordCodecBuilder<WoverChunkGenerator, BiomeSource> biomeSourceCodec = BiomeSource.CODEC
                        .fieldOf("biome_source")
                        .forGetter((WoverChunkGenerator generator) -> generator.biomeSource);

                RecordCodecBuilder<WoverChunkGenerator, Holder<NoiseGeneratorSettings>> settingsCodec = NoiseGeneratorSettings.CODEC
                        .fieldOf("settings")
                        .forGetter((WoverChunkGenerator generator) -> generator.generatorSettings());


                return builderInstance.group(biomeSourceCodec, settingsCodec)
                                      .apply(builderInstance, builderInstance.stable(WoverChunkGenerator::new));
            });

    public final BiomeSource initialBiomeSource;

    public WoverChunkGenerator(
            BiomeSource biomeSource,
            Holder<NoiseGeneratorSettings> holder
    ) {
        super(biomeSource, holder);
        initialBiomeSource = biomeSource;
        if (biomeSource instanceof BiomeSourceWithNoiseRelatedSettings bcl && holder.isBound()) {
            bcl.onLoadGeneratorSettings(holder.value());
        }

        if (IntegrationCore.RUNS_TERRABLENDER) {
            WoverWorldGenerator.C.log.info("Make sure features are loaded from terrablender:"
                    + biomeSource.getClass().getName());
            //terrablender is invalidating the feature initialization
            //we redo it at this point, otherwise we will get blank biomes
            rebuildFeaturesPerStep(biomeSource);
        }
    }

    @Override
    protected @NotNull Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    private void rebuildFeaturesPerStep(BiomeSource biomeSource) {
        if (this instanceof ChunkGeneratorAccessor acc) {
            Function<Holder<Biome>, BiomeGenerationSettings> function
                    = (Holder<Biome> hh) -> hh.value().getGenerationSettings();

            acc.wover_setFeaturesPerStep(Suppliers.memoize(() -> FeatureSorter.buildFeaturesPerStep(
                    List.copyOf(biomeSource.possibleBiomes()),
                    (hh) -> function.apply(hh).features(),
                    true
            )));
        }
    }

    /**
     * Other Mods like TerraBlender might inject new BiomeSources. We undo that change after the world setup did run.
     *
     * @param dimensionKey The Dimension where this ChunkGenerator is used from
     */
    @Override
    public void restoreInitialBiomeSource(ResourceKey<LevelStem> dimensionKey) {
        if (initialBiomeSource != getBiomeSource()) {
            if (this instanceof ChunkGeneratorAccessor acc) {
                if (initialBiomeSource instanceof MergeableBiomeSource<?> bs) {
                    acc.wover_setBiomeSource(bs.mergeWithBiomeSource(getBiomeSource()));
                } else if (initialBiomeSource instanceof ReloadableBiomeSource bs) {
                    bs.reloadBiomes();
                }

                rebuildFeaturesPerStep(getBiomeSource());
            }
        }
    }

    @Override
    public String toString() {
        return "WoVer - Chunk Generator (" + Integer.toHexString(hashCode()) + ")";
    }

    // This method is injected by Terrablender.
    // We make sure terrablender does not rewrite the feature-set for our ChunkGenerator by overwriting the
    // Mixin-Method with an empty implementation
    public void appendFeaturesPerStep() {
    }

    @Override
    public Registry<LevelStem> enforceGeneratorInWorldGenSettings(
            RegistryAccess access,
            ResourceKey<LevelStem> dimensionKey,
            ResourceKey<DimensionType> dimensionTypeKey,
            ChunkGenerator loadedChunkGenerator,
            Registry<LevelStem> dimensionRegistry
    ) {
        WoverWorldGenerator.C.log.info("Enforcing Correct Generator for " + dimensionKey.location().toString() + ".");

        ChunkGenerator referenceGenerator = this;
        if (loadedChunkGenerator instanceof ChunkGeneratorAccessor generator) {
            if (loadedChunkGenerator instanceof NoiseGeneratorSettingsProvider noiseProvider) {
                if (referenceGenerator instanceof NoiseGeneratorSettingsProvider referenceProvider) {
                    final BiomeSource bs;
                    if (referenceGenerator.getBiomeSource() instanceof MergeableBiomeSource<?> mbs) {
                        bs = mbs.mergeWithBiomeSource(loadedChunkGenerator.getBiomeSource());
                    } else {
                        bs = referenceGenerator.getBiomeSource();
                    }

                    referenceProvider.wover_getNoiseGeneratorSettingHolders();
                    referenceGenerator = new WoverChunkGenerator(
                            bs,
                            noiseProvider.wover_getNoiseGeneratorSettingHolders()
                    );
                }
            }
        }

        return WoverChunkGeneratorImpl.replaceGenerator(
                dimensionKey,
                dimensionTypeKey,
                access,
                dimensionRegistry,
                referenceGenerator
        );
    }


    public static NoiseGeneratorSettings amplifiedNether(BootstapContext<NoiseGeneratorSettings> bootstapContext) {
        HolderGetter<DensityFunction> densityGetter = bootstapContext.lookup(Registries.DENSITY_FUNCTION);
        return new NoiseGeneratorSettings(
                NETHER_NOISE_SETTINGS_AMPLIFIED,
                Blocks.NETHERRACK.defaultBlockState(),
                Blocks.LAVA.defaultBlockState(),
                NoiseRouterData.noNewCaves(
                        densityGetter,
                        bootstapContext.lookup(Registries.NOISE),
                        NoiseRouterData.slideNetherLike(densityGetter, 0, 256)
                ),
                SurfaceRuleData.nether(),
                List.of(),
                32,
                false,
                false,
                false,
                true
        );
    }

    @Override
    public void wover_injectSurfaceRules(Registry<LevelStem> dimensionRegistry, ResourceKey<LevelStem> dimensionKey) {
        SurfaceRuleUtil.injectNoiseBasedSurfaceRules(
                dimensionKey,
                generatorSettings(),
                this.getBiomeSource()
        );
    }
}

