package org.betterx.wover.config.api.client;

import de.ambertation.wunderlib.configs.ConfigFile;
import org.betterx.wover.config.api.MainConfig;
import org.betterx.wover.entrypoint.WoverUi;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientConfig extends ConfigFile {
    public final static String LOADING_CATEGORY = "loading";

    public final BooleanValue disableExperimentalWarning = new BooleanValue(
            LOADING_CATEGORY,
            "disableExperimentalWarning",
            false
    ).setGroup(MainConfig.GENERAL_GROUP);

    public ClientConfig() {
        super(WoverUi.C, "client");
    }
}
