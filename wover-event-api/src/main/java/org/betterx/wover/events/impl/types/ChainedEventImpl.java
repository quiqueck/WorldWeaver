package org.betterx.wover.events.impl.types;

import org.betterx.wover.entrypoint.LibWoverEvents;
import org.betterx.wover.events.api.ChainableSubscriber;
import org.betterx.wover.events.impl.AbstractEvent;

public class ChainedEventImpl<R, T extends ChainableSubscriber<R>> extends AbstractEvent<T> {
    public ChainedEventImpl(String eventName) {
        super(eventName);
    }

    public final R process(R input) {
        LibWoverEvents.C.LOG.debug("Emitting event: " + eventName);
        for (var subscriber : handlers) {
            input = subscriber.task.chain(input);
        }
        return input;
    }
}
