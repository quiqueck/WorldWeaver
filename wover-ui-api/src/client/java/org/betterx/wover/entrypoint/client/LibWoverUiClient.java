package org.betterx.wover.entrypoint.client;

import org.betterx.wover.config.api.client.ClientConfigs;
import org.betterx.wover.events.api.client.ClientWorldLifecycle;

import net.fabricmc.api.ClientModInitializer;

public class LibWoverUiClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientWorldLifecycle.ALLOW_EXPERIMENTAL_WARNING_SCREEN.subscribe((show) -> {
            if (ClientConfigs.CLIENT.disableExperimentalWarning.get()) {
                return false;
            }
            return show;
        });

        // ClientWorldLifecycle.ENUMERATE_STARTUP_SCREENS.subscribe(WelcomeScreen::new);

        ClientConfigs.saveConfigs();
    }
}