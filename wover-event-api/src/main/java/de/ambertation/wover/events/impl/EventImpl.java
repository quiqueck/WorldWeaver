package de.ambertation.wover.events.impl;

import de.ambertation.wover.entrypoint.LibWoverEvents;
import de.ambertation.wover.events.api.Subscriber;

import java.util.function.Consumer;

public class EventImpl<T extends Subscriber> extends AbstractEvent<T> {
    public EventImpl(String eventName) {
        super(eventName);
    }

    public void emit(Consumer<T> c) {
        LibWoverEvents.C.LOG.debug("Emitting event: " + eventName);
        handlers.forEach(sub -> c.accept(sub.task));
    }
}
