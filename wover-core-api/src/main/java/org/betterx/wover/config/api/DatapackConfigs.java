package org.betterx.wover.config.api;

import org.betterx.wover.entrypoint.LibWoverCore;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Reader;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public class DatapackConfigs {
    @FunctionalInterface
    public interface DatapackConfigReloadHandler {
        void onLoad(ResourceLocation id, JsonObject root);
    }

    @FunctionalInterface
    public interface DatapackConfigFinished {
        void whenFinished();
    }

    private static DatapackConfigs INSTANCE = new DatapackConfigs();

    public static DatapackConfigs instance() {
        return INSTANCE;
    }


    public void runForResources(
            ResourceManager manager,
            ResourceLocation fileLocation,
            DatapackConfigReloadHandler handler
    ) {
        runForResources(manager, List.of(fileLocation.getPath()), handler, null);
    }

    /**
     * Checks, weather a resource loaded from the <b>config</b> folder in a datapack
     * of any mod will match the one of the given paths.
     *
     * @param manager  The {@link ResourceManager} to use
     * @param paths    A List of Paths to check
     * @param handler  A function to call for each found resource. The
     *                 {@link ResourceLocation} is the id of the resource and the {@link JsonObject}
     *                 is the root of the stored json file. The namespace of the location identifies the
     *                 mod or datapack that is providing the config file.
     * @param finished A function to call when all resources have been processed.
     */
    public void runForResources(
            ResourceManager manager,
            List<String> paths,
            @Nullable DatapackConfigReloadHandler handler,
            @Nullable DatapackConfigFinished finished
    ) {
        final Map<ResourceLocation, List<Resource>> aSet = manager.listResourceStacks(
                "config",
                id -> {
                    System.out.println(id);
                    return paths.contains(id.getPath());
                }
        );

        if (handler != null) {
            for (Map.Entry<ResourceLocation, List<Resource>> entry : aSet.entrySet()) {
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
