package org.betterx.wover.entrypoint.client;

import org.betterx.wover.config.api.client.ClientConfigs;
import org.betterx.wover.events.api.client.ClientWorldLifecycle;

import net.fabricmc.api.ClientModInitializer;

public class WoverUiClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientWorldLifecycle.BEFORE_CLIENT_LOAD_SCREEN.subscribe((source, id, goOn) -> {
            if (ClientConfigs.CLIENT.disableExperimentalWarning.get()) {
                goOn.accept(false);
                return false;
            }
            return true;
        });

        ClientConfigs.saveConfigs();
    }
}