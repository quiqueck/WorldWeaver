package org.betterx.wover.events.impl.types.client;

import org.betterx.wover.WoverEventMod;
import org.betterx.wover.events.api.types.client.BeforeClientLoadScreen;
import org.betterx.wover.events.impl.AbstractEvent;
import org.betterx.wover.util.function.VoidFunction;

import net.minecraft.world.level.storage.LevelStorageSource;

public class LoadScreenEventImpl extends AbstractEvent<BeforeClientLoadScreen> {
    public LoadScreenEventImpl(String eventName) {
        super(eventName);
    }

    public final boolean process(LevelStorageSource levelSource, String levelID, VoidFunction callLoadScreen) {
        WoverEventMod.C.LOG.debug("Emitting event: " + eventName);
        for (var subscriber : handlers) {
            if (subscriber.task.process(levelSource, levelID, callLoadScreen))
                return true;
        }

        return false;
    }
}
