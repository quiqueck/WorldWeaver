package org.betterx.wover.testmod.entrypoint.client;

import de.ambertation.wunderlib.ui.layout.components.LayoutComponent;
import de.ambertation.wunderlib.ui.layout.components.VerticalStack;
import de.ambertation.wunderlib.ui.vanilla.LayoutScreen;
import org.betterx.wover.preset.api.client.WorldPresetsUI;
import org.betterx.wover.testmod.entrypoint.WoverWorldPresetTestMod;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.network.chat.Component;

import net.fabricmc.api.ClientModInitializer;

public class WoverWorldPresetClientTestMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        WorldPresetsUI.registerCustomUI(WoverWorldPresetTestMod.END_START, new PresetEditor() {
            @Override
            public Screen createEditScreen(
                    CreateWorldScreen createWorldScreen,
                    WorldCreationContext worldCreationContext
            ) {
                return new LayoutScreen(createWorldScreen, Component.literal("End Start")) {

                    @Override
                    protected LayoutComponent<?, ?> initContent() {
                        return new VerticalStack(fill(), fit()).centerHorizontal();
                    }
                };
            }
        });

        WorldPresetsUI.registerCustomUI(WoverWorldPresetTestMod.NETHER_START, new PresetEditor() {
            @Override
            public Screen createEditScreen(
                    CreateWorldScreen createWorldScreen,
                    WorldCreationContext worldCreationContext
            ) {
                return new LayoutScreen(createWorldScreen, Component.literal("Nether Start")) {

                    @Override
                    protected LayoutComponent<?, ?> initContent() {
                        return new VerticalStack(fill(), fit()).centerHorizontal();
                    }
                };
            }
        });
    }
}