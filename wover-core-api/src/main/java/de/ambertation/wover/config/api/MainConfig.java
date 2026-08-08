package de.ambertation.wover.config.api;

import de.ambertation.wunderlib.configs.ConfigFile;
import de.ambertation.wover.entrypoint.LibWoverCore;

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
     * If enabled, {@link de.ambertation.wover.core.api.Logger#verbose}, {@link de.ambertation.wover.core.api.Logger#verboseWarning}
     * and {@link de.ambertation.wover.core.api.Logger#verboseError} write their messages to the log.
     */
    public final BooleanValue verboseLogging = new BooleanValue(
            LOG_CATEGORY,
            "verbose",
            true
    ).setGroup(GENERAL_GROUP);

    // Removed 2026-08-04: "server.force_default_world_preset". It made dedicated servers ignore
    // "level-type" and always generate with our default preset. A server that never configured a
    // level-type already gets our preset written into its server.properties, so the only thing the
    // option could still do was discard a choice somebody made on purpose. Servers that have booted
    // an older build keep the now-unread key in their config file; it is harmless.

    /**
     * Creates the config. This constructor is only called once, use {@link Configs#MAIN} to access the instance.
     */
    public MainConfig() {
        super(LibWoverCore.C, "main");
    }
}
