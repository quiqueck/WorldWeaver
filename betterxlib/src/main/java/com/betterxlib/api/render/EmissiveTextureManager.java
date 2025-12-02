package com.betterxlib.api.render;

import com.betterxlib.BetterXLib;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages emissive texture discovery and caching.
 * <p>
 * Emissive textures are textures that should be rendered with full brightness,
 * making them appear to glow. They are identified by the "_e" suffix in their filename.
 * <p>
 * For example, if a block has texture "mymod:block/crystal", the emissive variant
 * would be "mymod:block/crystal_e".
 */
public final class EmissiveTextureManager {
    private static final String EMISSIVE_SUFFIX = "_e";
    private static final Set<ResourceLocation> EMISSIVE_TEXTURES = ConcurrentHashMap.newKeySet();
    private static final Set<ResourceLocation> CHECKED_TEXTURES = ConcurrentHashMap.newKeySet();
    private static boolean initialized = false;

    private EmissiveTextureManager() {}

    /**
     * Check if emissive textures are enabled in config.
     *
     * @return true if emissive textures should be rendered
     */
    public static boolean isEnabled() {
        return com.betterxlib.BXConfig.ENABLE_EMISSIVE_TEXTURES.get();
    }

    /**
     * Initialize the emissive texture manager.
     * Called during client setup.
     */
    public static void init() {
        if (initialized) return;
        initialized = true;
        BetterXLib.LOGGER.debug("EmissiveTextureManager initialized");
    }

    /**
     * Clear all cached emissive texture data.
     * Called on resource reload.
     */
    public static void clear() {
        EMISSIVE_TEXTURES.clear();
        CHECKED_TEXTURES.clear();
        BetterXLib.LOGGER.debug("EmissiveTextureManager cache cleared");
    }

    /**
     * Get the emissive variant of a texture location.
     *
     * @param original the original texture location
     * @return the emissive texture location (with _e suffix)
     */
    public static ResourceLocation getEmissiveTexture(ResourceLocation original) {
        String path = original.getPath();
        // Remove .png extension if present
        if (path.endsWith(".png")) {
            path = path.substring(0, path.length() - 4);
        }
        return ResourceLocation.fromNamespaceAndPath(original.getNamespace(), path + EMISSIVE_SUFFIX);
    }

    /**
     * Check if a texture has an emissive variant.
     *
     * @param texture the texture location
     * @return true if an emissive variant exists
     */
    public static boolean hasEmissiveTexture(ResourceLocation texture) {
        if (!isEnabled()) return false;

        // Check cache
        if (CHECKED_TEXTURES.contains(texture)) {
            return EMISSIVE_TEXTURES.contains(texture);
        }

        // Check if emissive texture exists
        ResourceLocation emissive = getEmissiveTexture(texture);
        boolean exists = checkTextureExists(emissive);

        CHECKED_TEXTURES.add(texture);
        if (exists) {
            EMISSIVE_TEXTURES.add(texture);
        }

        return exists;
    }

    /**
     * Register a texture as having an emissive variant.
     * Use this if you know a texture has an emissive variant without checking.
     *
     * @param texture the texture location
     */
    public static void registerEmissiveTexture(ResourceLocation texture) {
        EMISSIVE_TEXTURES.add(texture);
        CHECKED_TEXTURES.add(texture);
    }

    /**
     * Get all known emissive textures.
     *
     * @return set of texture locations with emissive variants
     */
    public static Set<ResourceLocation> getEmissiveTextures() {
        return new HashSet<>(EMISSIVE_TEXTURES);
    }

    private static boolean checkTextureExists(ResourceLocation texture) {
        try {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft == null) return false;

            ResourceManager resourceManager = minecraft.getResourceManager();
            if (resourceManager == null) return false;

            // Build the full texture path
            ResourceLocation fullPath = ResourceLocation.fromNamespaceAndPath(
                texture.getNamespace(),
                "textures/" + texture.getPath() + ".png"
            );

            return resourceManager.getResource(fullPath).isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if a texture path ends with the emissive suffix.
     *
     * @param path the texture path
     * @return true if this is an emissive texture
     */
    public static boolean isEmissiveTexturePath(String path) {
        return path.endsWith(EMISSIVE_SUFFIX) || path.endsWith(EMISSIVE_SUFFIX + ".png");
    }
}
