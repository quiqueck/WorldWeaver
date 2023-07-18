package org.betterx.wover.tag.api.event.context;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;

/**
 * A wrapper for an element to add to a tag.
 * <p>
 * This is used to retain all needed information about an element that is to be added to a tag.
 *
 * @param <T> The type of the element.
 */
public interface TagElementWrapper<T> {
    /**
     * The {@link ResourceLocation} of the element to add to the tag.
     *
     * @return The {@link ResourceLocation} of the element to add to the tag.
     */
    ResourceLocation id();
    /**
     * Whether the element is a tag or not.
     *
     * @return {@code true} if the element is a tag, {@code false} otherwise.
     */
    boolean tag();

    /**
     * Whether the element is optional or not.
     *
     * @return {@code false} if the element is optional, {@code true} otherwise.
     */
    boolean required();

    /**
     * Creates a {@link TagEntry} from this wrapper.
     *
     * @return A {@link TagEntry} from this wrapper.
     */
    TagEntry createTagEntry();
}
