package org.betterx.wover.entrypoint.client;

import org.betterx.wover.config.api.client.ClientConfigs;
import org.betterx.wover.events.api.client.ClientWorldLifecycle;
import org.betterx.wover.generator.api.preset.PresetsRegistry;
import org.betterx.wover.generator.impl.client.WorldSetupScreen;
import org.betterx.wover.preset.api.WorldPresetManager;
import org.betterx.wover.preset.api.client.WorldPresetsUI;

import net.fabricmc.api.ClientModInitializer;

public class WoverWorldGeneratorClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientWorldLifecycle.AFTER_WELCOME_SCREEN.subscribe(() -> {
            if (ClientConfigs.CLIENT.forceBetterXPreset.get()) {
                WorldPresetManager.suggestDefault(PresetsRegistry.WOVER_WORLD, 2000);
            } else {
                WorldPresetManager.suggestDefault(
                        net.minecraft.world.level.levelgen.presets.WorldPresets.NORMAL,
                        10000
                );
            }
        });

        if (ClientConfigs.CLIENT.forceBetterXPreset.get()) {
            WorldPresetManager.suggestDefault(PresetsRegistry.WOVER_WORLD, 2000);
        }

        WorldPresetsUI.registerCustomUI(PresetsRegistry.WOVER_WORLD, WorldSetupScreen::new);
    }
}