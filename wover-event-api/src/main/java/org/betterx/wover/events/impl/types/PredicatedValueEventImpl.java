package org.betterx.wover.events.impl.types;

import org.betterx.wover.events.api.types.ValueEventType;

import java.util.function.Predicate;

public class PredicatedValueEventImpl<R, T extends ValueEventType<R>> extends ValueEventImpl<R, T> {
    private final Predicate<R> predicate;

    public PredicatedValueEventImpl(String eventName, Predicate<R> predicate) {
        super(eventName);
        this.predicate = predicate;
    }

    @Override
    public void emit(R value) {
        if (predicate.test(value))
            super.emit(value);
    }
}
