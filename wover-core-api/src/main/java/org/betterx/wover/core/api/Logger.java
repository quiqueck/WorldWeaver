package org.betterx.wover.core.api;

import org.betterx.wover.config.api.Configs;

import java.util.HashMap;
import java.util.Map;

/**
 * A logger for a mod. This class is a wrapper around {@link de.ambertation.wunderlib.general.Logger} and
 * provides some additional functionality like caching loggers for a given mod id
 */
public final class Logger extends de.ambertation.wunderlib.general.Logger {
    private static final Map<String, Logger> cache = new HashMap<>();

    private Logger(String modID) {
        super(modID);
    }

    static Logger create(String modID) {
        return cache.computeIfAbsent(modID, (id) -> new Logger(id));
    }

    /**
     * Creates a logger for the given mod. If the method is called multiple times with the same mod,
     * the same logger instance is returned.
     *
     * @param mod The mod to create a logger for.
     * @return The logger for the given mod.
     */
    public static Logger create(ModCore mod) {
        return cache.computeIfAbsent(mod.modId, (id) -> new Logger(id));
    }

    /**
     * Log a message with level DEBUG on this logger.
     *
     * @param message the message string to be logged
     */
    public void debug(String message) {
        if (ModCore.isDevEnvironment()) {
            info("(DEBUG) " + message);
        } else {
            super.debug(message);
        }
    }

    /**
     * Log a message with parameters with level DEBUG on this logger.
     *
     * @param message the message string to be logged
     * @param params  the parameters to the message
     */
    public void debug(String message, Object... params) {
        if (ModCore.isDevEnvironment()) {
            info("(DEBUG) " + message, params);
        } else {
            super.debug(message, params);
        }
    }

    /**
     * Log a message with level INFO if the verbose logging option is enabled in {@link Configs#MAIN}
     *
     * @param message the message string to be logged
     */
    public void verbose(String message) {
        if (Configs.MAIN.verboseLogging.get()) {
            info(message);
        }
    }

    /**
     * Log a message with parameters with level INFO if the verbose logging option is enabled in {@link Configs#MAIN}
     *
     * @param message the message string to be logged
     * @param params  the parameters to the message
     */
    public void verbose(String message, Object... params) {
        if (Configs.MAIN.verboseLogging.get()) {
            info(message, params);
        }
    }


    /**
     * Log a message with level WARN if the verbose logging option is enabled in {@link Configs#MAIN}
     *
     * @param message the message string to be logged
     */
    public void verboseWarning(String message) {
        if (Configs.MAIN.verboseLogging.get()) {
            warn(message);
        }
    }

    /**
     * Log a message with parameters with level WARN if the verbose logging option is enabled in {@link Configs#MAIN}
     *
     * @param message the message string to be logged
     * @param params  the parameters to the message
     */
    public void verboseWarning(String message, Object... params) {
        if (Configs.MAIN.verboseLogging.get()) {
            warn(message, params);
        }
    }
}
