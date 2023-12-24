package org.betterx.wover.testmod.entrypoint.client;

import org.betterx.wover.events.api.client.ClientWorldLifecycle;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TestModWoverEventClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientWorldLifecycle.BEFORE_CLIENT_LOAD_SCREEN.subscribe((levelStorageAccess, continueWith) -> {
            System.out.println("Before client load screen: \n - " + levelStorageAccess + "\n - " + continueWith);
            continueWith.loadingScreen();
        });

        ClientWorldLifecycle.BEFORE_CLIENT_LOAD_SCREEN.subscribe((levelStorageAccess, continueWith) -> {
            System.out.println("Before client load screen II: \n - " + levelStorageAccess + "\n - " + continueWith);
            continueWith.loadingScreen();
        });

        ClientWorldLifecycle.BEFORE_CLIENT_LOAD_SCREEN.subscribe((levelStorageAccess, continueWith) -> {
            System.out.println("Before client load screen III: \n - " + levelStorageAccess + "\n - " + continueWith);
            continueWith.loadingScreen();
        });

        ClientWorldLifecycle.ALLOW_EXPERIMENTAL_WARNING_SCREEN.subscribe((bl) -> {
            System.out.println("Allow experimental warning screen: " + bl);
            return false;
        });
    }
}
