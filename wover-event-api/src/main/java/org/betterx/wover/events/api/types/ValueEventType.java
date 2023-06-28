package org.betterx.wover.events.api.types;

@FunctionalInterface
public interface ValueEventType<T> extends EventType {
    void send(T value);
}
