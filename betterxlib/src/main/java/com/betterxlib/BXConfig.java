package com.betterxlib;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * BetterXLib configuration settings.
 */
public class BXConfig {
    public static final ModConfigSpec SPEC;

    // Rendering settings
    public static final ModConfigSpec.BooleanValue ENABLE_EMISSIVE_TEXTURES;
    public static final ModConfigSpec.BooleanValue ENABLE_CUSTOM_FOG;

    // Debug settings
    public static final ModConfigSpec.BooleanValue DEBUG_MODE;
    public static final ModConfigSpec.BooleanValue LOG_BIOME_REGISTRATION;
    public static final ModConfigSpec.BooleanValue LOG_FEATURE_REGISTRATION;

    // Performance settings
    public static final ModConfigSpec.IntValue BIOME_CACHE_SIZE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("BetterXLib Configuration");
        builder.push("rendering");

        ENABLE_EMISSIVE_TEXTURES = builder
            .comment("Enable emissive texture rendering for blocks with _e suffix textures")
            .define("enableEmissiveTextures", true);

        ENABLE_CUSTOM_FOG = builder
            .comment("Enable custom fog rendering per biome")
            .define("enableCustomFog", true);

        builder.pop();

        builder.push("debug");

        DEBUG_MODE = builder
            .comment("Enable debug mode for additional logging")
            .define("debugMode", false);

        LOG_BIOME_REGISTRATION = builder
            .comment("Log biome registration events")
            .define("logBiomeRegistration", false);

        LOG_FEATURE_REGISTRATION = builder
            .comment("Log feature registration events")
            .define("logFeatureRegistration", false);

        builder.pop();

        builder.push("performance");

        BIOME_CACHE_SIZE = builder
            .comment("Size of the biome picker cache")
            .defineInRange("biomeCacheSize", 1024, 64, 16384);

        builder.pop();

        SPEC = builder.build();
    }
}
