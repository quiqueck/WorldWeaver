package de.ambertation.wover.test.api.gametest;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/**
 * The cross-mod compatibility framework's "does the other mod's worldgen actually reach the shared
 * BiomeSource, in the exact dimension(s) it's supposed to" check.
 * <p>
 * Deliberately <b>not</b> a {@code @GameTest} (see the superseded {@code CompatWorldgenGameTest}, kept
 * only in git history): vanilla's own {@code GameTestServer} hardcodes
 * {@code WorldPresets.FLAT.createWorldDimensions()} for every dimension it creates - confirmed by
 * decompiling {@code GameTestServer}'s bytecode, not inferred - so the overworld a GameTest run sees is
 * unconditionally the flat/void Superflat preset, never the mod's real registered generator. Noise-based
 * injections (TerraBlender regions, Terralith's own biome source, etc) can never be observed there
 * regardless of whether they actually work. Nether/end aren't affected (the flat preset only overrides
 * the overworld dimension), which is why the old GameTest-based check could see BiomesOPlenty in
 * nether/end but never in overworld - a real environment quirk, not a mod bug.
 * <p>
 * This class instead boots a <i>plain</i> dedicated server (the project's own {@code testmodServer} run,
 * not a GameTest run) with the real, normally-registered generator for every dimension, waits for
 * {@link ServerLifecycleEvents#SERVER_STARTED} (registries are fully loaded and every dimension's
 * {@link BiomeSource} is live well before any chunk actually needs to generate - no world save or actual
 * terrain generation is required for this check), inspects every level's BiomeSource the same way the
 * old GameTest check did, writes the result to a file, and immediately halts the server. The check runs
 * inside a try/finally around the halt call specifically so a bug in the check itself can never leave the
 * server running forever waiting for a client that will never connect - the node harness's own wall-clock
 * timeout is a second, independent backstop on top of that, not a replacement for it.
 * <p>
 * <b>Per-namespace, per-dimension expectations, not "present somewhere".</b> A mod like BiomesOPlenty
 * adds content to overworld, nether, <i>and</i> end - a flat "found this namespace in any dimension"
 * check would silently pass even if BiomesOPlenty's nether integration broke, as long as its overworld
 * biomes still showed up. {@value #NAMESPACES_PROPERTY} is instead
 * {@code namespace1:dim1+dim2;namespace2:dim3}, e.g.
 * {@code biomesoplenty:overworld+nether+end;terralith:overworld;incendium:nether;nullscape:end} - each
 * namespace is checked against exactly the dimensions listed for it, no more and no less. Recognized
 * dimension aliases: {@code overworld}, {@code nether}, {@code end} (see {@link #DIMENSION_ALIASES}).
 * <p>
 * <b>{@value #DEFAULT_GENERATOR_PROPERTY} - does WoVer's own default generator survive other mods being
 * present.</b> Boolean flag, independent of the namespace checks above. WoVer's own dedicated-server
 * default-preset injection ({@code DedicatedServerPropertiesMixin}) is what makes a fresh install boot on
 * a WoVer preset at all - a third-party mod that also patches {@code DedicatedServerProperties} or the
 * world-preset registry could plausibly interfere with that. When set, asserts both {@code the_nether}
 * and {@code the_end} are on WoVer's own generator (class name compared by string - see
 * {@value #WOVER_CHUNK_GENERATOR_CLASS} - rather than a hard compile dependency on wover-generator-api's
 * {@code WoverChunkGenerator}, since this module is shared by every compat-test participant, not just
 * wover-generator-api itself). Failures are reported as {@code generator@the_nether}/{@code generator@the_end}
 * in the same {@code missing} list the namespace checks use.
 * <p>
 * <b>No system property set = not a compat run at all.</b> Registers the SERVER_STARTED listener
 * unconditionally (this class is on every testmod's normal {@code main} entrypoint list, so it loads on
 * every ordinary {@code testmodServer}/{@code runServer} run too), but the listener itself is a no-op
 * unless {@value #NAMESPACES_PROPERTY} or {@value #DEFAULT_GENERATOR_PROPERTY} is set - only the
 * compat-test harness's dedicated {@code runCompatWorldgenBoot} run passes either, so a normal dev run
 * never halts early.
 */
public class CompatWorldgenBootCheck implements ModInitializer {
    private static final String NAMESPACES_PROPERTY = "betterx.compat.expectWorldgen";
    private static final String DEFAULT_GENERATOR_PROPERTY = "betterx.compat.expectDefaultGenerator";
    private static final String RESULT_FILE_PROPERTY = "betterx.compat.worldgenResultFile";
    private static final String WOVER_CHUNK_GENERATOR_CLASS = "de.ambertation.wover.generator.impl.chunkgenerator.WoverChunkGenerator";

    private static final Map<String, String> DIMENSION_ALIASES = Map.of(
            "overworld", "minecraft:overworld",
            "nether", "minecraft:the_nether",
            "end", "minecraft:the_end"
    );

    @Override
    public void onInitialize() {
        final Map<String, Set<String>> expected = expectedNamespaceDimensions();
        final boolean expectDefaultGenerator = Boolean.getBoolean(DEFAULT_GENERATOR_PROPERTY);
        if (expected.isEmpty() && !expectDefaultGenerator) {
            return;
        }

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            try {
                runCheck(server, expected, expectDefaultGenerator);
            } finally {
                server.halt(false);
            }
        });
    }

    /** Parses {@code namespace1:dim1+dim2;namespace2:dim3} into {namespace -> {full dimension ids}}. */
    private static Map<String, Set<String>> expectedNamespaceDimensions() {
        final String raw = System.getProperty(NAMESPACES_PROPERTY);
        if (raw == null || raw.isBlank()) return Map.of();

        final Map<String, Set<String>> expected = new LinkedHashMap<>();
        for (String entry : raw.split(";")) {
            final String trimmed = entry.trim();
            if (trimmed.isEmpty()) continue;
            final String[] parts = trimmed.split(":", 2);
            final String namespace = parts[0].trim();
            final Set<String> dimensions = new LinkedHashSet<>();
            if (parts.length > 1) {
                for (String alias : parts[1].split("\\+")) {
                    final String trimmedAlias = alias.trim();
                    if (trimmedAlias.isEmpty()) continue;
                    dimensions.add(DIMENSION_ALIASES.getOrDefault(trimmedAlias, trimmedAlias));
                }
            }
            if (!namespace.isEmpty() && !dimensions.isEmpty()) {
                expected.put(namespace, dimensions);
            }
        }
        return expected;
    }

    private static void runCheck(MinecraftServer server, Map<String, Set<String>> expected, boolean expectDefaultGenerator) {
        final Map<String, Set<String>> presentByDimension = new LinkedHashMap<>();
        for (ServerLevel level : server.getAllLevels()) {
            presentByDimension.put(
                    level.dimension().identifier().toString(),
                    namespacesIn(level.getChunkSource().getGenerator().getBiomeSource())
            );
        }

        final List<String> failures = new ArrayList<>();
        for (var entry : expected.entrySet()) {
            final String namespace = entry.getKey();
            for (String dimension : entry.getValue()) {
                final Set<String> present = presentByDimension.get(dimension);
                if (present == null || !present.contains(namespace)) {
                    failures.add(namespace + "@" + dimension);
                }
            }
        }

        if (expectDefaultGenerator) {
            for (var entry : Map.of("the_nether", Level.NETHER, "the_end", Level.END).entrySet()) {
                final ServerLevel level = server.getLevel(entry.getValue());
                final ChunkGenerator generator = level == null ? null : level.getChunkSource().getGenerator();
                if (generator == null || !generator.getClass().getName().equals(WOVER_CHUNK_GENERATOR_CLASS)) {
                    failures.add("generator@" + entry.getKey());
                }
            }
        }

        writeResult(failures.isEmpty(), expected, presentByDimension, failures);
    }

    private static void writeResult(
            boolean passed, Map<String, Set<String>> expected, Map<String, Set<String>> presentByDimension,
            List<String> failures
    ) {
        final String resultFile = System.getProperty(RESULT_FILE_PROPERTY);
        if (resultFile == null || resultFile.isBlank()) {
            return;
        }
        final StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"status\": \"").append(passed ? "PASS" : "FAIL").append("\",\n");
        json.append("  \"expected\": {\n");
        appendStringSetMap(json, expected);
        json.append("  },\n");
        json.append("  \"missing\": ").append(jsonArray(failures)).append(",\n");
        json.append("  \"presentByDimension\": {\n");
        appendStringSetMap(json, presentByDimension);
        json.append("  }\n");
        json.append("}\n");

        final Path path = Path.of(resultFile);
        if (path.getParent() != null) {
            path.getParent().toFile().mkdirs();
        }
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(json.toString());
        } catch (IOException e) {
            throw new RuntimeException("compat-test: failed to write worldgen boot-check result to " + resultFile, e);
        }
    }

    private static void appendStringSetMap(StringBuilder json, Map<String, Set<String>> map) {
        int i = 0;
        for (var entry : map.entrySet()) {
            json.append("    \"").append(escape(entry.getKey())).append("\": ").append(jsonArray(entry.getValue()));
            if (++i < map.size()) json.append(",");
            json.append("\n");
        }
    }

    private static String jsonArray(Iterable<String> values) {
        final StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (String value : values) {
            if (!first) sb.append(", ");
            sb.append("\"").append(escape(value)).append("\"");
            first = false;
        }
        return sb.append("]").toString();
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /** Every distinct namespace among the possible biomes a BiomeSource can produce. */
    private static Set<String> namespacesIn(BiomeSource source) {
        final Set<String> namespaces = new LinkedHashSet<>();
        for (Holder<Biome> biome : source.possibleBiomes()) {
            biome.unwrapKey().map(ResourceKey::identifier).map(Identifier::getNamespace).ifPresent(namespaces::add);
        }
        return namespaces;
    }
}
