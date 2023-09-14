package org.betterx.wover.config.api.client;

import org.betterx.wover.config.api.Configs;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientConfigs {
    public static final ClientConfig CLIENT = Configs.register(ClientConfig::new);

    public static void saveConfigs() {
        Configs.saveConfigs();
    }
}
