package org.betterx.wover.events.api.types;

public interface ChainableEventType<R> extends EventType {
    R chain(R input);
}
