package de.ambertation.wover.testmod.gametest;

import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.biome.api.data.BiomeDataRegistry;
import de.ambertation.wover.biome.api.modification.BiomeModification;
import de.ambertation.wover.biome.api.modification.BiomeModificationRegistry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;

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

    // Modification injected programmatically at bootstrap (TestModWoverBiome#onInitialize, subscribed to
    // BOOTSTRAP_BIOME_MODIFICATION_REGISTRY) - not from any datapack JSON.
    private static final String INJECTED_BIOME_MODIFICATION = NS + ":runtime_modification";

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

        if (!biomeModifications.containsKey(Identifier.parse(INJECTED_BIOME_MODIFICATION))) {
            failures.add(INJECTED_BIOME_MODIFICATION + ": expected modification injected via BOOTSTRAP_BIOME_MODIFICATION_REGISTRY but it is missing");
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
}
