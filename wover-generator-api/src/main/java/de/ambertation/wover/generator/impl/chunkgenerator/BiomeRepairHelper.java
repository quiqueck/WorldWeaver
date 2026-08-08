package de.ambertation.wover.generator.impl.chunkgenerator;

import de.ambertation.wover.biome.impl.modification.BiomeTagModificationWorker;
import de.ambertation.wover.common.generator.api.biomesource.BiomeSourceWithConfig;
import de.ambertation.wover.common.generator.api.biomesource.ReloadableBiomeSource;
import de.ambertation.wover.common.generator.api.chunkgenerator.EnforceableChunkGenerator;
import de.ambertation.wover.entrypoint.LibWoverWorldGenerator;
import de.ambertation.wover.generator.impl.biomesource.end.TheEndBiomesHelper;
import de.ambertation.wover.tag.api.predefined.CommonBiomeTags;

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

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Puts the generator a world was <i>created</i> with back in charge of its dimensions, and pulls
 * every Biome anyone else placed there into our Biome tags so it still shows up.
 * <p>
 * Two situations this exists for:
 * <ol>
 *     <li><b>Datapacks that ship their own dimension.</b> Incendium and Nullscape (datapacks wrapped
 *     in a jar) each ship a full {@code data/minecraft/dimension/the_nether.json} /
 *     {@code the_end.json} with their own {@code minecraft:noise} generator over a
 *     {@code minecraft:multi_noise} biome source.</li>
 *     <li><b>Mods that swap the biome source at runtime.</b> TerraBlender (and therefore
 *     BiomesOPlenty) replaces the BiomeSource on an already-built ChunkGenerator; see
 *     {@code WoverChunkGenerator#restoreInitialBiomeSource}.</li>
 * </ol>
 * In both cases we take the dimension back, but keep their content. Measured on a dedicated server
 * with BiomesOPlenty + TerraBlender + Incendium + Nullscape + Terralith installed alongside
 * BetterNether:
 * <pre>
 * minecraft:the_nether  WoVer - Chunk Generator, noise = minecraft:nether, height = 192
 *                       WoVer - Nether BiomeSource: 41 biomes
 *                       betternether(23), biomesoplenty(5), incendium(8), minecraft(5)
 *                       features: minecraft(41), betternether(114), incendium(36), biomesoplenty(28)
 * minecraft:the_end     WoVer - Chunk Generator, noise = minecraft:end, height = 288
 *                       WoVer - The End BiomeSource: 12 biomes
 *                       minecraft(5), biomesoplenty(4), nullscape(3)
 *                       features: minecraft(4), biomesoplenty(18), nullscape(17)
 * </pre>
 * What survives is everything those packs reference <i>by key</i> rather than embed: their Biomes,
 * their features, and their terrain - the heights above are not vanilla (128 for both), they are
 * Incendium's and Nullscape's own {@code noise_settings} overrides of {@code minecraft:nether} /
 * {@code minecraft:end}, which our ChunkGenerator resolves by the same key. What we drop is only
 * the <i>placement</i> embedded in their dimension JSON - the {@code multi_noise} climate layout -
 * which our tag-driven picker replaces. The overworld is left alone (TerraBlender keeps it).
 * <p>
 * <b>What dropping their placement costs in the End, measured.</b> Vanilla's
 * {@code minecraft:end} routes {@code noise_router.erosion} to {@code cache_2d(minecraft:end_islands)},
 * so the End's <i>erosion</i> parameter is the island height field, and both {@code TheEndBiomeSource}
 * and {@link de.ambertation.wover.generator.impl.biomesource.end.WoverEndBiomeSource
 * WoverEndBiomeSource} cut it at 0.25 / -0.0625 / -0.21875 to get the highlands / midlands / barrens /
 * small-island rings. A pack that replaces {@code minecraft:end} does not have to keep that: Nullscape
 * aliases {@code erosion} to {@code minecraft:overworld/erosion} and takes its terrain from
 * {@code nullscape:base/continents} plus {@code nullscape:island/island}. That looks like it should
 * leave our rings uncorrelated with the terrain, but it does not - Nullscape's own terrain functions
 * ({@code nullscape:base/depth}, {@code base/factor}, {@code base/sloped_cheese}, {@code nullscape:depth},
 * {@code nullscape:sloped_cheese}) all reference {@code minecraft:overworld/erosion} themselves, so the
 * field we read really is an input to the terrain that generates.
 * <p>
 * Measured on 1024 force-generated End chunks per run (chunks 250,250-281,281), Nullscape 1.2.20 +
 * BetterEnd, counting chunks whose ring Biome is a <i>land</i> Biome yet hold zero non-air blocks in
 * the whole 16x16x288 column (land Biomes decorate existing terrain; small-island Biomes grow their
 * own, so only the land ring is a clean measure):
 * <pre>
 *                                          seed 1234567890      seed -8899221100
 * Nullscape's own End (its multi_noise)     750 land, 22.9%      766 land,  2.5%
 * ours (reads the router's erosion)         447 land, 16.8%      103 land, 35.0%
 * ours + self-computed end_islands          325 land, 41.2%      (not run)
 * </pre>
 * Nullscape's End is void-heavy by design - 17-33% of all chunks in that region are completely empty,
 * and its own {@code minecraft:small_end_islands} sits over an empty chunk only ~59% of the time - so
 * "a land Biome over open void" is close to the base rate rather than a symptom. Our rings track the
 * terrain about as well as Nullscape's own placement does. Computing the vanilla island field
 * ourselves instead of reading the router was tried and is measurably <i>worse</i> (41.2%, and the
 * median non-air count under a land Biome drops from 3037 to 334), because it reproduces vanilla's
 * island layout rather than the pack's. What is left is a calibration gap, not a semantic one: the
 * three thresholds are tuned to {@code end_islands}' value distribution, so a differently distributed
 * field skews the ring <i>proportions</i> (seed -8899221100 above: 103 land chunks where Nullscape
 * itself has 766). Closing that would mean either re-deriving the cuts from the actual field's
 * quantiles or keeping the replaced {@code multi_noise} source as a classification oracle; neither is
 * implemented, and both would change generation for existing worlds.
 * <p>
 * The Biomes reach our pickers through {@link BiomeTags#IS_NETHER} and the
 * {@code wover:is_end/*} tags, filled by the two methods below:
 * {@link #registerAllBiomesFromFabric} reads Fabric's live tables (so anything registered through
 * {@code NetherBiomes}/{@code TheEndBiomes} is covered even if it ships no tags), and
 * {@link #registerAllBiomesFromVanillaDimension} reads the vanilla {@code minecraft:normal}
 * preset's own BiomeSource for that dimension - which is what actually brings in BiomesOPlenty's
 * four End Biomes ({@code Added 4 biomes to wover:is_end/highland}). Neither is redundant.
 */
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
        final Registry<Biome> biomes = registryAccess.lookupOrThrow(Registries.BIOME);

        // we ensure that all biomes registered using fabric have the proper biome tags
        registerAllBiomesFromFabric(biomes);
        var originalSet = dimensionRegistry.entrySet();
        for (Map.Entry<ResourceKey<LevelStem>, LevelStem> entry : originalSet) {
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
        // Registry#entrySet() iterates MappedRegistry.byKey, a HashMap<ResourceKey, ...>; ResourceKey hashes
        // by JVM identity, so this walk is in a different order on every boot and the Biomes would be
        // appended to each tag's content list in a different order. Sort by Identifier (value-based hash and
        // compareTo) so the resulting tag contents are reproducible.
        final List<Map.Entry<ResourceKey<Biome>, Biome>> sortedBiomes = biomes
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(e -> e.getKey().identifier().toString()))
                .toList();
        for (Map.Entry<ResourceKey<Biome>, Biome> e : sortedBiomes) {
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
                final Holder.Reference<Biome> holder = biomes.getOrThrow(e.getKey());
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
