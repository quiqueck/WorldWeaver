package org.betterx.wover.config.api;

import de.ambertation.wunderlib.configs.ConfigFile;
import org.betterx.wover.entrypoint.LibWoverCore;

/**
 * WorldWeaver's own main config file.
 * <p>
 * This class is instantiated once and exposed as {@link Configs#MAIN}. It is not meant to be instantiated
 * directly. The predefined {@code *_GROUP} constants are the groups that WorldWeaver's own config values are
 * sorted into on the config screen; other WorldWeaver modules add their values to these groups as well.
 */
public class MainConfig extends ConfigFile {
    /** Group for general, uncategorized options. */
    public final static Group GENERAL_GROUP = new Group(LibWoverCore.C.namespace, "general", 0);
    /** Group for options related to world loading. */
    public final static Group WORLD_LOADING = new Group(LibWoverCore.C.namespace, "loading", 800);
    /** Group for options related to dedicated servers. */
    public final static Group SERVER_GROUP = new Group(LibWoverCore.C.namespace, "server", 1000);
    /** Group for options related to structures. */
    public final static Group STRUCTURE_GROUP = new Group(LibWoverCore.C.namespace, "structure", 2000);
    /** Group for options related to entities. */
    public final static Group ENTITY_GROUP = new Group(LibWoverCore.C.namespace, "entity", 2500);
    /** Group for options related to performance. */
    public final static Group PERFORMANCE_GROUP = new Group(LibWoverCore.C.namespace, "performance", 3000);
    /** Group for cosmetic options. */
    public final static Group COSMETIC_GROUP = new Group(LibWoverCore.C.namespace, "cosmetic", 4000);
    /** Group for options related to the user interface. */
    public final static Group UI_GROUP = new Group(LibWoverCore.C.namespace, "ui", 4300);
    /** Group for options related to rendering. */
    public final static Group RENDERING_GROUP = new Group(LibWoverCore.C.namespace, "rendering", 4600);


    /** Category used for logging related options. */
    public final static String LOG_CATEGORY = "log";
    /** Category used for server related options, mirrors the title of {@link #SERVER_GROUP}. */
    public final static String SERVER_CATEGORY = SERVER_GROUP.title();

    /**
     * If enabled, {@link org.betterx.wover.core.api.Logger#verbose}, {@link org.betterx.wover.core.api.Logger#verboseWarning}
     * and {@link org.betterx.wover.core.api.Logger#verboseError} write their messages to the log.
     */
    public final BooleanValue verboseLogging = new BooleanValue(
            LOG_CATEGORY,
            "verbose",
            true
    ).setGroup(GENERAL_GROUP);

    /**
     * If enabled, dedicated servers are forced to use the default world preset instead of whatever preset was
     * selected in the server properties.
     */
    public final BooleanValue forceDefaultWorldPresetOnServer = new BooleanValue(
            SERVER_CATEGORY,
            "force_default_world_preset",
            true
    ).setGroup(SERVER_GROUP);

    /**
     * Creates the config. This constructor is only called once, use {@link Configs#MAIN} to access the instance.
     */
    public MainConfig() {
        super(LibWoverCore.C, "main");
    }
}
