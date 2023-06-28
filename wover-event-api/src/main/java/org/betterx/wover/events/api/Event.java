package org.betterx.wover.events.api;

import org.betterx.wover.events.api.types.EventType;

public interface Event<T extends EventType> {
    boolean subscribe(T handler);
}
