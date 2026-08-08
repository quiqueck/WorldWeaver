package de.ambertation.wover.testmod.gametest;

import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.biome.api.data.BiomeDataRegistry;
import de.ambertation.wover.biome.api.modification.BiomeModification;
import de.ambertation.wover.biome.api.modification.BiomeModificationRegistry;
import de.ambertation.wover.biome.api.modification.predicates.BiomePredicate;
import de.ambertation.wover.biome.api.modification.predicates.BiomePredicateRegistry;
import de.ambertation.wover.config.api.Configs;
import de.ambertation.wover.testmod.entrypoint.TestModWoverBiome;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.placement.NetherPlacements;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Fabric GameTest for wover-biome-api: the worldgen content that the testmod declares must all be
 * present in the live registries once the server is up.
 * <p>
 * The testmod contributes biome content across three registries, and this test guards each of them:
 * <ul>
 *   <li><b>Vanilla biome registry</b> — the testmod datagen writes committed JSON under
 *       {@code src/testmod/generated/data/.../worldgen/biome/} (test_biome), loaded as data into the
 *       built-in {@link Registries#BIOME}. Its presence proves datapack loading + processing of the
 *       biome registry still works.</li>
 *   <li><b>Custom BiomeData registry</b> — wover-biome-api adds its own datapack-backed registry
 *       {@link BiomeDataRegistry#BIOME_DATA_REGISTRY} ({@code wover:wover/worldgen/biome_data}). The
 *       testmod datagen writes committed JSON under {@code .../wover/worldgen/biome_data/} (savanna),
 *       so this facet proves the custom registry is created and loaded from datapack JSON.</li>
 *   <li><b>Custom BiomeModification registry</b> — the datapack-backed
 *       {@link BiomeModificationRegistry#BIOME_MODIFICATION_REGISTRY}
 *       ({@code wover:wover/worldgen/biome_modifications}) is populated two ways: from committed JSON
 *       (test_features, test_tags) and programmatically at bootstrap by {@code TestModWoverBiome}
 *       (runtime_modification, subscribed via {@code BOOTSTRAP_BIOME_MODIFICATION_REGISTRY}). Asserting
 *       all three proves both datapack loading and runtime modification-injection still fire.</li>
 * </ul>
 * A missing entry is a real regression in biome datapack loading or in one of the custom-registry paths.
 * The negative assertion guards against the test silently passing on an always-true registry.
 */
public class BiomeGameTest {
    private static final String NS = "wover-biome-testmod";

    // Biome loaded from the committed datapack JSON (worldgen/biome/test_biome.json).
    private static final List<String> DATAPACK_BIOMES = List.of(
            NS + ":test_biome"
    );

    // BiomeData entry loaded from the committed datapack JSON (wover/worldgen/biome_data/savanna.json).
    private static final List<String> DATAPACK_BIOME_DATA = List.of(
            NS + ":savanna"
    );

    // Biome modifications loaded from committed datapack JSON (wover/worldgen/biome_modifications/*.json).
    private static final List<String> DATAPACK_BIOME_MODIFICATIONS = List.of(
            NS + ":test_features",
            NS + ":test_tags"
    );

    // Modifications injected programmatically at bootstrap (TestModWoverBiome#onInitialize, subscribed to
    // BOOTSTRAP_BIOME_MODIFICATION_REGISTRY) - not from any datapack JSON.
    private static final String INJECTED_BIOME_MODIFICATION = NS + ":runtime_modification";
    private static final String INJECTED_SPAWN_MODIFICATION = NS + ":runtime_spawn_modification";

    // A plausible-but-absent biome id: never registered by the testmod. Its presence would mean the
    // containsKey checks are meaningless, so this makes the test able to fail on a regression.
    private static final String ABSENT_BIOME = NS + ":does_not_exist_biome";

    @GameTest
    public void biomeContentIsLoadedAndInjected(GameTestHelper helper) {
        final Registry<Biome> biomes = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.BIOME);
        final Registry<BiomeData> biomeData = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(BiomeDataRegistry.BIOME_DATA_REGISTRY);
        final Registry<BiomeModification> biomeModifications = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(BiomeModificationRegistry.BIOME_MODIFICATION_REGISTRY);

        final List<String> failures = new ArrayList<>();

        for (String id : DATAPACK_BIOMES) {
            if (!biomes.containsKey(Identifier.parse(id))) {
                failures.add(id + ": expected biome loaded from datapack JSON but it is missing");
            }
        }

        for (String id : DATAPACK_BIOME_DATA) {
            if (!biomeData.containsKey(Identifier.parse(id))) {
                failures.add(id + ": expected BiomeData loaded from datapack JSON but it is missing");
            }
        }

        for (String id : DATAPACK_BIOME_MODIFICATIONS) {
            if (!biomeModifications.containsKey(Identifier.parse(id))) {
                failures.add(id + ": expected biome modification loaded from datapack JSON but it is missing");
            }
        }

        for (String id : List.of(INJECTED_BIOME_MODIFICATION, INJECTED_SPAWN_MODIFICATION)) {
            if (!biomeModifications.containsKey(Identifier.parse(id))) {
                failures.add(id + ": expected modification injected via BOOTSTRAP_BIOME_MODIFICATION_REGISTRY but it is missing");
            }
        }

        // Negative guard: an id the testmod never registers must not be present.
        if (biomes.containsKey(Identifier.parse(ABSENT_BIOME))) {
            failures.add(ABSENT_BIOME + ": unexpectedly present - the containsKey assertions are not meaningful");
        }

        if (!failures.isEmpty()) {
            final List<String> presentBiomes = new ArrayList<>();
            biomes.keySet().forEach(k -> presentBiomes.add(k.toString()));
            final List<String> presentData = new ArrayList<>();
            biomeData.keySet().forEach(k -> presentData.add(k.toString()));
            final List<String> presentMods = new ArrayList<>();
            biomeModifications.keySet().forEach(k -> presentMods.add(k.toString()));
            helper.fail(Component.literal(
                    "Biome registry regression:\n - " + String.join("\n - ", failures)
                            + "\n\nBiomeData actually present: " + presentData
                            + "\n\nBiome modifications actually present: " + presentMods
            ));
            return;
        }

        helper.succeed();
    }

    /**
     * A {@link BiomeModification} that adds mob spawns must actually reach the {@link Biome}, and must not
     * drop the spawns or spawn costs the Biome already had.
     * <p>
     * This is the end-to-end guard for {@code MobSettingsWorker}, which 26.3 forced to be rewritten:
     * {@code Biome#getMobSettings()} is gone and a Biome's {@link MobSpawnSettings} now live in its
     * {@link net.minecraft.world.attribute.EnvironmentAttributeMap} under
     * {@link EnvironmentAttributes#NATURAL_MOB_SPAWNS}. Registry presence (asserted above) says nothing
     * about whether the modification was applied, so without this test the whole rewrite was untested:
     * a worker that installs nothing, reads the wrong attribute, or rebuilds the settings lossily all
     * load, datagen and boot identically.
     * <p>
     * Note what each assertion catches:
     * <ul>
     *     <li>the added ocelot spawn missing -&gt; the new attribute value was never installed, or was
     *     installed on a copy of the Biome;</li>
     *     <li>the vanilla spawns missing -&gt; the rebuild seeded from the wrong source (for instance
     *     from {@code getMobsToSpawn}, which substitutes the empty list, instead of the declared
     *     categories);</li>
     *     <li>the vanilla spawn costs missing -&gt; {@code addAllCosts} was skipped, which
     *     {@code MobSpawnSettings.Builder} would otherwise silently drop.</li>
     * </ul>
     */
    @GameTest
    public void biomeModificationAddsMobSpawns(GameTestHelper helper) {
        final Registry<Biome> biomes = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME);
        final List<String> failures = new ArrayList<>();

        final Biome badlands = biomes.getValueOrThrow(Biomes.BADLANDS);
        final MobSpawnSettings modified = readMobSpawns(badlands);

        final MobSpawnSettings.SpawnerData added = modified
                .getMobsToSpawn(EntityTypes.OCELOT.getCategory())
                .unwrap()
                .stream()
                .filter(w -> w.value().type() == EntityTypes.OCELOT
                        && w.weight() == TestModWoverBiome.SPAWN_WEIGHT)
                .map(Weighted::value)
                .findFirst()
                .orElse(null);

        if (added == null) {
            failures.add("badlands: the ocelot spawn added by " + INJECTED_SPAWN_MODIFICATION
                    + " (weight " + TestModWoverBiome.SPAWN_WEIGHT + ") is not present in "
                    + EntityTypes.OCELOT.getCategory() + "; the modification was not applied to the Biome. "
                    + "Present: " + describe(modified));
        } else {
            // The group count must survive as the IntProvider the builder derives from min/max.
            final int min = added.count().minInclusive();
            final int max = added.count().maxInclusive();
            if (min != TestModWoverBiome.SPAWN_MIN_GROUP || max != TestModWoverBiome.SPAWN_MAX_GROUP) {
                failures.add("badlands: ocelot group count is [" + min + ".." + max + "], expected ["
                        + TestModWoverBiome.SPAWN_MIN_GROUP + ".." + TestModWoverBiome.SPAWN_MAX_GROUP + "]");
            }
        }

        // Badlands' own vanilla spawns must still be there - the rebuild has to be additive.
        if (modified.getMobsToSpawn(MobCategory.MONSTER).unwrap().isEmpty()) {
            failures.add("badlands: the vanilla MONSTER spawns were lost by the modification rebuild. "
                    + "Present: " + describe(modified));
        }

        // Spawn costs live in a second map that MobSpawnSettings.Builder only carries over on an explicit
        // addAllCosts call, so dropping it is silent. No overworld Biome declares any cost, which is why
        // this half of the assertion has to run against soul sand valley.
        final MobSpawnSettings soulSandValley = readMobSpawns(biomes.getValueOrThrow(Biomes.SOUL_SAND_VALLEY));
        if (soulSandValley.allSpawnCosts().isEmpty()) {
            failures.add("soul_sand_valley: the vanilla spawn costs were lost by the modification rebuild "
                    + "(MobSpawnSettings.Builder#addAllCosts was not carried over)");
        }
        final boolean soulSandValleyGotOcelot = soulSandValley
                .getMobsToSpawn(EntityTypes.OCELOT.getCategory())
                .unwrap()
                .stream()
                .anyMatch(w -> w.value().type() == EntityTypes.OCELOT
                        && w.weight() == TestModWoverBiome.SPAWN_WEIGHT);
        if (!soulSandValleyGotOcelot) {
            failures.add("soul_sand_valley: the ocelot spawn added by " + INJECTED_SPAWN_MODIFICATION
                    + " is missing. Present: " + describe(soulSandValley));
        }

        // A Biome the modification does not match must be untouched.
        final Biome desert = biomes.getValueOrThrow(Biomes.DESERT);
        final boolean desertHasOcelot = readMobSpawns(desert)
                .getMobsToSpawn(EntityTypes.OCELOT.getCategory())
                .unwrap()
                .stream()
                .anyMatch(w -> w.value().type() == EntityTypes.OCELOT
                        && w.weight() == TestModWoverBiome.SPAWN_WEIGHT);
        if (desertHasOcelot) {
            failures.add("desert: got the ocelot spawn even though " + INJECTED_SPAWN_MODIFICATION
                    + " only matches badlands - the modification is leaking across Biomes");
        }

        // 26.3 hazard guard: when a Biome declares no spawns of its own, reading NATURAL_MOB_SPAWNS
        // yields the attribute's default, which is the shared singleton MobSpawnSettings.EMPTY. The 26.1
        // worker mutated the MobSpawnSettings it was handed in place; doing that now would write the
        // modification's spawns into EMPTY and leak them into every such Biome in the game. The worker
        // therefore installs a fresh value instead of mutating, and this asserts EMPTY stayed empty.
        if (!MobSpawnSettings.EMPTY.definedCategories().isEmpty()
                || !MobSpawnSettings.EMPTY.allSpawnCosts().isEmpty()) {
            failures.add("MobSpawnSettings.EMPTY was mutated (" + describe(MobSpawnSettings.EMPTY)
                    + "); a biome modification wrote into the shared default instead of installing its own"
                    + " NATURAL_MOB_SPAWNS value");
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal("Mob spawn modification regression:\n - "
                    + String.join("\n - ", failures)));
            return;
        }

        helper.succeed();
    }

    /**
     * A {@link BiomeModification} that adds features must actually reach the {@link Biome}'s
     * {@link net.minecraft.world.level.biome.BiomeGenerationSettings}.
     * <p>
     * This is the end-to-end guard for {@code GenerationSettingsWorker}. 26.3 retyped the two fields it
     * writes through {@code BiomeGenerationSettingsAccessor} — {@code carvers} became
     * {@code HolderSet<WorldCarver>} and {@code boneMealFeatures} a
     * {@code Supplier<List<Feature>>} — and a field {@code @Accessor} whose target was retyped still
     * applies while handing out the wrong thing, with no compile-time or boot-time complaint. Asserting on
     * the resulting feature list is what turns that into a visible failure.
     */
    @GameTest
    public void biomeModificationAddsFeatures(GameTestHelper helper) {
        final Registry<Biome> biomes = helper.getLevel().registryAccess().lookupOrThrow(Registries.BIOME);
        final List<String> failures = new ArrayList<>();

        // wover-biome-testmod:test_features adds small_basalt_columns to beach + meadow at
        // SURFACE_STRUCTURES; runtime_modification adds it to meadow at VEGETAL_DECORATION.
        assertHasPlacedFeature(biomes, Biomes.BEACH, NetherPlacements.SMALL_BASALT_COLUMNS, true, failures);
        assertHasPlacedFeature(biomes, Biomes.MEADOW, NetherPlacements.SMALL_BASALT_COLUMNS, true, failures);
        // Badlands is only touched by the spawn modification, so it must not have gained the feature.
        assertHasPlacedFeature(biomes, Biomes.BADLANDS, NetherPlacements.SMALL_BASALT_COLUMNS, false, failures);

        if (!failures.isEmpty()) {
            helper.fail(Component.literal("Feature modification regression:\n - "
                    + String.join("\n - ", failures)));
            return;
        }

        helper.succeed();
    }

    /**
     * Every {@link de.ambertation.wover.biome.api.modification.predicates.BiomePredicate} type must be
     * registered and must survive a {@link BiomePredicate#CODEC} round-trip.
     * <p>
     * 26.3 removed {@code KeyDispatchDataCodec}, so {@link BiomePredicate#codec()} now returns a
     * {@link com.mojang.serialization.MapCodec} and {@code BiomePredicate.CODEC} dispatches on it
     * directly. That is a change to how every predicate serializes, and the predicate registry is a
     * built-in registry populated by hand — a predicate whose codec is not registered only fails when a
     * datapack happens to use it, which no other test does.
     * <p>
     * This also pins the renamed {@code wover:has_feature} type (was
     * {@code wover:has_configured_feature}, named after the deleted {@code ConfiguredFeature}).
     */
    @GameTest
    public void biomePredicateCodecsRoundTrip(GameTestHelper helper) {
        final RegistryAccess access = helper.getLevel().registryAccess();
        final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, access);
        final List<String> failures = new ArrayList<>();

        // One instance per registered predicate type, so an unregistered codec cannot hide.
        final List<BiomePredicate> samples = List.of(
                BiomePredicate.always(),
                BiomePredicate.not(BiomePredicate.always()),
                BiomePredicate.and(BiomePredicate.always(), BiomePredicate.isBiome(Biomes.BADLANDS)),
                BiomePredicate.or(BiomePredicate.always(), BiomePredicate.isBiome(Biomes.MEADOW)),
                BiomePredicate.isBiome(Biomes.DESERT),
                BiomePredicate.hasTag(BiomeTags.IS_OVERWORLD),
                BiomePredicate.inOverworld(),
                BiomePredicate.inNamespace("minecraft"),
                BiomePredicate.pathContains("badlands"),
                BiomePredicate.spawns(EntityTypes.OCELOT),
                BiomePredicate.hasStructure(BuiltinStructures.DESERT_PYRAMID),
                BiomePredicate.hasPlacedFeature(NetherPlacements.SMALL_BASALT_COLUMNS),
                // Needs a Value whose parent config file is actually registered, hence Configs.MAIN.
                BiomePredicate.hasConfig(Configs.MAIN.verboseLogging, true),
                // The renamed predicate: ResourceKey<Feature> in Registries.FEATURE.
                BiomePredicate.hasFeature(ResourceKey.create(
                        Registries.FEATURE,
                        Identifier.withDefaultNamespace("small_basalt_columns")
                ))
        );

        final Set<String> seenTypes = new HashSet<>();
        for (BiomePredicate predicate : samples) {
            final DataResult<JsonElement> encoded = BiomePredicate.CODEC.encodeStart(ops, predicate);
            if (encoded.isError()) {
                failures.add(predicate.getClass().getSimpleName() + ": cannot be encoded - "
                        + encoded.error().orElseThrow().message());
                continue;
            }
            final JsonElement json = encoded.getOrThrow();
            final JsonElement type = json.isJsonObject() ? json.getAsJsonObject().get("type") : null;
            if (type == null) {
                failures.add(predicate.getClass().getSimpleName()
                        + ": encoded without a \"type\" discriminator: " + json);
            } else {
                seenTypes.add(type.getAsString());
            }

            final DataResult<BiomePredicate> decoded = BiomePredicate.CODEC
                    .parse(ops, json)
                    .map(p -> (BiomePredicate) p);
            if (decoded.isError()) {
                failures.add(predicate.getClass().getSimpleName() + ": cannot be decoded from " + json
                        + " - " + decoded.error().orElseThrow().message());
                continue;
            }
            final JsonElement reEncoded = BiomePredicate.CODEC
                    .encodeStart(ops, decoded.getOrThrow())
                    .getOrThrow();
            if (!json.equals(reEncoded)) {
                failures.add(predicate.getClass().getSimpleName()
                        + ": round-trip is not stable: " + json + " -> " + reEncoded);
            }
        }

        // Every codec in the registry must have been covered by a sample above, so that a predicate type
        // added later cannot quietly go untested.
        BiomePredicateRegistry.BIOME_PREDICATES
                .keySet()
                .forEach(id -> {
                    if (!seenTypes.contains(id.toString())) {
                        failures.add(id + ": registered predicate type is not covered by this test");
                    }
                });
        if (!seenTypes.contains("wover:has_feature")) {
            failures.add("wover:has_feature: the renamed predicate type is not registered");
        }

        if (!failures.isEmpty()) {
            helper.fail(Component.literal("BiomePredicate codec regression:\n - "
                    + String.join("\n - ", failures)
                    + "\n\nRegistered types: " + BiomePredicateRegistry.BIOME_PREDICATES.keySet()));
            return;
        }

        helper.succeed();
    }

    /**
     * Reads the {@link MobSpawnSettings} of a {@link Biome} the way vanilla does at runtime.
     * <p>
     * Deliberately does not go through {@code MobSettingsWorker#mobSettingsOf}: the test has to be able to
     * catch that helper reading the wrong attribute, so it resolves the attribute independently.
     */
    private static MobSpawnSettings readMobSpawns(Biome biome) {
        return biome.getAttributes().applyModifier(
                EnvironmentAttributes.NATURAL_MOB_SPAWNS,
                MobSpawnSettings.EMPTY
        );
    }

    private static void assertHasPlacedFeature(
            Registry<Biome> biomes,
            ResourceKey<Biome> biomeKey,
            ResourceKey<PlacedFeature> featureKey,
            boolean expected,
            List<String> failures
    ) {
        final Biome biome = biomes.getValueOrThrow(biomeKey);
        final boolean found = biome
                .getGenerationSettings()
                .features()
                .stream()
                .flatMap(HolderSet::stream)
                .map(Holder::unwrapKey)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .anyMatch(featureKey::equals);

        if (found != expected) {
            failures.add(biomeKey.identifier() + ": " + featureKey.identifier()
                    + (expected
                    ? " is missing - the feature modification was not applied to this Biome"
                    : " is present - the feature modification leaked into a Biome it does not match"));
        }
    }

    private static String describe(MobSpawnSettings settings) {
        final List<String> parts = new ArrayList<>();
        for (MobCategory category : settings.definedCategories()) {
            parts.add(category + "=" + settings.getMobsInCategory(category)
                                              .unwrap()
                                              .stream()
                                              .map(w -> EntityType.getKey(w.value().type()) + "@" + w.weight())
                                              .toList());
        }
        return parts + " costs=" + settings.allSpawnCosts().size();
    }
}
