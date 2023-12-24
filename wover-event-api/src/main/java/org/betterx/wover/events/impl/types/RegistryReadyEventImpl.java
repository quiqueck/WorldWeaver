package org.betterx.wover.events.impl.types;

import org.betterx.wover.entrypoint.LibWoverEvents;
import org.betterx.wover.events.api.types.OnRegistryReady;
import org.betterx.wover.events.impl.AbstractEvent;
import org.betterx.wover.state.api.WorldState;

import net.minecraft.core.RegistryAccess;

public class RegistryReadyEventImpl extends AbstractEvent<OnRegistryReady> {
    public RegistryReadyEventImpl(String eventName) {
        super(eventName);
    }

    public void emit(RegistryAccess value, OnRegistryReady.Stage stage) {
        if (value != WorldState.registryAccess()) {
            LibWoverEvents.C.LOG.debug("Emitting event: "
                    + eventName
                    + " ("
                    + Integer.toHexString(value.hashCode())
                    + ", "
                    + stage
                    + ")"
            );

            handlers.forEach(c -> c.task.ready(value, stage));
        }
    }

}
