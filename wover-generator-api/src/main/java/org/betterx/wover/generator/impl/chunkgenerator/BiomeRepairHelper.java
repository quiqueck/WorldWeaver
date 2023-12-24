package org.betterx.wover.generator.impl.chunkgenerator;

import org.betterx.wover.biome.impl.modification.BiomeTagModificationWorker;
import org.betterx.wover.common.generator.api.biomesource.BiomeSourceWithConfig;
import org.betterx.wover.common.generator.api.biomesource.ReloadableBiomeSource;
import org.betterx.wover.common.generator.api.chunkgenerator.EnforceableChunkGenerator;
import org.betterx.wover.entrypoint.LibWoverWorldGenerator;
import org.betterx.wover.generator.impl.biomesource.end.TheEndBiomesHelper;
import org.betterx.wover.tag.api.predefined.CommonBiomeTags;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;

import net.fabricmc.fabric.api.biome.v1.NetherBiomes;

import com.google.common.base.Stopwatch;

import java.util.Map;

class BiomeRepairHelper {
    private Map<ResourceKey<LevelStem>, ChunkGenerator> vanillaDimensions = null;

    public static TagKey<Biome> getBiomeTagForDimension(ResourceKey<LevelStem> key) {
        if (key.equals(LevelStem.END)) return CommonBiomeTags.IS_END_HIGHLAND;
        else if (key.equals(LevelStem.NETHER)) return BiomeTags.IS_NETHER;
        else if (key.equals(LevelStem.OVERWORLD)) return BiomeTags.IS_OVERWORLD;
        return null;
    }

    public Registry<LevelStem> repairBiomeSourceInAllDimensions(
            RegistryAccess registryAccess,
            Registry<LevelStem> dimensionRegistry
    ) {
        Map<ResourceKey<LevelStem>, ChunkGenerator> configuredDimensions = WorldGeneratorConfigImpl.loadWorldDimensions(
                registryAccess,
                WorldGeneratorConfigImpl.getPresetsNbt()
        );
        final Registry<Biome> biomes = registryAccess.registryOrThrow(Registries.BIOME);

        // we ensure that all biomes registered using fabric have the proper biome tags
        registerAllBiomesFromFabric(biomes);
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : dimensionRegistry.entrySet()) {
            boolean didRepair = false;
            ResourceKey<LevelStem> key = entry.getKey();
            LevelStem loadedStem = entry.getValue();

            final ChunkGenerator referenceGenerator = configuredDimensions.get(key);

            if (referenceGenerator instanceof EnforceableChunkGenerator<?> enforcer) {
                final ChunkGenerator loadedChunkGenerator = loadedStem.generator();

                // if the loaded ChunkGenerator is not the one we expect from vanilla, we will load the vanilla
                // ones and mark all modded biomes with the respective dimension
                registerAllBiomesFromVanillaDimension(registryAccess, biomes, key);

                // now compare the reference world settings (the ones that were created when the world was
                // started) with the settings that were loaded by the game.
                // If those do not match, we will create a new ChunkGenerator / BiomeSources with appropriate
                // settings
                if (enforcer.togetherShouldRepair(loadedChunkGenerator)) {
                    dimensionRegistry = enforcer.enforceGeneratorInWorldGenSettings(
                            registryAccess,
                            key,
                            loadedStem.type().unwrapKey().orElseThrow(),
                            loadedChunkGenerator,
                            dimensionRegistry
                    );
                    didRepair = true;
                } else if (loadedChunkGenerator.getBiomeSource() instanceof BiomeSourceWithConfig lodedSource) {
                    if (referenceGenerator.getBiomeSource() instanceof BiomeSourceWithConfig refSource) {
                        if (!refSource.getBiomeSourceConfig().sameConfig(lodedSource.getBiomeSourceConfig())) {
                            lodedSource.setBiomeSourceConfig(refSource.getBiomeSourceConfig());
                        }
                    }
                }
            }

            if (!didRepair) {
                if (loadedStem.generator().getBiomeSource() instanceof ReloadableBiomeSource reload) {
                    reload.reloadBiomes();
                }
            }
        }

        // we ensure that all dimensions get the correct reference to the originally configured WorldPreset
        copyWorldPresetReference(dimensionRegistry, configuredDimensions);

        return dimensionRegistry;
    }

    private static void copyWorldPresetReference(
            Registry<LevelStem> dimensionRegistry,
            Map<ResourceKey<LevelStem>, ChunkGenerator> configuredDimensions
    ) {
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> loadedDimension : dimensionRegistry.entrySet()) {
            final ChunkGenerator referenceGenerator = configuredDimensions.get(loadedDimension.getKey());

            if (referenceGenerator instanceof ConfiguredChunkGenerator refCfg
                    && loadedDimension.getValue().generator() instanceof ConfiguredChunkGenerator loadedCfg) {
                if (loadedCfg.wover_getConfiguredWorldPreset() == null) {
                    loadedCfg.wover_setConfiguredWorldPreset(refCfg.wover_getConfiguredWorldPreset());
                }

            }
        }
    }

    private void registerAllBiomesFromFabric(
            Registry<Biome> biomes
    ) {
        final Stopwatch sw = Stopwatch.createStarted();
        int biomesAdded = 0;

        final BiomeTagModificationWorker biomeTagWorker = new BiomeTagModificationWorker();
        for (Map.Entry<ResourceKey<Biome>, Biome> e : biomes.entrySet()) {
            TagKey<Biome> tag = null;
            if (NetherBiomes.canGenerateInNether(e.getKey())) {
                tag = BiomeTags.IS_NETHER;
            } else if (TheEndBiomesHelper.canGenerateAsMainIslandBiome(e.getKey())) {
                tag = CommonBiomeTags.IS_END_CENTER;
            } else if (TheEndBiomesHelper.canGenerateAsHighlandsBiome(e.getKey())) {
                tag = CommonBiomeTags.IS_END_HIGHLAND;
            } else if (TheEndBiomesHelper.canGenerateAsEndBarrens(e.getKey())) {
                tag = CommonBiomeTags.IS_END_BARRENS;
            } else if (TheEndBiomesHelper.canGenerateAsSmallIslandsBiome(e.getKey())) {
                tag = CommonBiomeTags.IS_SMALL_END_ISLAND;
            } else if (TheEndBiomesHelper.canGenerateAsEndMidlands(e.getKey())) {
                tag = CommonBiomeTags.IS_END_MIDLAND;
            }

            if (tag != null) {
                final Holder.Reference<Biome> holder = biomes.getHolderOrThrow(e.getKey());
                if (!holder.is(tag)) {
                    biomeTagWorker.addBiomeToTag(tag, biomes, e.getKey(), holder);
                    biomesAdded++;
                }
            }
        }

        biomeTagWorker.finished();

        if (biomesAdded > 0) {
            LibWoverWorldGenerator.C.log.info("Added Tags for {} fabric biomes in {}", biomesAdded, sw);
        }

    }

    private void registerAllBiomesFromVanillaDimension(
            RegistryAccess access,
            Registry<Biome> biomes,
            ResourceKey<LevelStem> key
    ) {
        final Stopwatch sw = Stopwatch.createStarted();
        int biomesAdded = 0;

        final BiomeTagModificationWorker biomeTagWorker = new BiomeTagModificationWorker();
        final TagKey<Biome> tag = getBiomeTagForDimension(key);

        if (tag != null) {
            if (vanillaDimensions == null) {
                vanillaDimensions = DimensionsWrapper.getDimensionsMap(
                        access,
                        net.minecraft.world.level.levelgen.presets.WorldPresets.NORMAL
                );
            }

            final ChunkGenerator vanillaDim = vanillaDimensions.getOrDefault(key, null);
            if (vanillaDim != null && vanillaDim.getBiomeSource() != null) {
                for (Holder<Biome> biomeHolder : vanillaDim.getBiomeSource().possibleBiomes()) {
                    if (biomeHolder.unwrapKey().isPresent() && !biomeHolder.is(tag)) {
                        biomeTagWorker.addBiomeToTag(tag, biomes, biomeHolder.unwrapKey().orElseThrow(), biomeHolder);
                        biomesAdded++;
                    }
                }
            }

            biomeTagWorker.finished();

            if (biomesAdded > 0) {
                LibWoverWorldGenerator.C.log.info("Added {} biomes to {} in {}", biomesAdded, tag.location(), sw);
            }
        }
    }
}
