package org.betterx.wover.events.impl.types.client;

import org.betterx.wover.entrypoint.WoverEvents;
import org.betterx.wover.events.api.types.client.BeforeClientLoadScreen;
import org.betterx.wover.events.impl.AbstractEvent;

import net.minecraft.world.level.storage.LevelStorageSource;

import java.util.function.Consumer;

public class LoadScreenEventImpl extends AbstractEvent<BeforeClientLoadScreen> {
    public LoadScreenEventImpl(String eventName) {
        super(eventName);
    }

    public final boolean process(LevelStorageSource levelSource, String levelID, Consumer<Boolean> callLoadScreen) {
        WoverEvents.C.LOG.debug("Emitting event: " + eventName);
        for (var subscriber : handlers) {
            if (!subscriber.task.process(levelSource, levelID, callLoadScreen))
                return false;
        }

        return true;
    }
}
