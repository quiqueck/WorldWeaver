package org.betterx.wover.testmod.entrypoint.client;

import org.betterx.wover.events.api.client.ClientWorldLifecycle;

import net.fabricmc.api.ClientModInitializer;

public class WoverEventTestModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientWorldLifecycle.BEFORE_CLIENT_LOAD_SCREEN.subscribe((levelSource, levelID, callLoadScreen) -> {
            System.out.println("Before client load screen: \n - " + levelSource + "\n - " + levelID + "\n - " + callLoadScreen);
            callLoadScreen.accept(false);
            return false;
        });
    }
}
