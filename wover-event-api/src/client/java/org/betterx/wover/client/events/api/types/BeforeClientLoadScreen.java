package org.betterx.wover.client.events.api.types;

import org.betterx.wover.events.api.types.EventType;
import org.betterx.wover.lang.api.VoidFunction;

import net.minecraft.world.level.storage.LevelStorageSource;

@FunctionalInterface
public interface BeforeClientLoadScreen extends EventType {
    boolean process(LevelStorageSource levelSource, String levelID, VoidFunction callLoadScreen);
}
