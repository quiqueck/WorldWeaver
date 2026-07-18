package org.betterx.wover.testmod.gametest;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.GameTest;

/**
 * Fabric GameTest for Concern 3 (world presets): custom world-preset datapack files get loaded, and
 * programmatically-registered presets get injected into the world.
 * <p>
 * The testmod contributes presets two ways, both of which must appear in the vanilla
 * {@link Registries#WORLD_PRESET} registry once the server is up:
 * <ul>
 *   <li>{@code wover:nether_start} - written as a committed datapack JSON under
 *       {@code src/testmod/generated/data/wover/worldgen/world_preset/} and loaded as data;</li>
 *   <li>{@code wover-preset-testmod:end_start} - registered programmatically via
 *       {@code WorldPresetManager.BOOTSTRAP_WORLD_PRESETS}.</li>
 * </ul>
 * A missing preset is a real regression in datapack loading or preset registration.
 */
public class WorldPresetGameTest {
    private static final String DATAPACK_PRESET = "wover:nether_start";
    private static final String INJECTED_PRESET = "wover-preset-testmod:end_start";

    @GameTest
    public void customWorldPresetsAreLoadedAndInjected(GameTestHelper helper) {
        final Registry<WorldPreset> registry = helper
                .getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.WORLD_PRESET);

        final List<String> failures = new ArrayList<>();

        if (!registry.containsKey(ResourceLocation.parse(DATAPACK_PRESET))) {
            failures.add(DATAPACK_PRESET + ": expected world preset loaded from datapack JSON but it is missing");
        }
        if (!registry.containsKey(ResourceLocation.parse(INJECTED_PRESET))) {
            failures.add(INJECTED_PRESET + ": expected world preset injected via BOOTSTRAP_WORLD_PRESETS but it is missing");
        }

        if (!failures.isEmpty()) {
            final List<String> present = new ArrayList<>();
            registry.keySet().forEach(k -> present.add(k.toString()));
            helper.fail(Component.literal(
                    "World-preset registry regression:\n - " + String.join("\n - ", failures)
                            + "\n\nPresets actually present: " + present
            ));
            return;
        }

        helper.succeed();
    }
}
