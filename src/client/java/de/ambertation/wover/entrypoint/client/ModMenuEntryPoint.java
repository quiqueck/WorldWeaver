package de.ambertation.wover.entrypoint.client;

import de.ambertation.wover.screens.impl.MainMenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuEntryPoint implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (parent) -> new MainMenu(parent);
    }
}
