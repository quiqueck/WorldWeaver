package org.betterx.wover.client.events.impl.types;

import org.betterx.wover.WoverEventMod;
import org.betterx.wover.client.events.api.types.BeforeClientLoadScreen;
import org.betterx.wover.events.impl.AbstractEvent;
import org.betterx.wover.lang.api.VoidFunction;

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
