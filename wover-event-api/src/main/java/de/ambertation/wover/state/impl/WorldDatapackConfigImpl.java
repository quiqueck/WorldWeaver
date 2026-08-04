package de.ambertation.wover.state.impl;

import de.ambertation.wunderlib.configs.ConfigResource;
import de.ambertation.wover.config.api.DatapackConfigs;
import de.ambertation.wover.events.api.Event;
import de.ambertation.wover.events.api.WorldLifecycle;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.flag.FeatureFlagSet;

import com.google.gson.JsonObject;
import static de.ambertation.wover.events.impl.AbstractEvent.SYSTEM_PRIORITY;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;

/**
 * Special fields:
 * <ul>
 * <li><code>merge</code></li>
 * <li><code>priority</code>: higher=earlier load, by default 1000 except for the config from the original mod's datapack
 * that will have 2000</li>
 * </ul>
 */
public class WorldDatapackConfigImpl {
    private static final Map<String, ConfigResource> CONFIGS = new HashMap<>();

    public static void registerConfig(ConfigResource config) {
        CONFIGS.put(config.location.getPath(), config);
    }

    @ApiStatus.Internal
    public static void initialize() {
        WorldLifecycle.BEFORE_LOADING_RESOURCES.subscribe(WorldDatapackConfigImpl::onResourcesLoaded, SYSTEM_PRIORITY);
        WorldLifecycle.RESOURCES_LOADED.subscribe(WorldDatapackConfigImpl::onResourcesLoaded, SYSTEM_PRIORITY);
    }

    private static void onResourcesLoaded(ResourceManager resourceManager, FeatureFlagSet featureFlagSet) {
        ConfigResource.invalidateCache();
    }

    private static void onResourcesLoaded(ResourceManager resourceManager) {
        //reset all configs to null
        CONFIGS.values().forEach(v -> v.setRootElement((JsonObject) null));

        //load new content
        final List<String> paths = CONFIGS.keySet().stream().toList();
        WorldDatapackConfigImpl obj = new WorldDatapackConfigImpl();
        DatapackConfigs
                .instance()
                .runForConfigPaths(resourceManager, paths, obj::processBiomeConfigs, obj::whenFinished);
    }

    final List<LoadedItem> resources = new LinkedList<>();

    private record LoadedItem(
            ResourceLocation resource,
            JsonObject object,
            ConfigResource cfg,
            int initialPriority,
            boolean defaultPriority
    ) {
        public boolean isMaster() {
            return resource.getNamespace().equals(cfg.location.getNamespace());
        }

        public int priority() {
            if (defaultPriority && isMaster())
                return initialPriority * 2;
            return initialPriority;
        }

        @Override
        public String toString() {
            return resource + " -> " + priority();
        }
    }

    private void processBiomeConfigs(ResourceLocation resourceLocation, JsonObject jsonObject) {
        int priority = Event.DEFAULT_PRIORITY;
        boolean defaultPriority = true;
        if (jsonObject.has("priority") && jsonObject.get("priority").isJsonPrimitive() && jsonObject
                .getAsJsonPrimitive("priority")
                .isNumber()) {
            defaultPriority = false;
            priority = jsonObject.getAsJsonPrimitive("priority").getAsInt();
        }
        final ConfigResource config = CONFIGS.get(resourceLocation.getPath());

        if (config != null) {
            resources.add(new LoadedItem(resourceLocation, jsonObject, config, priority, defaultPriority));
        }

    }

    private void whenFinished() {
        this.resources.sort((a, b) -> {
            if (a.priority() == b.priority()) {
                return a.resource.compareTo(b.resource);
            }
            return a.priority() > b.priority() ? -1 : 1;
        });

        this.resources.forEach(v -> {
            v.cfg.setRootElement(v.object);
        });
    }
}
