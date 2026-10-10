package de.ambertation.wover.testmod.gametest;

import de.ambertation.wover.biome.api.data.BiomeCodecRegistry;
import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.biome.api.data.BiomeDataRegistry;
import de.ambertation.wover.biome.impl.data.BiomeCodecRegistryImpl;
import de.ambertation.wover.common.generator.api.biomesource.NoiseGeneratorSettingsProvider;
import de.ambertation.wover.generator.api.biomesource.WoverBiomeData;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.material.MaterialRuleContext;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
 * <p>
 * Three further tests were added for the 26.3 port, all covering changes that fail <em>silently</em>
 * rather than loudly and so are not caught by "it compiles and boots":
 * {@link #woverBiomeDataDispatchesToItsOwnSubtypeCodec} (the {@code KeyDispatchDataCodec} removal, where a
 * wrong-but-assignable codec would drop every placement field),
 * {@link #amplifiedNetherNoiseSettingsSurvivedThePort} (the {@code NoiseRouter} 15-to-8 shrink and
 * reorder, where a wrong argument order compiles and merely generates a subtly wrong Nether), and
 * {@link #noiseGeneratorSettingsShadowIsWired} (a field {@code @Shadow} that can apply and still hand out
 * {@code null}, which no boot in this repo exercises).
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

    // ---------------------------------------------------------------------------------------------------
    // WoverBiomeData dispatch codec (26.3: KeyDispatchDataCodec removed)
    // ---------------------------------------------------------------------------------------------------

    /**
     * The {@link WoverBiomeData} entries the committed datapack JSON declares, with the subtype-only
     * fields each one is expected to carry. Only {@link WoverBiomeData}'s own codec can produce these
     * values - the base {@link BiomeData} codec knows nothing about {@code parent}, {@code edgeSize} or
     * {@code genChance}.
     */
    private record ExpectedWoverData(
            String biomeDataId,
            float genChance,
            int edgeSize,
            String parentId
    ) {}

    private static final List<ExpectedWoverData> EXPECTED_WOVER_BIOME_DATA = List.of(
            // testmod data (src/testmod/generated/.../wover/worldgen/biome_data/)
            new ExpectedWoverData("wover-generator-testmod:nether_test_biome", 2.0f, 0, null),
            new ExpectedWoverData(
                    "wover-generator-testmod:nether_sub_biome", 1.0f, 0,
                    "wover-generator-testmod:nether_main_biome"
            ),
            new ExpectedWoverData("wover-generator-testmod:wastes_sub_biome", 1.0f, 0, "minecraft:nether_wastes"),
            // main-module data shipped in the jar (src/main/generated/.../wover/worldgen/biome_data/)
            new ExpectedWoverData("minecraft:end_highlands", 1.0f, 4, null),
            new ExpectedWoverData("minecraft:the_end", 1.0f, 0, null)
    );

    /**
     * 26.3 deleted {@code KeyDispatchDataCodec}, so {@code BiomeData#codec()} returns a bare
     * {@link MapCodec} and {@code WoverBiomeData.KEY_CODEC} is gone; {@code WoverBiomeDataImpl} now
     * registers {@link WoverBiomeData#CODEC} under {@code wover:wover_data} directly.
     * <p>
     * That whole path fails <em>silently and destructively</em> if it regresses, which is why it needs a
     * test rather than just a compile: {@link BiomeCodecRegistry} dispatches on the {@code "type"} field,
     * so if {@code wover:wover_data} were registered with the wrong codec (say the inherited
     * {@link BiomeData#CODEC}, which is a perfectly assignable {@code MapCodec<? extends BiomeData>} and
     * therefore compiles), every entry would still load - just as a plain {@link BiomeData} with
     * {@code parent}, {@code edge}, {@code edgeSize}, {@code terrainHeight} and {@code genChance} thrown
     * away. Those fields are exactly what WoVer's BiomeSources use to build the sub-biome/edge placement
     * graph, so the world would generate with no sub-biomes and no edges, and nothing would be logged.
     * <p>
     * The test therefore checks the concrete runtime type <em>and</em> the decoded subtype field values of
     * every committed entry, plus a re-encode through the real dispatch codec to prove the discriminator
     * round-trips.
     */
    @GameTest
    public void woverBiomeDataDispatchesToItsOwnSubtypeCodec(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();

        // --- the codec must be registered, and registered as WoverBiomeData's own ---
        final Identifier woverDataType = Identifier.parse("wover:wover_data");
        final MapCodec<? extends BiomeData> registered = BiomeCodecRegistry.BIOME_CODECS
                .get(woverDataType)
                .map(h -> h.value())
                .orElse(null);

        if (registered == null) {
            final Set<String> present = new TreeSet<>();
            BiomeCodecRegistry.BIOME_CODECS.keySet().forEach(k -> present.add(k.toString()));
            helper.fail(Component.literal(
                    "wover:wover_data is not registered in the BiomeData codec registry - every "
                            + "WoverBiomeData datapack entry would fail to load. Present: " + present
            ));
            return;
        }
        if (registered != WoverBiomeData.CODEC) {
            failures.add("wover:wover_data resolves to a codec that is not WoverBiomeData.CODEC - subtype "
                    + "fields (parent/edge/edgeSize/genChance/terrainHeight) would be silently dropped");
        }
        // codec() is the dispatch key: it must agree with what is registered, or encoding throws.
        if (WoverBiomeData.of(PROBE_BIOME_KEY).codec() != WoverBiomeData.CODEC) {
            failures.add("WoverBiomeData#codec() does not return WoverBiomeData.CODEC - the dispatch codec "
                    + "would not be able to encode a WoverBiomeData at all");
        }

        // --- every committed entry must have decoded as a WoverBiomeData with its fields intact ---
        final Registry<BiomeData> biomeData = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(BiomeDataRegistry.BIOME_DATA_REGISTRY);

        for (ExpectedWoverData expected : EXPECTED_WOVER_BIOME_DATA) {
            final BiomeData actual = biomeData
                    .get(Identifier.parse(expected.biomeDataId()))
                    .map(h -> h.value())
                    .orElse(null);

            if (actual == null) {
                failures.add(expected.biomeDataId() + ": expected BiomeData entry is missing entirely");
                continue;
            }
            if (!(actual instanceof WoverBiomeData wover)) {
                failures.add(expected.biomeDataId() + ": decoded as " + actual.getClass().getSimpleName()
                        + " instead of WoverBiomeData - the wover:wover_data dispatch is not reaching "
                        + "WoverBiomeData.CODEC, so all placement fields were dropped");
                continue;
            }
            if (wover.genChance != expected.genChance()) {
                failures.add(expected.biomeDataId() + ": genChance is " + wover.genChance
                        + ", expected " + expected.genChance());
            }
            if (wover.edgeSize != expected.edgeSize()) {
                failures.add(expected.biomeDataId() + ": edgeSize is " + wover.edgeSize
                        + ", expected " + expected.edgeSize());
            }
            final String actualParent = wover.parent == null ? null : wover.parent.identifier().toString();
            if (!java.util.Objects.equals(actualParent, expected.parentId())) {
                failures.add(expected.biomeDataId() + ": parent is " + actualParent
                        + ", expected " + expected.parentId());
            }
        }

        // --- the dispatch codec must round-trip, i.e. the "type" discriminator works both ways ---
        final RegistryAccess access = helper.getLevel().registryAccess();
        final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, access);
        final WoverBiomeData probe = WoverBiomeData.withEdge(
                ResourceKey.create(Registries.BIOME, Identifier.parse("wover-generator-testmod:nether_main_biome")),
                ResourceKey.create(Registries.BIOME, Identifier.parse("wover-generator-testmod:nether_sub_biome"))
        );

        final DataResult<JsonElement> encoded = BiomeCodecRegistryImpl.CODEC.encodeStart(ops, probe);
        if (encoded.isError()) {
            failures.add("dispatch codec cannot encode a WoverBiomeData: " + encoded.error().orElseThrow().message());
        } else {
            final JsonElement json = encoded.getOrThrow();
            final JsonObject obj = json.isJsonObject() ? json.getAsJsonObject() : null;
            if (obj == null || !obj.has("type")) {
                failures.add("encoded WoverBiomeData has no \"type\" discriminator: " + json);
            } else if (!"wover:wover_data".equals(obj.get("type").getAsString())) {
                failures.add("encoded WoverBiomeData has type \"" + obj.get("type").getAsString()
                        + "\", expected \"wover:wover_data\"");
            }
            if (obj != null && !obj.has("edge")) {
                failures.add("encoded WoverBiomeData lost its \"edge\" field - the base BiomeData codec was "
                        + "used instead of WoverBiomeData.CODEC: " + json);
            }

            final DataResult<BiomeData> decoded = BiomeCodecRegistryImpl.CODEC
                    .parse(ops, json)
                    .map(d -> (BiomeData) d);
            if (decoded.isError()) {
                failures.add("dispatch codec cannot decode its own output: "
                        + decoded.error().orElseThrow().message());
            } else if (!(decoded.getOrThrow() instanceof WoverBiomeData back)) {
                failures.add("round-tripped WoverBiomeData came back as "
                        + decoded.getOrThrow().getClass().getSimpleName());
            } else if (back.edge == null || !back.edge.equals(probe.edge)) {
                failures.add("round-tripped WoverBiomeData lost its edge: " + back.edge);
            }
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "WoverBiomeData codec dispatch regression:\n - " + String.join("\n - ", failures)
            ));
            return;
        }

        helper.succeed();
    }

    // A biome key only used to probe WoverBiomeData#codec(); never registered anywhere.
    private static final ResourceKey<Biome> PROBE_BIOME_KEY =
            ResourceKey.create(Registries.BIOME, Identifier.parse("wover-generator-testmod:codec_probe"));

    // ---------------------------------------------------------------------------------------------------
    // amplified_nether NoiseGeneratorSettings (26.3: NoiseRouter 15 -> 8, materialRule, aquifers/oreVeins)
    // ---------------------------------------------------------------------------------------------------

    /**
     * The eight {@code noise_router} fields 26.3's {@link net.minecraft.world.level.levelgen.NoiseRouter}
     * still has, and the seven it lost (four to {@code Aquifer.Config}, three to the {@code ore_vein} material rule).
     * The 26.3 release also renamed {@code preliminary_surface_level} to {@code chunk_surface_level}.
     */
    private static final Set<String> EXPECTED_ROUTER_FIELDS = Set.of(
            "temperature", "vegetation", "continents", "erosion",
            "depth", "ridges", "chunk_surface_level", "final_density"
    );
    private static final Set<String> REMOVED_ROUTER_FIELDS = Set.of(
            "barrier", "fluid_level_floodedness", "fluid_level_spread", "lava",
            "vein_toggle", "vein_ridged", "vein_gap", "preliminary_surface_level"
    );
    // The router slots WoverChunkGenerator fills with DensityFunctions.zero().
    private static final Set<String> ZERO_ROUTER_FIELDS = Set.of(
            "continents", "erosion", "depth", "ridges", "chunk_surface_level"
    );

    /**
     * {@code WoverChunkGenerator#amplifiedNether} had to be genuinely ported, not just recompiled: 26.3
     * shrank {@code NoiseRouter} from 15 components to 8 <em>and reordered them</em> (26.1 started with the
     * four aquifer noises, so {@code temperature} sat at index 5; it is now index 1), replaced
     * {@code surfaceRule} with a {@code Holder<RuleSource>} called {@code materialRule}, and replaced the
     * {@code aquifersEnabled}/{@code oreVeinsEnabled} booleans with {@code Optional<Aquifer.Config>} /
     * {@code List<OreVeinifier>} (in the 26.3 release ore veins moved on into the material rules, so the settings
     * no longer carry them at all).
     * <p>
     * Every component of {@code NoiseRouter} has the same type, so getting the new order wrong compiles
     * cleanly and merely generates a subtly wrong Nether - {@code temperature} would silently become
     * {@code continents}. This test pins the identity of each slot by encoding the settings back to JSON
     * and checking which noise ended up where, which is the only way to tell the slots apart.
     * <p>
     * It reads the settings out of the live registry, so it also guards the committed
     * {@code amplified_nether.json} against going stale again: the 26.1 file carried the removed router
     * fields and an inlined {@code surface_rule}, which 26.3's loader would not accept.
     */
    @GameTest
    public void amplifiedNetherNoiseSettingsSurvivedThePort(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        final RegistryAccess access = helper.getLevel().registryAccess();

        final NoiseGeneratorSettings settings = access
                .lookupOrThrow(Registries.NOISE_SETTINGS)
                .get(Identifier.parse(DATAPACK_NOISE_SETTINGS))
                .map(h -> h.value())
                .orElse(null);

        if (settings == null) {
            helper.fail(Component.literal(DATAPACK_NOISE_SETTINGS + " is not in the noise-settings registry"));
            return;
        }

        // --- the components that used to be booleans ---
        if (settings.aquifers().isPresent()) {
            failures.add("aquifers() is present, but the amplified nether generates with aquifers off "
                    + "(26.1: aquifersEnabled = false)");
        }
        // (26.3 release: ore veins are no longer a NoiseGeneratorSettings component but a material rule;
        // the amplified nether references minecraft:nether, which has no ore_vein rule.)

        // --- the component that used to be an inline RuleSource ---
        final Identifier materialRule = settings
                .materialRule()
                .unwrapKey()
                .map(k -> k.identifier())
                .orElse(null);
        if (materialRule == null) {
            failures.add("materialRule is an inline (direct) holder - it should reference the vanilla "
                    + "minecraft:nether material rule the way vanilla's own nether settings do");
        } else if (!"minecraft:nether".equals(materialRule.toString())) {
            failures.add("materialRule references " + materialRule + ", expected minecraft:nether");
        }

        // --- the unchanged scalar settings, so a wholesale rewrite cannot pass unnoticed ---
        if (settings.seaLevel() != 32) {
            failures.add("seaLevel is " + settings.seaLevel() + ", expected 32");
        }
        if (!settings.useLegacyRandomSource()) {
            failures.add("useLegacyRandomSource is false, expected true");
        }
        if (settings.disableMobGeneration()) {
            failures.add("disableMobGeneration is true, expected false");
        }
        if (settings.noiseSettings().minY() != 0 || settings.noiseSettings().height() != 256) {
            failures.add("noiseSettings is minY=" + settings.noiseSettings().minY()
                    + " height=" + settings.noiseSettings().height() + ", expected minY=0 height=256");
        }

        // --- the NoiseRouter slots, identified by re-encoding ---
        final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, access);
        final DataResult<JsonElement> encoded = NoiseGeneratorSettings.DIRECT_CODEC.encodeStart(ops, settings);
        if (encoded.isError()) {
            failures.add("cannot re-encode the settings: " + encoded.error().orElseThrow().message());
        } else {
            final JsonObject router = encoded
                    .getOrThrow()
                    .getAsJsonObject()
                    .getAsJsonObject("noise_router");

            for (String removed : REMOVED_ROUTER_FIELDS) {
                if (router.has(removed)) {
                    failures.add("noise_router still carries the removed field \"" + removed + "\"");
                }
            }
            for (String kept : EXPECTED_ROUTER_FIELDS) {
                if (!router.has(kept)) {
                    failures.add("noise_router is missing \"" + kept + "\"");
                }
            }
            for (String zero : ZERO_ROUTER_FIELDS) {
                if (router.has(zero) && !"0.0".equals(router.get(zero).toString())) {
                    failures.add("noise_router." + zero + " is " + router.get(zero)
                            + ", expected the constant 0.0 - a non-zero value here means a density function "
                            + "landed in the wrong NoiseRouter slot");
                }
            }
            // These two are the slots that actually carry a noise, and they are what an argument-order
            // mistake would move somewhere else.
            assertShiftedNoise(router, "temperature", "minecraft:nether/temperature", failures);
            assertShiftedNoise(router, "vegetation", "minecraft:nether/vegetation", failures);

            if (router.has("final_density")) {
                // Since the 26.3 release the beardifier is part of the router (vanilla's nether router is
                // add(postProcess(slide), beardifier())), so the squeezed noise sits on the left of an add.
                final JsonElement finalDensity = router.get("final_density");
                final boolean ok = finalDensity.isJsonObject()
                        && "minecraft:add".equals(typeOf(finalDensity))
                        && "minecraft:squeeze".equals(typeOf(finalDensity.getAsJsonObject().get("left")))
                        && "minecraft:beardifier".equals(typeOf(finalDensity.getAsJsonObject().get("right")));
                if (!ok) {
                    failures.add("noise_router.final_density is " + finalDensity
                            + ", expected add(minecraft:squeeze post-processed nether noise, minecraft:beardifier)");
                }
            }
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "amplified_nether noise settings regression:\n - " + String.join("\n - ", failures)
                            + "\n\nEncoded settings: " + encoded.result().orElse(null)
            ));
            return;
        }

        helper.succeed();
    }

    // ---------------------------------------------------------------------------------------------------
    // NoiseBasedChunkGeneratorMixin's @Shadow of the (private, final) `settings` field
    // ---------------------------------------------------------------------------------------------------

    /**
     * {@code NoiseBasedChunkGeneratorMixin} reaches {@code NoiseBasedChunkGenerator.settings} - a
     * {@code private final} field - through a {@code @Shadow}, to implement
     * {@code NoiseGeneratorSettingsProvider}. This build has no refmap and no mixin annotation processor, so
     * nothing checks that shadow at compile time, and a field-level mixin whose target was renamed or
     * retyped can still apply and simply hand out {@code null} at runtime (exactly what bit
     * {@code wover-surface-api}'s {@code MaterialRuleContext.biome} accessor in this same port).
     * <p>
     * Booting the server does not catch it either: the only caller,
     * {@code WoverChunkGenerator#enforceGeneratorInWorldGenSettings}, runs only when another mod has
     * replaced the generator, so it never executes in a clean dev run. This test drives the shadow directly
     * and cross-checks it against vanilla's public {@code generatorSettings()}, which must return the very
     * same holder.
     */
    @GameTest
    public void noiseGeneratorSettingsShadowIsWired(GameTestHelper helper) {
        final List<String> failures = new ArrayList<>();
        final MinecraftServer server = helper.getLevel().getServer();

        int checked = 0;
        for (ServerLevel level : server.getAllLevels()) {
            final ChunkGenerator generator = level.getChunkSource().getGenerator();
            if (!(generator instanceof NoiseBasedChunkGenerator noiseGenerator)) continue;
            checked++;

            final String where = level.dimension().identifier() + " (" + generator.getClass().getSimpleName() + ")";

            if (!(generator instanceof NoiseGeneratorSettingsProvider provider)) {
                failures.add(where + ": a NoiseBasedChunkGenerator does not implement "
                        + "NoiseGeneratorSettingsProvider - NoiseBasedChunkGeneratorMixin did not apply");
                continue;
            }

            final Holder<NoiseGeneratorSettings> viaShadow = provider.wover_getNoiseGeneratorSettingHolders();
            if (viaShadow == null) {
                failures.add(where + ": wover_getNoiseGeneratorSettingHolders() returned null - the @Shadow "
                        + "of NoiseBasedChunkGenerator.settings applied but is not bound to the real field");
                continue;
            }
            if (viaShadow != noiseGenerator.generatorSettings()) {
                failures.add(where + ": the @Shadow field and vanilla's generatorSettings() disagree ("
                        + viaShadow + " vs " + noiseGenerator.generatorSettings() + ")");
            }
            if (provider.wover_getNoiseGeneratorSettings() == null) {
                failures.add(where + ": wover_getNoiseGeneratorSettings() returned null");
            }
        }

        if (checked == 0) {
            helper.fail(Component.literal(
                    "No NoiseBasedChunkGenerator among the loaded dimensions, so the @Shadow was never "
                            + "exercised - this test would pass vacuously and must be re-scoped"
            ));
            return;
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal(
                    "NoiseBasedChunkGeneratorMixin regression (" + checked + " generators checked):\n - "
                            + String.join("\n - ", failures)
            ));
            return;
        }

        helper.succeed();
    }

    private static void assertShiftedNoise(
            JsonObject router,
            String field,
            String expectedNoise,
            List<String> failures
    ) {
        if (!router.has(field)) return; // already reported as missing
        final JsonElement value = router.get(field);
        if (!value.isJsonObject()) {
            failures.add("noise_router." + field + " is " + value + ", expected a minecraft:shifted_noise "
                    + "using " + expectedNoise + " - a plain number here means the NoiseRouter arguments "
                    + "are in the wrong order");
            return;
        }
        final JsonObject obj = value.getAsJsonObject();
        if (!obj.has("noise") || !expectedNoise.equals(obj.get("noise").getAsString())) {
            failures.add("noise_router." + field + " uses noise "
                    + (obj.has("noise") ? obj.get("noise") : "<none>")
                    + ", expected " + expectedNoise + " - the NoiseRouter arguments are swapped");
        }
    }

    private static String typeOf(JsonElement element) {
        if (element == null || !element.isJsonObject()) return null;
        final JsonElement type = element.getAsJsonObject().get("type");
        return type == null || !type.isJsonPrimitive() ? null : type.getAsString();
    }
}
