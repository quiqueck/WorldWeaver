package de.ambertation.wover.config.api;

import de.ambertation.wover.entrypoint.LibWoverCore;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Reader;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 * Loads JSON config files that mods and datapacks place under a {@code config/} folder in their resources.
 * <p>
 * Unlike {@link Configs}/{@link de.ambertation.wunderlib.configs.AbstractConfig}, which stores locally editable
 * config values, this class is meant for reading configuration data that was shipped inside a datapack or resource
 * pack, so it can react to reloads and merge values contributed by several packs. Use {@link #instance()} to get
 * the singleton instance and then call {@link #runForConfigPaths} (or {@link #runForResource}) whenever you need to
 * (re-)load such files, for example from a resource reload listener.
 */
public class DatapackConfigs {
    /**
     * Called once for every resource that was found while loading a config file.
     */
    @FunctionalInterface
    public interface DatapackConfigReloadHandler {
        /**
         * Called for every loaded resource.
         *
         * @param id   The id of the resource. The namespace of the id identifies the mod or datapack that provided
         *             the resource.
         * @param root The root {@link JsonObject} of the loaded file.
         */
        void onLoad(Identifier id, JsonObject root);
    }

    /**
     * Called once after all matching resources were processed by a {@link DatapackConfigReloadHandler}.
     */
    @FunctionalInterface
    public interface DatapackConfigFinished {
        /**
         * Called after all matching resources were processed.
         */
        void whenFinished();
    }

    private static DatapackConfigs INSTANCE = new DatapackConfigs();

    /**
     * Returns the singleton instance of this class.
     *
     * @return The singleton instance.
     */
    public static DatapackConfigs instance() {
        return INSTANCE;
    }


    /**
     * Loads all resources from the {@code config} folder of any mod or datapack whose path matches
     * {@code config/<path of fileLocation>} in the same namespace as {@code fileLocation}.
     *
     * @param manager      The {@link ResourceManager} to use.
     * @param fileLocation The location whose namespace and path identify the config file to load.
     * @param handler      A function that is called for each found resource.
     */
    public void runForResource(
            ResourceManager manager,
            Identifier fileLocation,
            DatapackConfigReloadHandler handler
    ) {
        final Map<Identifier, List<Resource>> aSet = manager.listResourceStacks(
                "config",
                id -> {
                    LibWoverCore.C.log.debug("Checking Resource from Datapack: '{}'", id);
                    return fileLocation.getNamespace().equals(id.getNamespace()) && id
                            .getPath()
                            .equals("config/" + fileLocation.getPath());
                }
        );

        runForSet(handler, null, aSet);
    }

    /**
     * Checks, weather a resource loaded from the <b>config</b> folder in a datapack
     * of any mod will match the one of the given paths.
     *
     * @param manager  The {@link ResourceManager} to use
     * @param paths    A List of Paths to check
     * @param handler  A function to call for each found resource. The
     *                 {@link Identifier} is the id of the resource and the {@link JsonObject}
     *                 is the root of the stored json file. The namespace of the location identifies the
     *                 mod or datapack that is providing the config file.
     * @param finished A function to call when all resources have been processed.
     */
    public void runForConfigPaths(
            ResourceManager manager,
            List<String> paths,
            @Nullable DatapackConfigReloadHandler handler,
            @Nullable DatapackConfigFinished finished
    ) {
        final Map<Identifier, List<Resource>> aSet = manager.listResourceStacks(
                "config",
                id -> {
                    LibWoverCore.C.log.debug("Checking Resource from Datapack: '{}'", id);
                    return paths.contains(id.getPath());
                }
        );

        runForSet(handler, finished, aSet);
    }

    private static void runForSet(
            @Nullable DatapackConfigReloadHandler handler,
            @Nullable DatapackConfigFinished finished,
            Map<Identifier, List<Resource>> resources
    ) {
        if (handler != null) {
            for (Map.Entry<Identifier, List<Resource>> entry : resources.entrySet()) {
                for (Resource item : entry.getValue()) {
                    try (Reader reader = item.openAsReader()) {
                        final JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
                        if (obj != null)
                            handler.onLoad(entry.getKey(), obj);
                    } catch (Exception e) {
                        LibWoverCore.C.log.error(
                                "Error occurred while loading resource json " + entry.getKey(),
                                e
                        );
                    }
                }
            }
        }

        if (finished != null) {
            finished.whenFinished();
        }
    }
}
