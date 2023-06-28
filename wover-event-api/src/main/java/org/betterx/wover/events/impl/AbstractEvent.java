package org.betterx.wover.events.impl;

import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.EventType;

import java.util.LinkedList;
import java.util.List;

public class AbstractEvent<T extends EventType> implements Event<T> {
    protected final String eventName;
    protected final List<T> handlers = new LinkedList<>();

    public AbstractEvent(String eventName) {
        this.eventName = eventName;
    }

    public final boolean subscribe(T handler) {
        if (!handlers.contains(handler)) {
            handlers.add(handler);
            return true;
        }

        return false;
    }
}
