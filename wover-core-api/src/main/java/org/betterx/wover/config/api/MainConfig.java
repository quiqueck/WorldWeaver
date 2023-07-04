package org.betterx.wover.config.api;

import de.ambertation.wunderlib.configs.ConfigFile;
import org.betterx.wover.entrypoint.WoverCore;

public class MainConfig extends ConfigFile {
    public final static Group GENERAL_GROUP = new Group(WoverCore.C.namespace, "general", 0);
    public final static String LOG_CATEGORY = "log";

    public final BooleanValue verboseLogging = new BooleanValue(
            LOG_CATEGORY,
            "verbose",
            true
    ).setGroup(GENERAL_GROUP);

    public MainConfig() {
        super(WoverCore.C, "main");
    }
}
