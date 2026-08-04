package de.ambertation.wover.entrypoint.client;

import de.ambertation.wover.config.api.client.ClientConfigs;
import de.ambertation.wover.events.api.client.ClientWorldLifecycle;
import de.ambertation.wover.ui.impl.client.VersionCheckerClient;

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

        ClientWorldLifecycle.ENUMERATE_STARTUP_SCREENS.subscribe(VersionCheckerClient::presentUpdateScreen);

        ClientConfigs.saveConfigs();
    }
}