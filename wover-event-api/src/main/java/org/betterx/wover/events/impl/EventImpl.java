package org.betterx.wover.events.impl;

import org.betterx.wover.WoverEventMod;
import org.betterx.wover.events.api.types.EventType;

import java.util.function.Consumer;

public class EventImpl<T extends EventType> extends AbstractEvent<T> {
    public EventImpl(String eventName) {
        super(eventName);
    }

    public void emit(Consumer<T> c) {
        WoverEventMod.C.LOG.debug("Emitting event: " + eventName);
        handlers.forEach(c);
    }
}
