package org.betterx.wover.events.impl.types;

import org.betterx.wover.entrypoint.LibWoverEvents;
import org.betterx.wover.events.api.types.OnFolderReady;
import org.betterx.wover.events.impl.AbstractEvent;
import org.betterx.wover.state.api.WorldState;

import net.minecraft.world.level.storage.LevelStorageSource;

public class FolderReadyEventImpl extends AbstractEvent<OnFolderReady> {
    public FolderReadyEventImpl(String eventName) {
        super(eventName);
    }

    public void emit(LevelStorageSource.LevelStorageAccess value) {
        if (value != WorldState.storageAccess()) {
            LibWoverEvents.C.LOG.debug("Emitting event: "
                    + eventName
                    + " ("
                    + Integer.toHexString(value.hashCode())
                    + ", "
                    + value.getLevelId()
                    + ")"
            );

            handlers.forEach(c -> c.task.ready(value));
        }
    }

}
