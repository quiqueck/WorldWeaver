package org.betterx.wover.config.api.client;

import de.ambertation.wunderlib.configs.ConfigFile;
import org.betterx.wover.config.api.MainConfig;
import org.betterx.wover.entrypoint.LibWoverUi;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Client-only config file backing the options WorldWeaver's UI module presents on the config screen (welcome
 * screen, update-checker) and the world-loading experience.
 * <p>
 * This class is instantiated once and exposed as {@link ClientConfigs#CLIENT}. It is not meant to be instantiated
 * directly.
 */
@Environment(EnvType.CLIENT)
public class ClientConfig extends ConfigFile {
    /** Category used for internal, non-user-facing options (see {@link #didPresentWelcomeScreen}). */
    public final static String INTERNAL_CATEGORY = "internal";
    /** Category used for options related to world loading. */
    public final static String LOADING_CATEGORY = "loading";
    /** Category used for general, uncategorized options. */
    public final static String GENERAL_CATEGORY = "general";

    /**
     * Whether the welcome screen was already shown to the player. Hidden from the UI, this is toggled internally
     * once the player confirms the welcome screen.
     */
    public final BooleanValue didPresentWelcomeScreen = new BooleanValue(
            INTERNAL_CATEGORY,
            "did_present_welcome_screen",
            false
    ).setGroup(MainConfig.GENERAL_GROUP)
     .hideInUI();

    /** Whether {@link org.betterx.wover.ui.api.VersionChecker} should periodically check for mod updates. */
    public final BooleanValue checkForNewVersions = new BooleanValue(
            GENERAL_CATEGORY,
            "check_for_new_versions",
            true
    ).setGroup(MainConfig.GENERAL_GROUP);

    /** Whether Modrinth (instead of CurseForge) should be preferred when both download links are available. */
    public final BooleanValue prefereModrinth = new BooleanValue(
            GENERAL_CATEGORY,
            "prefere_modrinth",
            false
    ).setGroup(MainConfig.GENERAL_GROUP);

    /** Whether the experimental-settings warning screen should be skipped when loading an existing world. */
    public final BooleanValue disableExperimentalWarning = new BooleanValue(
            LOADING_CATEGORY,
            "disable_experimental_warning",
            false
    ).setGroup(MainConfig.WORLD_LOADING);

    /** Whether the BetterX world preset should be forced as the default for newly created worlds. */
    public final BooleanValue forceBetterXPreset = new BooleanValue(
            GENERAL_CATEGORY,
            "force_betterx_world_type",
            true
    ).setGroup(MainConfig.WORLD_LOADING);

    /**
     * Creates the config. This constructor is only called once, use {@link ClientConfigs#CLIENT} to access the
     * instance.
     */
    public ClientConfig() {
        super(LibWoverUi.C, "client");
    }
}
