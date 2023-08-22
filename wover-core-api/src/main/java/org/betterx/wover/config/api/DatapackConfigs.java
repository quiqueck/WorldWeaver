package org.betterx.wover.config.api;

import org.betterx.wover.entrypoint.WoverCore;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.Reader;
import java.util.Map;

public class DatapackConfigs {
    @FunctionalInterface
    public interface DatapackConfigReloadHandler {
        void onLoad(ResourceLocation id, JsonObject root);
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
        for (Map.Entry<ResourceLocation, Resource> entry : manager.listResources(
                "config",
                id -> id.getNamespace().equals(fileLocation.getNamespace()) &&
                        id.getPath().endsWith(fileLocation.getPath())
        ).entrySet()) {
            try (Reader reader = entry.getValue().openAsReader()) {
                final JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
                if (obj != null)
                    handler.onLoad(entry.getKey(), obj);
            } catch (Exception e) {
                WoverCore.C.log.error(
                        "Error occurred while loading resource json " + entry.getKey(),
                        e
                );
            }
        }
    }
}
