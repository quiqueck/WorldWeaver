package org.betterx.wover.entrypoint.client;

import org.betterx.wover.config.api.client.ClientConfigs;
import org.betterx.wover.events.api.client.ClientWorldLifecycle;
import org.betterx.wover.generator.api.preset.WorldPresets;
import org.betterx.wover.generator.impl.chunkgenerator.ConfiguredChunkGenerator;
import org.betterx.wover.generator.impl.client.WorldSetupScreen;
import org.betterx.wover.preset.api.WorldPresetManager;
import org.betterx.wover.preset.api.client.WorldPresetsUI;

import net.minecraft.world.level.dimension.LevelStem;

import net.fabricmc.api.ClientModInitializer;

public class LibWoverWorldGeneratorClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
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

        if (ClientConfigs.CLIENT.forceBetterXPreset.get()) {
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