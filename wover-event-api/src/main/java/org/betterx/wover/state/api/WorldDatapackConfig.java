package org.betterx.wover.state.api;

import de.ambertation.wunderlib.configs.ConfigResource;
import org.betterx.wover.state.impl.WorldDatapackConfigImpl;

public class WorldDatapackConfig {
    public static void registerConfig(ConfigResource config) {
        WorldDatapackConfigImpl.registerConfig(config);
    }
}
