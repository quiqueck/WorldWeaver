package org.betterx.wover.config.api.client;

import de.ambertation.wunderlib.configs.ConfigFile;
import org.betterx.wover.config.api.MainConfig;
import org.betterx.wover.entrypoint.WoverUi;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientConfig extends ConfigFile {
    public final static String INTERNAL_CATEGORY = "internal";
    public final static String LOADING_CATEGORY = "loading";
    public final static String GENERAL_CATEGORY = "general";

    public final BooleanValue didPresentWelcomeScreen = new BooleanValue(
            INTERNAL_CATEGORY,
            "did_present_welcome_screen",
            false
    ).setGroup(MainConfig.GENERAL_GROUP)
     .hideInUI();

    public final BooleanValue checkForNewVersions = new BooleanValue(
            GENERAL_CATEGORY,
            "check_for_new_versions",
            true
    ).setGroup(MainConfig.GENERAL_GROUP);

    public final BooleanValue disableExperimentalWarning = new BooleanValue(
            LOADING_CATEGORY,
            "disable_experimental_warning",
            false
    ).setGroup(MainConfig.WORLD_LOADING);

    public final BooleanValue forceBetterXPreset = new BooleanValue(
            GENERAL_CATEGORY,
            "force_betterx_world_type",
            true
    ).setGroup(MainConfig.WORLD_LOADING);

    public ClientConfig() {
        super(WoverUi.C, "client");
    }
}
