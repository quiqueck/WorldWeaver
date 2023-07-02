package org.betterx.wover.testmod.entrypoint;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.events.api.WorldLifecycle;

import net.fabricmc.api.ModInitializer;

public class WoverEventTestMod implements ModInitializer {
    public static final ModCore C = ModCore.create("event-test-mod");

    @Override
    public void onInitialize() {
        WorldLifecycle.WORLD_FOLDER_READY.subscribe((access) -> {
            C.LOG.info("World folder ready (prio 10): {}", access);
        }, 10);

        WorldLifecycle.WORLD_FOLDER_READY.subscribe((access) -> {
            C.LOG.info("World folder ready (prio 2000):\n - {}", access);
        }, 2000);

        WorldLifecycle.WORLD_FOLDER_READY.subscribe((access) -> {
            C.LOG.info("World folder ready (prio default):\n - {}", access);
        });

        WorldLifecycle.WORLD_REGISTRY_READY.subscribe((registry, stage) -> {
            C.LOG.info("World registry ready:\n - {}\n - {}", registry, stage);
        });

        WorldLifecycle.CREATED_NEW_WORLD_FOLDER.subscribe((storageAccess, registryAccess, preset, dimensions, recreated) -> {
            C.LOG.info(
                    "Created new world folder:\n - {}\n - {}\n - {}\n - {}\n - {}",
                    storageAccess,
                    registryAccess,
                    preset,
                    dimensions,
                    recreated
            );
        });

        WorldLifecycle.BEFORE_CREATING_LEVELS.subscribe(((storageAccess, packRepository, registries, worldData) -> {
            C.LOG.info(
                    "Before creating levels: \n - {}\n - {}\n - {}\n - {}\n",
                    storageAccess,
                    packRepository,
                    registries,
                    worldData
            );
        }));

        WorldLifecycle.ON_DIMENSION_LOAD.subscribe(input -> {
            C.LOG.info("Dimension load: \n - {}", input);
            return input;
        });

        WorldLifecycle.MINECRAFT_SERVER_READY.subscribe((storageSource, packRepository, worldStem) -> {
            C.LOG.info("Minecraft server ready: \n - {}\n - {}\n - {}", storageSource, packRepository, worldStem);
        });
    }
}
