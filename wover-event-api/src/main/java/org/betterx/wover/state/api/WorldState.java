package org.betterx.wover.state.api;

import org.betterx.wover.entrypoint.LibWoverEvents;
import org.betterx.wover.state.impl.WorldStateImpl;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.LevelStorageSource;

import org.jetbrains.annotations.Nullable;

/**
 * A collection of useful methods to access the current world state.
 * <p>
 * The state is fed by some events, so it is not guaranteed to be available at all times.
 */
public class WorldState {
    /**
     * The current registry access for the world. It is set through the
     * {@link org.betterx.wover.events.api.WorldLifecycle#WORLD_REGISTRY_READY} event.
     * Only Registries that are not sent with the {@link org.betterx.wover.events.api.types.OnRegistryReady.Stage}
     * {@link org.betterx.wover.events.api.types.OnRegistryReady.Stage#PREPARATION}
     * are stored. If you need access to pre-registry registries, use {@link #allStageRegistryAccess()}.
     *
     * @return the current registry access for the world.
     */
    public static RegistryAccess registryAccess() {
        return WorldStateImpl.INSTANCE.getCurrentRegistryAccess();
    }

    /**
     * The latest registry access for the world. It is set through the
     * {@link org.betterx.wover.events.api.WorldLifecycle#WORLD_REGISTRY_READY} event.
     * Compared to {@link #registryAccess()} it also contains registries that are sent with the
     * {@link org.betterx.wover.events.api.types.OnRegistryReady.Stage#PREPARATION}.
     * <p>
     * This is useful if you need access to the registries quite early (for example during Tag-Loading Events).
     * However, in general you should use {@link #registryAccess()}.
     *
     * @return the latest registry access for the world.
     */
    public static RegistryAccess allStageRegistryAccess() {
        return WorldStateImpl.INSTANCE.currentAllStageRegistryAccess;
    }

    /**
     * The current storage access for the world.It is set through the
     * {@link org.betterx.wover.events.api.WorldLifecycle#WORLD_FOLDER_READY} event.
     *
     * @return the current storage access for the world.
     */
    public static LevelStorageSource.LevelStorageAccess storageAccess() {
        return WorldStateImpl.INSTANCE.getCurrentStorageAccess();
    }

    /**
     * Gets the {@link ResourceLocation} of a biome.
     * <p>
     * This method uses #allStageRegistryAccess() to get query the biome registry.
     *
     * @param biome The biome to get the ID for.
     * @return The {@link ResourceLocation} of the biome or null if the biome is null or the registry access is null.
     */
    public static @Nullable ResourceLocation getBiomeID(@Nullable Biome biome) {
        final RegistryAccess access = allStageRegistryAccess();
        final ResourceLocation id = access == null || biome == null
                ? null
                : access.registryOrThrow(Registries.BIOME).getKey(biome);

        if (id == null) {
            LibWoverEvents.C.log.error("Unable to get ResourceLocation for " + biome + ".");
        }

        return id;
    }
}
