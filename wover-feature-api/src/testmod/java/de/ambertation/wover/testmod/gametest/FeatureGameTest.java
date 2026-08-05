package de.ambertation.wover.testmod.gametest;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Fabric GameTest for wover-feature-api: the worldgen features that the testmod declares must all be
 * present in the live registries once the server is up.
 * <p>
 * The testmod contributes feature content two distinct ways, and this test guards both:
 * <ul>
 *   <li><b>Datapack-loaded worldgen objects</b> — the testmod datagen writes committed JSON under
 *       {@code src/testmod/generated/data/.../worldgen/} which is loaded as data into the dynamic
 *       registries: {@link Registries#CONFIGURED_FEATURE} (test, test_datagen, random_spread,
 *       patch_basalt_stalactite) and {@link Registries#PLACED_FEATURE} (inline_feature,
 *       inline_feature_all, test_datagen, vanilla_feature). Their presence proves datapack loading +
 *       processing of the configured/placed-feature registries still works.</li>
 *   <li><b>Programmatically-registered feature types</b> — wover-feature-api registers its custom
 *       {@link Feature} subclasses (pillar, place_block, sequence, condition, template,
 *       mark_postprocessing) into the built-in {@link Registries#FEATURE} at class-init time (via
 *       {@code FeatureManagerImpl}), not from any datapack JSON. The testmod's own {@code test.json}
 *       configured feature is of type {@code wover:pillar}, so this facet must resolve for the datapack
 *       above to even decode. Their presence proves the programmatic feature-type registration still
 *       fires.</li>
 * </ul>
 * A missing entry is a real regression in feature datapack loading or in the type-registration path.
 * The negative assertion guards against the test silently passing on an always-true registry.
 */
public class FeatureGameTest {
    private static final String NS = "wover-feature-testmod";

    // Configured features loaded from the committed datapack JSON (worldgen/configured_feature/*.json).
    private static final List<String> DATAPACK_CONFIGURED_FEATURES = List.of(
            NS + ":test",
            NS + ":test_datagen",
            NS + ":random_spread",
            NS + ":patch_basalt_stalactite"
    );

    // Placed features loaded from the committed datapack JSON (worldgen/placed_feature/*.json).
    private static final List<String> DATAPACK_PLACED_FEATURES = List.of(
            NS + ":inline_feature",
            NS + ":inline_feature_all",
            NS + ":test_datagen",
            NS + ":vanilla_feature"
    );

    // Custom Feature types registered programmatically into the built-in FEATURE registry at class-init
    // (FeatureManagerImpl), under the production "wover" namespace - not from any datapack JSON.
    private static final List<String> PROGRAMMATIC_FEATURE_TYPES = List.of(
            "wover:pillar",
            "wover:place_block",
            "wover:sequence",
            "wover:condition",
            "wover:template",
            "wover:mark_postprocessing"
    );

    // A plausible-but-absent feature id: never registered by the testmod. Its presence would mean the
    // containsKey check is meaningless, so this makes the test able to fail on a regression.
    private static final String ABSENT_CONFIGURED_FEATURE = NS + ":does_not_exist_feature";

    @GameTest
    public void featuresAreLoadedAndTypesRegistered(GameTestHelper helper) {
        final Registry<ConfiguredFeature<?, ?>> configuredFeatures = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.CONFIGURED_FEATURE);
        final Registry<PlacedFeature> placedFeatures = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.PLACED_FEATURE);
        final Registry<Feature<?>> featureTypes = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.FEATURE);

        final List<String> failures = new ArrayList<>();

        for (String id : DATAPACK_CONFIGURED_FEATURES) {
            if (!configuredFeatures.containsKey(Identifier.parse(id))) {
                failures.add(id + ": expected configured_feature loaded from datapack JSON but it is missing");
            }
        }

        for (String id : DATAPACK_PLACED_FEATURES) {
            if (!placedFeatures.containsKey(Identifier.parse(id))) {
                failures.add(id + ": expected placed_feature loaded from datapack JSON but it is missing");
            }
        }

        for (String id : PROGRAMMATIC_FEATURE_TYPES) {
            if (!featureTypes.containsKey(Identifier.parse(id))) {
                failures.add(id + ": expected feature type registered programmatically at class-init but it is missing");
            }
        }

        // Negative guard: an id the testmod never registers must not be present.
        if (configuredFeatures.containsKey(Identifier.parse(ABSENT_CONFIGURED_FEATURE))) {
            failures.add(ABSENT_CONFIGURED_FEATURE + ": unexpectedly present - the containsKey assertions are not meaningful");
        }

        if (!failures.isEmpty()) {
            final List<String> presentConfigured = new ArrayList<>();
            configuredFeatures.keySet().forEach(k -> presentConfigured.add(k.toString()));
            helper.fail(Component.literal(
                    "Feature registry regression:\n - " + String.join("\n - ", failures)
                            + "\n\nConfigured features actually present: " + presentConfigured
            ));
            return;
        }

        helper.succeed();
    }
}
