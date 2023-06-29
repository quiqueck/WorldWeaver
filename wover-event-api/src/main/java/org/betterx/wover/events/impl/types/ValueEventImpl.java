package org.betterx.wover.events.impl.types;

import org.betterx.wover.WoverEventMod;
import org.betterx.wover.events.api.types.ValueEventType;
import org.betterx.wover.events.impl.AbstractEvent;

public class ValueEventImpl<R, T extends ValueEventType<R>> extends AbstractEvent<T> {
    public ValueEventImpl(String eventName) {
        super(eventName);
    }

    public void emit(R value) {
        WoverEventMod.C.LOG.debug("Emitting event: " + eventName);
        handlers.forEach(c -> c.task.send(value));
    }
}
