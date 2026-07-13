package org.betterx.wover.events.api;


/**
 * An event is a collection of subscribers that are called when the event is emitted.
 * <p>
 * Subscribers are ordered by priority, with the highest priority value being called first. If two subscribers have
 * the same priority, they are called in the order they were subscribed. If no priority is specified, the
 * {@link #DEFAULT_PRIORITY} priority will be assigned.
 *
 * @param <T> The type of objects/functionals that can subscribe to this Event.
 */
public interface Event<T extends Subscriber> {
    /**
     * The default priority for subscribers, when added with {@link #subscribe(Subscriber)}.
     */
    int DEFAULT_PRIORITY = 1000;
    /**
     * The default priority for read-only subscribers, when added with {@link #subscribe(Subscriber)}.
     * A read-only subscriber should not modify any event releated data.
     */
    int MAX_READONLY_PRIORITY = -10000;

    /**
     * Subscribe to this event.
     * <p>
     * Will call {@link #subscribe(Subscriber, int)} with {@link #DEFAULT_PRIORITY}.
     *
     * @param subscriber The subscriber to add.
     * @return {@code true} if the subscriber was added, {@code false} if the subscriber was already subscribed.
     */
    default boolean subscribe(T subscriber) {
        return subscribe(subscriber, DEFAULT_PRIORITY);
    }

    /**
     * Subscribe to this event.
     *
     * @param subscriber The subscriber to add.
     * @param priority   The priority of the subscriber. Higher priority subscribers are called first.
     * @return {@code true} if the subscriber was added, {@code false} if the subscriber was already subscribed.
     */
    boolean subscribe(T subscriber, int priority);

    /**
     * Subscribe to this event as a read-only subscriber.
     * <p>
     * Will call {@link #subscribeReadOnly(Subscriber, int)} with {@link #DEFAULT_PRIORITY}. Note that the
     * priority passed to {@link #subscribeReadOnly(Subscriber, int)} is always relative to
     * {@link #MAX_READONLY_PRIORITY}, so read-only subscribers are always called after every subscriber that
     * was added with {@link #subscribe(Subscriber)} or {@link #subscribe(Subscriber, int)}.
     *
     * @param subscriber The subscriber to add.
     * @return {@code true} if the subscriber was added, {@code false} if the subscriber was already subscribed.
     */
    default boolean subscribeReadOnly(T subscriber) {
        return subscribeReadOnly(subscriber, DEFAULT_PRIORITY);
    }

    /**
     * Subscribe to this event as a read-only subscriber.
     * <p>
     * A read-only subscriber should not modify any event related data. Read-only subscribers are always called
     * after every subscriber that was added with {@link #subscribe(Subscriber)} or
     * {@link #subscribe(Subscriber, int)}, since their effective priority is offset by
     * {@link #MAX_READONLY_PRIORITY}. Among themselves, read-only subscribers are still ordered by the passed
     * {@code priority}, with higher priority subscribers being called first.
     *
     * @param subscriber The subscriber to add.
     * @param priority   The priority of the subscriber, relative to the other read-only subscribers.
     * @return {@code true} if the subscriber was added, {@code false} if the subscriber was already subscribed.
     */
    boolean subscribeReadOnly(T subscriber, int priority);
}
