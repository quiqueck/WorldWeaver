package org.betterx.wover.tag.api.event.context;

import org.betterx.wover.tag.api.TagRegistry;
import org.betterx.wover.tag.api.builder.TagBuilder;

import net.minecraft.tags.TagKey;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Provides the BootstrapContext for the {@link org.betterx.wover.tag.api.TagRegistry#bootstrapEvent()}
 * as well as the {@code WoverTagProvider}.
 * <p>
 * Using the context you can add elements to tags as well as enumerate which elements are already present.
 *
 * @param <T> The type of the element.
 */
public interface TagBootstrapContext<T> extends TagBuilder<T> {
    /**
     * Enumerates all elements that are already present in the given Tag.
     *
     * @param consumer The consumer to pass the elements to.
     *                 The first parameter is the {@link TagKey} that represents the Tag,
     *                 the second is the list of elements that are currently added to
     *                 that Tag.
     */
    void forEach(BiConsumer<TagKey<T>, List<TagElementWrapper<T>>> consumer);

    /**
     * The {@link TagRegistry} that was used to create this Instance.
     *
     * @return The {@link TagRegistry} that is used to register the Tags.
     */
    TagRegistry<T, ? extends TagBootstrapContext<T>> registry();
}
