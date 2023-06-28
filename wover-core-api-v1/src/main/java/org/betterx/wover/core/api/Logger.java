package org.betterx.wover.core.api;

import java.util.HashMap;
import java.util.Map;


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
        return cache.computeIfAbsent(mod.MOD_ID, (id) -> new Logger(id));
    }
}
