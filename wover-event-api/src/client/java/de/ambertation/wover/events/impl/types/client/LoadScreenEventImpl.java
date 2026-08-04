package de.ambertation.wover.events.impl.types.client;

import de.ambertation.wover.entrypoint.LibWoverEvents;
import de.ambertation.wover.events.api.types.client.BeforeClientLoadScreen;
import de.ambertation.wover.events.impl.AbstractEvent;

import net.minecraft.world.level.storage.LevelStorageSource;

public class LoadScreenEventImpl extends AbstractEvent<BeforeClientLoadScreen> {
    public LoadScreenEventImpl(String eventName) {
        super(eventName);
    }

    public final void process(
            LevelStorageSource.LevelStorageAccess levelAccess,
            BeforeClientLoadScreen.ContinueWith finalCall
    ) {
        LibWoverEvents.C.LOG.debug("Emitting event: " + eventName);

        BeforeClientLoadScreen.ContinueWith firstCall = finalCall;
        for (int i = handlers.size() - 1; i >= 0; i--) {
            final BeforeClientLoadScreen.ContinueWith finalFirstCall = firstCall;
            final int finalI = i;
            firstCall = () -> handlers.get(finalI).task.process(levelAccess, finalFirstCall);
        }

        firstCall.loadingScreen();
    }
}
