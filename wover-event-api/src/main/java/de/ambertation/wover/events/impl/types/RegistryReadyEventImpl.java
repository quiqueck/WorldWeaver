package de.ambertation.wover.events.impl.types;

import de.ambertation.wover.entrypoint.LibWoverEvents;
import de.ambertation.wover.events.api.types.OnRegistryReady;
import de.ambertation.wover.events.impl.AbstractEvent;
import de.ambertation.wover.state.api.WorldState;

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
