package org.betterx.wover.events.impl.types;

import org.betterx.wover.WoverEventMod;
import org.betterx.wover.events.api.types.ChainableEventType;
import org.betterx.wover.events.impl.AbstractEvent;

public class ChainedEventImpl<R, T extends ChainableEventType<R>> extends AbstractEvent<T> {
    public ChainedEventImpl(String eventName) {
        super(eventName);
    }

    public final R process(R input) {
        WoverEventMod.C.LOG.debug("Emitting event: " + eventName);
        for (var handler : handlers) {
            input = handler.chain(input);
        }
        return input;
    }
}
