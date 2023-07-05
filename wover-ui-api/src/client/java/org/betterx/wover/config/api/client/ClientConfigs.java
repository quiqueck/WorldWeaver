package org.betterx.wover.config.api.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientConfigs {
    public static final ClientConfig CLIENT = new ClientConfig();

    public static void saveConfigs() {
        CLIENT.save();
    }
}
