package de.ambertation.wover.core.api;

import net.minecraft.resources.Identifier;

import net.fabricmc.loader.api.FabricLoader;

/**
 * Provides {@link ModCore} instances and presence checks for a handful of mods that WorldWeaver commonly
 * integrates with. Use {@link #hasMod(String)} to check for arbitrary mods, or one of the predefined
 * {@code RUNS_*} constants for the mods that are already known to WorldWeaver.
 */
public class IntegrationCore {
    /**
     * Checks if a mod with the given namespace/id is currently loaded.
     *
     * @param namespace The mod id (namespace) to check for.
     * @return {@code true} if the mod is loaded, {@code false} otherwise.
     */
    public static boolean hasMod(String namespace) {
        return FabricLoader.getInstance()
                           .getModContainer(namespace)
                           .isPresent();
    }

    /** {@code true} if the mod TerraBlender is loaded. */
    public static final boolean RUNS_TERRABLENDER = hasMod("terrablender");
    /** {@code true} if the mod Nullscape is loaded. */
    public static final boolean RUNS_NULLSCAPE = hasMod("nullscape");

    /** The {@link ModCore} instance representing vanilla Minecraft. */
    public static final ModCore MINECRAFT = ModCore.create(Identifier.DEFAULT_NAMESPACE);
    /** The {@link ModCore} instance for BetterEnd. */
    public static final ModCore BETTER_END = ModCore.create("betterend");
    /** The {@link ModCore} instance for BetterNether. */
    public static final ModCore BETTER_NETHER = ModCore.create("betternether");
}
