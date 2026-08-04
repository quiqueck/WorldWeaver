package de.ambertation.wover.events.impl.types;

import de.ambertation.wover.entrypoint.LibWoverEvents;
import de.ambertation.wover.events.api.ChainableSubscriber;
import de.ambertation.wover.events.impl.AbstractEvent;

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
