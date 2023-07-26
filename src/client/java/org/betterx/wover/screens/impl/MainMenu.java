package org.betterx.wover.screens.impl;

import de.ambertation.wunderlib.ui.vanilla.ConfigScreen;
import org.betterx.wover.config.api.Configs;
import org.betterx.wover.config.api.client.ClientConfigs;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class MainMenu extends ConfigScreen {
    public MainMenu(
            @Nullable Screen parent
    ) {
        super(parent, Component.translatable("wover.mainmenu.title"), List.of(ClientConfigs.CLIENT, Configs.MAIN));
    }

    @Override
    public void onClose() {
        super.onClose();
        Configs.saveConfigs();
        ClientConfigs.saveConfigs();
    }
}
