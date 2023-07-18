package org.betterx.wover.state.api;

import org.betterx.wover.entrypoint.WoverEvents;
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
     * The current registry access for the world.It is set through the
     * {@link org.betterx.wover.events.api.WorldLifecycle#WORLD_REGISTRY_READY} event.
     * Only Registries that are not sent with the {@link org.betterx.wover.events.api.types.OnRegistryReady.Stage}
     * {@link org.betterx.wover.events.api.types.OnRegistryReady.Stage#PREPARATION}
     * are stored.
     *
     * @return the current registry access for the world.
     */
    public static RegistryAccess registryAccess() {
        return WorldStateImpl.INSTANCE.getCurrentRegistryAccess();
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

    public static @Nullable ResourceLocation getBiomeID(@Nullable Biome biome) {
        final RegistryAccess access = registryAccess();
        final ResourceLocation id = access == null || biome == null
                ? null
                : access.registryOrThrow(Registries.BIOME).getKey(biome);

        if (id == null) {
            WoverEvents.C.log.error("Unable to get ID for " + biome + ".");
        }

        return id;
    }
}
