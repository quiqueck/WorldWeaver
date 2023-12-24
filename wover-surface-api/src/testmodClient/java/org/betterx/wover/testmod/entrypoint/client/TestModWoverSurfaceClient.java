package org.betterx.wover.testmod.entrypoint.client;

import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TestModWoverSurfaceClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CreateWorldScreen s;
    }
}