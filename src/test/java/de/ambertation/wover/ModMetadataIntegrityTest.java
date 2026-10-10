package de.ambertation.wover;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards the mod metadata of every module against dangling references.
 *
 * <p>WorldWeaver has no mixin annotation processor and no refmap, so nothing at compile time checks
 * that the class names written into {@code fabric.mod.json} and the {@code *.mixins.*.json} configs
 * still exist. A stale entry there is invisible to {@code ./gradlew build} and only surfaces as a hard
 * crash the moment Fabric tries to resolve it — and for entrypoints that are keyed by a run
 * configuration (notably {@code fabric-datagen}) that moment may only come during a datagen run, which
 * the normal build and the headless GameTest suite never reach.
 *
 * <p>That is not a hypothetical: the root aggregator's testmod carried a {@code fabric-datagen}
 * entrypoint pointing at {@code org.betterx.datagen.betterapi.Generator} — a class from the pre-rename
 * BetterX package layout that had not existed for several Minecraft generations. It survived the 1.21.8,
 * 26.1 and 26.3 ports untouched because no build step and no test ever looked at it.
 *
 * <p>These checks are deliberately structural (they read the committed sources, not the built jars) so
 * they stay meaningful without booting Minecraft, and they run as part of the ordinary {@code test} task.
 */
class ModMetadataIntegrityTest {
    /** Every source set that may contribute Java sources or resources to a module. */
    private static final List<String> SOURCE_SETS =
            List.of("main", "client", "datagen", "testmod", "testmodClient", "testmodDatagen");

    private static final Gson GSON = new Gson();

    /**
     * Walks up from the working directory until it finds the repository root. Gradle runs a {@code Test}
     * task with the owning project's directory as the working directory, so for the root project this is
     * already the answer — but resolving it explicitly keeps the test runnable from an IDE too.
     */
    private static Path repoRoot() {
        Path p = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        while (p != null) {
            if (Files.isRegularFile(p.resolve("settings.gradle")) && Files.isRegularFile(p.resolve("common.gradle"))) {
                return p;
            }
            p = p.getParent();
        }
        throw new IllegalStateException("could not locate the WorldWeaver repository root");
    }

    /**
     * All module roots: the aggregator at {@code /} plus every {@code wover-*} module. Deliberately does
     * not use the Gradle model — this test is about what is committed on disk.
     */
    private static List<Path> moduleRoots(Path repo) {
        List<Path> roots = new ArrayList<>();
        roots.add(repo);
        try (Stream<Path> s = Files.list(repo)) {
            s.filter(Files::isDirectory)
             .filter(d -> d.getFileName().toString().startsWith("wover-"))
             .sorted()
             .forEach(roots::add);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return roots;
    }

    /** Every {@code fabric.mod.json} committed under a module's source sets. */
    private static List<Path> modJsons(Path repo) {
        List<Path> out = new ArrayList<>();
        for (Path module : moduleRoots(repo)) {
            for (String set : SOURCE_SETS) {
                Path p = module.resolve("src").resolve(set).resolve("resources").resolve("fabric.mod.json");
                if (Files.isRegularFile(p)) out.add(p);
            }
        }
        return out;
    }

    private static JsonObject read(Path p) {
        try {
            return GSON.fromJson(Files.readString(p), JsonObject.class);
        } catch (IOException e) {
            throw new UncheckedIOException("could not read " + p, e);
        }
    }

    /**
     * Resolves a binary class name to a committed {@code .java} file anywhere in the repository. Scoped
     * repo-wide rather than to the declaring module, because a module may legitimately name a class that
     * lives in a module it depends on; the failure this guards against is a name that exists nowhere.
     */
    private static boolean classExists(Path repo, String binaryName) {
        // Nested classes are written Outer$Inner in metadata but live in Outer.java.
        String topLevel = binaryName.split("\\$")[0];
        String relative = topLevel.replace('.', '/') + ".java";
        for (Path module : moduleRoots(repo)) {
            for (String set : SOURCE_SETS) {
                if (Files.isRegularFile(module.resolve("src").resolve(set).resolve("java").resolve(relative))) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Resolves a resource path (mixin config, access widener) within the declaring module. */
    private static boolean resourceExists(Path module, String name) {
        for (String set : SOURCE_SETS) {
            if (Files.isRegularFile(module.resolve("src").resolve(set).resolve("resources").resolve(name))) {
                return true;
            }
        }
        return false;
    }

    private static Path moduleOf(Path repo, Path modJson) {
        // <module>/src/<set>/resources/fabric.mod.json -> <module>
        return modJson.getParent().getParent().getParent().getParent();
    }

    private static List<String> stringsOf(JsonElement el) {
        List<String> out = new ArrayList<>();
        if (el == null || !el.isJsonArray()) return out;
        for (JsonElement e : el.getAsJsonArray()) {
            if (e.isJsonPrimitive()) {
                out.add(e.getAsString());
            } else if (e.isJsonObject()) {
                // entrypoints may be {"adapter": ..., "value": ...}; mixins may be {"config": ...}
                JsonObject o = e.getAsJsonObject();
                for (String key : List.of("value", "config")) {
                    if (o.has(key)) out.add(o.get(key).getAsString());
                }
            }
        }
        return out;
    }

    @Test
    void everyFabricModJsonEntrypointResolvesToACommittedClass() {
        Path repo = repoRoot();
        List<Path> jsons = modJsons(repo);
        assertFalse(jsons.isEmpty(), "found no fabric.mod.json files - is the repository root wrong?");

        List<String> problems = new ArrayList<>();
        int checked = 0;
        for (Path json : jsons) {
            JsonObject root = read(json);
            if (!root.has("entrypoints")) continue;
            JsonObject entrypoints = root.getAsJsonObject("entrypoints");
            for (String kind : entrypoints.keySet()) {
                for (String raw : stringsOf(entrypoints.get(kind))) {
                    checked++;
                    // "com.example.Foo::method" references a static method/field on Foo.
                    String className = raw.split("::")[0].trim();
                    if (!classExists(repo, className)) {
                        problems.add(repo.relativize(json) + " -> entrypoint \"" + kind + "\": " + className);
                    }
                }
            }
        }
        assertTrue(checked > 0, "no entrypoints were checked");
        assertTrue(
                problems.isEmpty(),
                "fabric.mod.json declares entrypoint classes that do not exist in any module:\n  "
                        + String.join("\n  ", problems)
        );
    }

    @Test
    void everyDeclaredMixinConfigAndAccessWidenerExists() {
        Path repo = repoRoot();
        List<String> problems = new ArrayList<>();
        int checked = 0;
        for (Path json : modJsons(repo)) {
            Path module = moduleOf(repo, json);
            JsonObject root = read(json);
            for (String name : stringsOf(root.get("mixins"))) {
                checked++;
                if (!resourceExists(module, name)) {
                    problems.add(repo.relativize(json) + " -> missing mixin config: " + name);
                }
            }
            if (root.has("accessWidener")) {
                checked++;
                String aw = root.get("accessWidener").getAsString();
                if (!resourceExists(module, aw)) {
                    problems.add(repo.relativize(json) + " -> missing access widener: " + aw);
                }
            }
        }
        assertTrue(checked > 0, "no mixin configs or access wideners were checked");
        assertTrue(problems.isEmpty(), "fabric.mod.json references missing resources:\n  " + String.join("\n  ", problems));
    }

    @Test
    void everyMixinListedInAMixinConfigResolvesToACommittedClass() {
        Path repo = repoRoot();
        List<String> problems = new ArrayList<>();
        int checked = 0;
        for (Path module : moduleRoots(repo)) {
            for (String set : SOURCE_SETS) {
                Path resources = module.resolve("src").resolve(set).resolve("resources");
                if (!Files.isDirectory(resources)) continue;
                try (Stream<Path> s = Files.list(resources)) {
                    for (Path cfg : s.filter(p -> p.getFileName().toString().contains("mixins")
                            && p.getFileName().toString().endsWith(".json")).toList()) {
                        JsonObject root = read(cfg);
                        String pkg = root.has("package") ? root.get("package").getAsString() : "";
                        // "mixins" applies everywhere; "client"/"server" are environment-scoped lists.
                        for (String key : List.of("mixins", "client", "server")) {
                            JsonElement el = root.get(key);
                            if (el == null || !el.isJsonArray()) continue;
                            JsonArray arr = el.getAsJsonArray();
                            for (JsonElement e : arr) {
                                checked++;
                                String name = e.getAsString();
                                String fq = pkg.isEmpty() ? name : pkg + "." + name;
                                if (!classExists(repo, fq)) {
                                    problems.add(repo.relativize(cfg) + " -> " + key + ": " + fq);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
        assertTrue(checked > 0, "no mixin entries were checked");
        assertTrue(
                problems.isEmpty(),
                "mixin configs list mixins whose class does not exist:\n  " + String.join("\n  ", problems)
        );
    }
    /** Resolves a binary class name to its committed {@code .java} file, or {@code null}. */
    private static Path sourceOf(Path repo, String binaryName) {
        String relative = binaryName.split("\\$")[0].replace('.', '/') + ".java";
        for (Path module : moduleRoots(repo)) {
            for (String set : SOURCE_SETS) {
                Path p = module.resolve("src").resolve(set).resolve("java").resolve(relative);
                if (Files.isRegularFile(p)) return p;
            }
        }
        return null;
    }

    /**
     * Every class that defines a datapack registry through {@code DatapackRegistryBuilder.register(...)} has
     * to be initialized from a {@code wover.datapack.registry} entrypoint, not (only) from a {@code main}
     * entrypoint.
     *
     * <p>{@code RegistryDataLoader} captures its list of world registries once, in its static initializer,
     * and our mixin pulls the {@code wover.datapack.registry} entrypoints in from exactly there. A
     * {@code main} entrypoint, however, may run after that class was already loaded by another mod's
     * {@code main} entrypoint - TerraBlender does so through Fabric's {@code DynamicRegistries.register}.
     * A registry added that late is silently missing from every world. That is how
     * {@code wover/world_preset_info} went missing whenever TerraBlender was installed ("WorldPresetInfoRegistry:
     * Registry not read" on world creation, and the wrong per-dimension preset written to the world).
     *
     * <p>Checked structurally: the defining class's simple name must appear in the source of a class that is
     * declared as a {@code wover.datapack.registry} entrypoint (or be that class itself).
     */
    @Test
    void everyDatapackRegistryIsRegisteredFromTheDatapackRegistryEntrypoint() {
        Path repo = repoRoot();
        Set<Path> entrypointSources = new HashSet<>();
        List<String> entrypointCode = new ArrayList<>();
        for (Path json : modJsons(repo)) {
            JsonObject root = read(json);
            if (!root.has("entrypoints")) continue;
            for (String raw : stringsOf(root.getAsJsonObject("entrypoints").get("wover.datapack.registry"))) {
                Path src = sourceOf(repo, raw.split("::")[0].trim());
                if (src == null) continue; // reported by everyFabricModJsonEntrypointResolvesToACommittedClass
                entrypointSources.add(src);
                try {
                    entrypointCode.add(Files.readString(src));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
        assertFalse(entrypointSources.isEmpty(), "found no wover.datapack.registry entrypoints");

        List<String> problems = new ArrayList<>();
        int checked = 0;
        for (Path module : moduleRoots(repo)) {
            for (String set : SOURCE_SETS) {
                Path java = module.resolve("src").resolve(set).resolve("java");
                if (!Files.isDirectory(java)) continue;
                try (Stream<Path> s = Files.walk(java)) {
                    for (Path file : s.filter(p -> p.toString().endsWith(".java")).toList()) {
                        if (!Files.readString(file).contains("DatapackRegistryBuilder.register(")) continue;
                        checked++;
                        if (entrypointSources.contains(file)) continue;
                        String simpleName = file.getFileName().toString().replace(".java", "");
                        if (entrypointCode.stream().noneMatch(code -> code.contains(simpleName + "."))) {
                            problems.add(repo.relativize(file).toString());
                        }
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
        assertTrue(checked > 0, "found no DatapackRegistryBuilder.register(...) calls - did the API move?");
        assertTrue(
                problems.isEmpty(),
                "these classes define a datapack registry but are not initialized from a wover.datapack.registry "
                        + "entrypoint, so the registry is lost whenever RegistryDataLoader is loaded before our main "
                        + "entrypoint runs:\n  " + String.join("\n  ", problems)
        );
    }
}
