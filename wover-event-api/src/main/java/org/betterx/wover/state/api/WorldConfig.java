package org.betterx.wover.state.api;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.OnWorldConfig;
import org.betterx.wover.state.impl.WorldConfigImpl;

import net.minecraft.nbt.CompoundTag;

import org.jetbrains.annotations.NotNull;

/**
 * Manages config files that are located in the <b>data</b>-Folder of a world.
 */
public class WorldConfig {
    /**
     * Register mod cache, world cache is located in world data folder.
     *
     * @param modCore The Mod
     */
    public static void registerMod(ModCore modCore) {
        WorldConfigImpl.registerMod(modCore);
    }

    /**
     * This event is fired when the world config is ready to be used.
     * <p>
     * You can subscribe to this Event with methods that adhere to {@link OnWorldConfig}.
     */
    public static Event<OnWorldConfig> event(ModCore modCore) {
        return WorldConfigImpl.event(modCore);
    }

    /**
     * Get root {@link CompoundTag} for mod cache in world data folder.
     *
     * @param modCore - {@link String} modCore.
     * @return {@link CompoundTag}
     */
    public static CompoundTag getRootTag(ModCore modCore) {
        return WorldConfigImpl.getRootTag(modCore);
    }

    /**
     * Get {@link CompoundTag} with specified path from mod cache in world data folder.
     *
     * @param modCore The Mod
     * @param path    path to tag, dot-separated.
     * @return {@link CompoundTag}
     */
    public static @NotNull CompoundTag getCompoundTag(ModCore modCore, String path) {
        return WorldConfigImpl.getCompoundTag(modCore, path);
    }

    /**
     * Forces mod cache file to be saved.
     *
     * @param modCore The Mod
     */
    public static void saveFile(ModCore modCore) {
        WorldConfigImpl.saveFile(modCore);
    }

    /**
     * Check if the mod is registered for a worldconfig file
     *
     * @param modCore The Mod
     * @return true if the mod is registered.
     */
    public static boolean hasMod(ModCore modCore) {
        return WorldConfigImpl.hasMod(modCore);
    }
}