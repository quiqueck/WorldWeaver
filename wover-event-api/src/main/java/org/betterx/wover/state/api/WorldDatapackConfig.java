package org.betterx.wover.state.api;

import de.ambertation.wunderlib.configs.ConfigResource;
import org.betterx.wover.state.impl.WorldDatapackConfigImpl;

/**
 * Registers {@link ConfigResource}s that are backed by datapack JSON files.
 * <p>
 * A registered {@link ConfigResource} is (re-)populated automatically whenever the world's resources are
 * (re-)loaded, i.e. on {@link org.betterx.wover.events.api.WorldLifecycle#BEFORE_LOADING_RESOURCES} and
 * {@link org.betterx.wover.events.api.WorldLifecycle#RESOURCES_LOADED}. Every JSON file across all loaded datapacks
 * whose path matches the resource's {@code location} is collected, sorted by an optional {@code priority} field
 * (higher priority loads earlier; the config from the resource's own namespace defaults to twice
 * {@link org.betterx.wover.events.api.Event#DEFAULT_PRIORITY} unless it specifies its own {@code priority}) and
 * merged into the resource in that order.
 */
public class WorldDatapackConfig {
    /**
     * Registers a {@link ConfigResource} so its content gets loaded from/merged with matching datapack JSON files.
     *
     * @param config the config resource to register.
     */
    public static void registerConfig(ConfigResource config) {
        WorldDatapackConfigImpl.registerConfig(config);
    }
}
