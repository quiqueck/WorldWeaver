package org.betterx.wover.config.api.client;

import org.betterx.wover.config.api.Configs;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Entry point for accessing WorldWeaver UI's client-only {@link ClientConfig}.
 */
@Environment(EnvType.CLIENT)
public class ClientConfigs {
    /** The singleton {@link ClientConfig} instance, registered with {@link Configs}. */
    public static final ClientConfig CLIENT = Configs.register(ClientConfig::new);

    /**
     * Persists every registered config (not just {@link #CLIENT}) to disk.
     * <p>
     * This is a convenience wrapper around {@link Configs#saveConfigs()}.
     */
    public static void saveConfigs() {
        Configs.saveConfigs();
    }
}
