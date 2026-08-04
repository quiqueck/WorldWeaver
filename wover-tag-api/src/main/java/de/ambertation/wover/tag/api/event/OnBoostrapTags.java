package de.ambertation.wover.tag.api.event;

import de.ambertation.wover.events.api.Subscriber;
import de.ambertation.wover.tag.api.event.context.TagBootstrapContext;

/**
 * Used by tag bootstrap events.
 *
 * @param <T> The type of the tag.
 * @param <P> The type of the bootstrap context.
 */
@FunctionalInterface
public interface OnBoostrapTags<T, P extends TagBootstrapContext<T>> extends Subscriber {
    /**
     * Called when the tag is being bootstrapped.
     *
     * @param context The bootstrap context, which allows you to add elements to Tags.
     */
    void bootstrap(P context);
}
