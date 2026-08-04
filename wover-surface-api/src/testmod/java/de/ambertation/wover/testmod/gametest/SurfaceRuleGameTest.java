package de.ambertation.wover.testmod.gametest;

import de.ambertation.wover.surface.api.AssignedSurfaceRule;
import de.ambertation.wover.surface.api.SurfaceRuleRegistry;

import net.minecraft.core.Registry;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Fabric GameTest for Concern 3 (surface rules): "custom datapack files get loaded and processed" and
 * "custom rules get injected into the world".
 * <p>
 * The testmod contributes surface rules two ways, and both must end up in the world's
 * {@link SurfaceRuleRegistry#SURFACE_RULES_REGISTRY} once the server is up:
 * <ul>
 *   <li>as committed datapack JSON files under {@code src/testmod/generated/data/.../surface_rules/}
 *       (test-plains, test-desert, …) - these prove datapack loading + processing of the custom
 *       registry;</li>
 *   <li>programmatically via {@code SurfaceRuleRegistry.BOOTSTRAP_SURFACE_RULE_REGISTRY} (test-savana)
 *       - this proves rule <em>injection</em> at bootstrap.</li>
 * </ul>
 * This test reads the live registry back from {@code registryAccess()} and asserts every expected rule
 * is present. A missing rule is a real regression in datapack loading or rule injection.
 */
public class SurfaceRuleGameTest {
    // Rules loaded from the committed datapack JSON files (always-on testmod data).
    private static final List<String> DATAPACK_RULES = List.of(
            "wover-surface-testmod:test-plains",
            "wover-surface-testmod:test-desert",
            "wover-surface-testmod:test-flower-forrest",
            "wover-surface-testmod:test-plains-below",
            "wover-surface-testmod:test-beach"
    );

    // Rule injected programmatically at bootstrap (TestModWoverSurface, guarded by !isDatagen()).
    private static final String INJECTED_RULE = "wover-surface-testmod:test-savana";

    @GameTest
    public void customSurfaceRulesAreLoadedAndInjected(GameTestHelper helper) {
        final Registry<AssignedSurfaceRule> registry = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(SurfaceRuleRegistry.SURFACE_RULES_REGISTRY);

        final List<String> failures = new ArrayList<>();

        for (String id : DATAPACK_RULES) {
            if (!registry.containsKey(ResourceLocation.parse(id))) {
                failures.add(id + ": expected surface rule loaded from datapack JSON but it is missing");
            }
        }

        if (!registry.containsKey(ResourceLocation.parse(INJECTED_RULE))) {
            failures.add(INJECTED_RULE + ": expected surface rule injected via BOOTSTRAP_SURFACE_RULE_REGISTRY but it is missing");
        }

        if (!failures.isEmpty()) {
            final List<String> present = new ArrayList<>();
            registry.keySet().forEach(k -> present.add(k.toString()));
            helper.fail(Component.literal(
                    "Surface-rule registry regression:\n - " + String.join("\n - ", failures)
                            + "\n\nRules actually present: " + present
            ));
            return;
        }

        helper.succeed();
    }
}
