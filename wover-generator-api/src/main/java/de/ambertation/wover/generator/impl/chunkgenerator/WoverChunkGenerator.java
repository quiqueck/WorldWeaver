package de.ambertation.wover.generator.impl.chunkgenerator;

import de.ambertation.wover.biome.impl.modification.ChunkGeneratorHelper;
import de.ambertation.wover.biome.mixin.ChunkGeneratorAccessor;
import de.ambertation.wover.common.generator.api.biomesource.BiomeSourceWithNoiseRelatedSettings;
import de.ambertation.wover.common.generator.api.biomesource.MergeableBiomeSource;
import de.ambertation.wover.common.generator.api.biomesource.NoiseGeneratorSettingsProvider;
import de.ambertation.wover.common.generator.api.biomesource.ReloadableBiomeSource;
import de.ambertation.wover.common.generator.api.chunkgenerator.EnforceableChunkGenerator;
import de.ambertation.wover.common.generator.api.chunkgenerator.RebuildableFeaturesPerStep;
import de.ambertation.wover.common.generator.api.chunkgenerator.RestorableBiomeSource;
import de.ambertation.wover.common.surface.api.InjectableSurfaceRules;
import de.ambertation.wover.core.api.IntegrationCore;
import de.ambertation.wover.entrypoint.LibWoverWorldGenerator;
import de.ambertation.wover.surface.impl.SurfaceRuleUtil;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.*;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public class WoverChunkGenerator extends NoiseBasedChunkGenerator implements
        RestorableBiomeSource<WoverChunkGenerator>,
        InjectableSurfaceRules<WoverChunkGenerator>,
        EnforceableChunkGenerator<WoverChunkGenerator>,
        RebuildableFeaturesPerStep<WoverChunkGenerator> {
    public static final Identifier ID = LibWoverWorldGenerator.C.id("betterx");

    protected static final NoiseSettings NETHER_NOISE_SETTINGS_AMPLIFIED = NoiseSettings.create(0, 256, 1, 4);
    public static final ResourceKey<NoiseGeneratorSettings> AMPLIFIED_NETHER = ResourceKey.create(
            Registries.NOISE_SETTINGS,
            LibWoverWorldGenerator.C.id("amplified_nether")
    );

    public static final MapCodec<WoverChunkGenerator> CODEC = RecordCodecBuilder
            .mapCodec((RecordCodecBuilder.Instance<WoverChunkGenerator> builderInstance) -> {

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
            LibWoverWorldGenerator.C.log.info("Make sure features are loaded from terrablender:"
                    + biomeSource.getClass().getName());
            //terrablender is invalidating the feature initialization
            //we redo it at this point, otherwise we will get blank biomes
            ChunkGeneratorHelper.rebuildFeaturesPerStep(this, biomeSource);
        }

        LibWoverWorldGenerator.C.log.info("Created WoverChunkGenerator with " + biomeSource.getClass().getName());
    }

    @Override
    protected @NotNull MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    public void wover_rebuildFeaturesPerStep() {
        ChunkGeneratorHelper.rebuildFeaturesPerStep(this, getBiomeSource());
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

                ChunkGeneratorHelper.rebuildFeaturesPerStep(this, getBiomeSource());
            }
        }
    }

    @Override
    public String toString() {
        return ChunkGeneratorManagerImpl.printGeneratorInfo("WoVer - Chunk Generator", this);
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
        LibWoverWorldGenerator.C.log.info("Enforcing Correct Generator for " + dimensionKey
                .identifier()
                .toString() + ".");

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
                dimensionRegistry.entrySet(),
                referenceGenerator,
                key -> dimensionRegistry.get(key).map(Holder.Reference::value).orElse(null),
                (registry, key, stem) -> registry.register(
                        key, stem,
                        dimensionRegistry.registrationInfo(key).orElse(RegistrationInfo.BUILT_IN)
                )
        );
    }


    public static NoiseGeneratorSettings amplifiedNether(BootstrapContext<NoiseGeneratorSettings> bootstapContext) {
        HolderGetter<DensityFunction> densityGetter = bootstapContext.lookup(Registries.DENSITY_FUNCTION);
        return new NoiseGeneratorSettings(
                NETHER_NOISE_SETTINGS_AMPLIFIED,
                Blocks.NETHERRACK.defaultBlockState(),
                Blocks.LAVA.defaultBlockState(),
                netherNoNewCaves(
                        densityGetter,
                        bootstapContext.lookup(Registries.NOISE),
                        slideNetherLike(densityGetter, 0, 256)
                ),
                // 26.2: SurfaceRules.isBiome() resolves its biome keys into a HolderSet right away instead
                // of storing the raw keys, so every rule factory that filters by biome needs a biome
                // lookup - SurfaceRuleData.nether() included. The rule itself is unchanged.
                SurfaceRuleData.nether(bootstapContext.lookup(Registries.BIOME)),
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

    // 26.1 removed NoiseRouterData.noNewCaves / made slideNetherLike private. Reconstructed here to
    // preserve the exact amplified_nether noise_router that was generated before (height 256 nether slide).
    private static final ResourceKey<DensityFunction> BASE_3D_NOISE_NETHER = ResourceKey.create(
            Registries.DENSITY_FUNCTION,
            Identifier.withDefaultNamespace("nether/base_3d_noise")
    );

    private static DensityFunction slideNetherLike(HolderGetter<DensityFunction> functions, int minY, int height) {
        DensityFunction caves = new DensityFunctions.HolderHolder(functions.getOrThrow(BASE_3D_NOISE_NETHER));
        // mirrors NoiseRouterData.slide(caves, minY, height, 24, 0, 0.9375, -8, 24, 2.5)
        DensityFunction topFactor = DensityFunctions.yClampedGradient(minY + height - 24, minY + height, 1.0, 0.0);
        DensityFunction noiseValue = DensityFunctions.lerp(topFactor, 0.9375, caves);
        DensityFunction bottomFactor = DensityFunctions.yClampedGradient(minY - 8, minY + 24, 0.0, 1.0);
        return DensityFunctions.lerp(bottomFactor, 2.5, noiseValue);
    }

    private static NoiseRouter netherNoNewCaves(
            HolderGetter<DensityFunction> functions,
            HolderGetter<net.minecraft.world.level.levelgen.synth.NormalNoise.NoiseParameters> noises,
            DensityFunction slide
    ) {
        DensityFunction temperature = DensityFunctions.shiftedNoise2d(
                DensityFunctions.zero(), DensityFunctions.zero(), 0.25, noises.getOrThrow(Noises.TEMPERATURE_NETHER)
        );
        DensityFunction vegetation = DensityFunctions.shiftedNoise2d(
                DensityFunctions.zero(), DensityFunctions.zero(), 0.25, noises.getOrThrow(Noises.VEGETATION_NETHER)
        );
        // mirrors NoiseRouterData.postProcess(slide)
        DensityFunction fullNoise = DensityFunctions.mul(
                DensityFunctions.interpolated(DensityFunctions.blendDensity(slide)),
                DensityFunctions.constant(0.64)
        ).squeeze();
        return new NoiseRouter(
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                temperature,
                vegetation,
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                fullNoise,
                DensityFunctions.zero(),
                DensityFunctions.zero(),
                DensityFunctions.zero()
        );
    }
}

