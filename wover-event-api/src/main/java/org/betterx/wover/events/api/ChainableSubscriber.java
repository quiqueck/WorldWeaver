package org.betterx.wover.events.api;


/**
 * Chainable subscribers are called in sequence with the output of
 * one subscriber being passed as the input to the next subscriber.
 *
 * <p>
 * Subscription chains will return the output of the last subscriber
 * in the chain. The order is determined by the event, usually subscribers
 * are ordered by priority (with higher priority subscribers being called
 * first). If two subscribers have the same priority, the order is
 * the insertion order will be used
 *
 * @see org.betterx.wover.events.api.types.OnDimensionLoad
 */
@FunctionalInterface
public interface ChainableSubscriber<R> extends Subscriber {
    /**
     * Called when the event is emitted.
     *
     * @param input the input to the subscriber
     * @return the output of the subscriber. Normally subscribers are expected
     * to return the input unless they are modifying the input in some way.
     */
    R chain(R input);
}
