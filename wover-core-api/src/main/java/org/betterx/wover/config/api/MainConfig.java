package org.betterx.wover.config.api;

import de.ambertation.wunderlib.configs.ConfigFile;
import org.betterx.wover.entrypoint.WoverCore;

public class MainConfig extends ConfigFile {
    public final static Group GENERAL_GROUP = new Group(WoverCore.C.namespace, "general", 0);
    public final static Group WORLD_LOADING = new Group(WoverCore.C.namespace, "loading", 800);
    public final static Group SERVER_GROUP = new Group(WoverCore.C.namespace, "server", 1000);
    public final static String LOG_CATEGORY = "log";
    public final static String SERVER_CATEGORY = SERVER_GROUP.title();

    public final BooleanValue verboseLogging = new BooleanValue(
            LOG_CATEGORY,
            "verbose",
            true
    ).setGroup(GENERAL_GROUP);

    public final BooleanValue forceDefaultWorldPresetOnServer = new BooleanValue(
            SERVER_CATEGORY,
            "force_default_world_preset",
            true
    ).setGroup(SERVER_GROUP);

    public MainConfig() {
        super(WoverCore.C, "main");
    }
}
