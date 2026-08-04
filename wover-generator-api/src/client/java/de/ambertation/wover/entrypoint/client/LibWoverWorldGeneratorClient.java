package de.ambertation.wover.entrypoint.client;

import de.ambertation.wover.client.api.ClientTraitBootstrap;
import de.ambertation.wover.config.api.client.ClientConfigs;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.events.api.client.ClientWorldLifecycle;
import de.ambertation.wover.generator.api.preset.WorldPresets;
import de.ambertation.wover.generator.impl.chunkgenerator.ConfiguredChunkGenerator;
import de.ambertation.wover.generator.impl.client.WorldSetupScreen;
import de.ambertation.wover.preset.api.WorldPresetManager;
import de.ambertation.wover.preset.api.client.WorldPresetsUI;

import net.minecraft.world.level.dimension.LevelStem;

import net.fabricmc.api.ClientModInitializer;

public class LibWoverWorldGeneratorClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Collect every mod's wover.client.traits registrations (including this module's own
        // WoverBiomeSourceConfigScreenFactories), then apply the render appliers (idempotent).
        ClientTraitBootstrap.ensureRegistered();

        ClientWorldLifecycle.AFTER_WELCOME_SCREEN.subscribe(() -> {
            if (ClientConfigs.CLIENT.forceBetterXPreset.get()) {
                WorldPresetManager.suggestDefault(WorldPresets.WOVER_WORLD, 2000);
            } else {
                WorldPresetManager.suggestDefault(
                        net.minecraft.world.level.levelgen.presets.WorldPresets.NORMAL,
                        10000
                );
            }
        });

        if (ModCore.isClient() && ClientConfigs.CLIENT.forceBetterXPreset.get()) {
            WorldPresetManager.suggestDefault(WorldPresets.WOVER_WORLD, 2000);
        }

        WorldPresetsUI.registerCustomUI(holder -> {
            if (WorldPresetsUI.isKey(holder, WorldPresets.WOVER_WORLD)) {
                return WorldSetupScreen::new;
            }

            for (LevelStem dim : WorldPresetManager.getDimensions(holder).values()) {
                if (dim.generator() instanceof ConfiguredChunkGenerator gen) {
                    if (gen.wover_getConfiguredWorldPreset() != null) {
                        return WorldSetupScreen::new;
                    }
                }
            }

            return null;
        });

        WorldPresetsUI.registerCustomUI(WorldPresets.WOVER_WORLD_AMPLIFIED, WorldSetupScreen::new);
        WorldPresetsUI.registerCustomUI(WorldPresets.WOVER_WORLD_LARGE, WorldSetupScreen::new);
    }
}