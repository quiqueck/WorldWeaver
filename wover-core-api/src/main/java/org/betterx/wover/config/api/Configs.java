package org.betterx.wover.config.api;

public class Configs {
    public static final MainConfig MAIN = new MainConfig();

    public static void saveConfigs() {
        MAIN.save();
    }
}
