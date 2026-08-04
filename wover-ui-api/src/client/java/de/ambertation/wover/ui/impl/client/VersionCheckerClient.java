package de.ambertation.wover.ui.impl.client;

import de.ambertation.wover.config.api.client.ClientConfigs;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.ui.api.VersionChecker;

import net.minecraft.client.gui.screens.Screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class VersionCheckerClient extends VersionChecker {
    public static void presentUpdateScreen(List<Function<Runnable, Screen>> screens) {
        VersionChecker.startCheck(
                ModCore.isClient()
                        && ClientConfigs.CLIENT.checkForNewVersions.get()
                        && ClientConfigs.CLIENT.didPresentWelcomeScreen.get()
        );

        if (!ClientConfigs.CLIENT.didPresentWelcomeScreen.get()) {
            screens.add(WelcomeScreen::new);
        } else if (ClientConfigs.CLIENT.checkForNewVersions.get() && !VersionChecker.isEmpty()) {
            screens.add(UpdatesScreen::new);
        }
    }
}
