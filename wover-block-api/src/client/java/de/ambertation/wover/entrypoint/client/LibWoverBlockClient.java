package de.ambertation.wover.entrypoint.client;

import de.ambertation.wover.client.api.ClientTraitBootstrap;
import de.ambertation.wover.client.impl.ClientRenderTraitRegistry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class LibWoverBlockClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Collect every mod's wover.client.traits registrations, then apply the render appliers (idempotent).
        ClientTraitBootstrap.ensureRegistered();
        ClientRenderTraitRegistry.applyAll();
    }
}
