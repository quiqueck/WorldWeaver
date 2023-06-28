package org.betterx.wover.client.events.impl;

import net.fabricmc.api.ClientModInitializer;

public class WoverEventsModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("Loaded Tag API [client]");
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
    }
}