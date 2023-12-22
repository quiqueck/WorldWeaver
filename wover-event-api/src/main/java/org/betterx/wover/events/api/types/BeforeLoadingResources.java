package org.betterx.wover.events.api.types;

import org.betterx.wover.events.api.Subscriber;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.flag.FeatureFlagSet;

@FunctionalInterface
public interface BeforeLoadingResources extends Subscriber {
    void didLoad(ResourceManager resourceManager, FeatureFlagSet featureFlagSet);
}
