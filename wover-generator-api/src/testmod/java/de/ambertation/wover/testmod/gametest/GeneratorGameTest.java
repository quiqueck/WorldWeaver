package de.ambertation.wover.testmod.gametest;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Fabric GameTest for wover-generator-api: the chunk-generator / dimension / world-preset wiring the
 * module contributes must all be reachable in the live registries once the server is up.
 * <p>
 * The module contributes its world-generation content across two independent registration paths, and
 * this test guards both:
 * <ul>
 *   <li><b>Datapack-loaded dynamic registries</b> — {@code wover-generator-api} ships committed datagen
 *       JSON under {@code src/main/generated} (wired in as a {@code main} resource root, so it lands in
 *       the jar as a datapack). Those files populate the dynamic registries via {@code registryAccess()}:
 *       the three {@link WorldPreset}s {@code wover:normal / large / amplified} in
 *       {@link Registries#WORLD_PRESET}, and the custom {@code wover:amplified_nether}
 *       {@link NoiseGeneratorSettings} in {@link Registries#NOISE_SETTINGS}. Their presence proves the
 *       preset/noise datapack JSON is still loaded and processed.</li>
 *   <li><b>Programmatically-registered codec registries</b> — at mod init {@code LibWoverWorldGenerator}
 *       calls {@code ChunkGeneratorManagerImpl.initialize()} / {@code BiomeSourceManagerImpl.initialize()},
 *       which insert the module's {@link ChunkGenerator} codec {@code wover:betterx} into
 *       {@link BuiltInRegistries#CHUNK_GENERATOR} and the two {@link BiomeSource} codecs
 *       {@code wover:nether_biome_source} / {@code wover:end_biome_source} into
 *       {@link BuiltInRegistries#BIOME_SOURCE}. These are the low-level building blocks every WoVer
 *       dimension is assembled from, so their presence proves the generator/biome-source wiring still
 *       fires.</li>
 * </ul>
 * A missing entry in either path is a real regression. The negative assertion guards against the test
 * silently passing against an always-true registry.
 */
public class GeneratorGameTest {
    // World presets loaded from the committed datagen JSON shipped as a main resource
    // (src/main/generated/data/wover/worldgen/world_preset/*.json).
    private static final List<String> DATAPACK_WORLD_PRESETS = List.of(
            "wover:normal",
            "wover:large",
            "wover:amplified"
    );

    // Custom noise settings loaded from the committed datagen JSON shipped as a main resource
    // (src/main/generated/data/wover/worldgen/noise_settings/amplified_nether.json).
    private static final String DATAPACK_NOISE_SETTINGS = "wover:amplified_nether";

    // Chunk-generator codec registered programmatically at mod init (WoverChunkGenerator.ID).
    private static final String PROGRAMMATIC_CHUNK_GENERATOR = "wover:betterx";

    // Biome-source codecs registered programmatically at mod init (BiomeSourceManagerImpl.initialize()).
    private static final List<String> PROGRAMMATIC_BIOME_SOURCES = List.of(
            "wover:nether_biome_source",
            "wover:end_biome_source"
    );

    // A plausible-but-absent preset id the module never registers. Its presence would mean the
    // containsKey checks are meaningless, so this makes the test able to fail on a regression.
    private static final String ABSENT_WORLD_PRESET = "wover:does_not_exist_preset";

    @GameTest
    public void generatorContentIsLoadedAndInjected(GameTestHelper helper) {
        final Registry<WorldPreset> worldPresets = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.WORLD_PRESET);
        final Registry<NoiseGeneratorSettings> noiseSettings = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.NOISE_SETTINGS);
        final Registry<MapCodec<? extends ChunkGenerator>> chunkGenerators = BuiltInRegistries.CHUNK_GENERATOR;
        final Registry<MapCodec<? extends BiomeSource>> biomeSources = BuiltInRegistries.BIOME_SOURCE;

        final List<String> failures = new ArrayList<>();

        // --- datapack-loaded facet ---
        for (String id : DATAPACK_WORLD_PRESETS) {
            if (!worldPresets.containsKey(Identifier.parse(id))) {
                failures.add(id + ": expected world preset loaded from datapack JSON but it is missing");
            }
        }
        if (!noiseSettings.containsKey(Identifier.parse(DATAPACK_NOISE_SETTINGS))) {
            failures.add(DATAPACK_NOISE_SETTINGS + ": expected noise settings loaded from datapack JSON but it is missing");
        }

        // --- programmatic (codec-registry) facet ---
        if (!chunkGenerators.containsKey(Identifier.parse(PROGRAMMATIC_CHUNK_GENERATOR))) {
            failures.add(PROGRAMMATIC_CHUNK_GENERATOR + ": expected chunk-generator codec registered at mod init but it is missing");
        }
        for (String id : PROGRAMMATIC_BIOME_SOURCES) {
            if (!biomeSources.containsKey(Identifier.parse(id))) {
                failures.add(id + ": expected biome-source codec registered at mod init but it is missing");
            }
        }

        // Negative guard: an id the module never registers must not be present.
        if (worldPresets.containsKey(Identifier.parse(ABSENT_WORLD_PRESET))) {
            failures.add(ABSENT_WORLD_PRESET + ": unexpectedly present - the containsKey assertions are not meaningful");
        }

        if (!failures.isEmpty()) {
            final List<String> presentPresets = new ArrayList<>();
            worldPresets.keySet().forEach(k -> presentPresets.add(k.toString()));
            final List<String> presentGenerators = new ArrayList<>();
            chunkGenerators.keySet().forEach(k -> presentGenerators.add(k.toString()));
            final List<String> presentSources = new ArrayList<>();
            biomeSources.keySet().forEach(k -> presentSources.add(k.toString()));
            helper.fail(Component.literal(
                    "Generator registry regression:\n - " + String.join("\n - ", failures)
                            + "\n\nWorld presets actually present: " + presentPresets
                            + "\n\nChunk generators actually present: " + presentGenerators
                            + "\n\nBiome sources actually present: " + presentSources
            ));
            return;
        }

        helper.succeed();
    }
}
