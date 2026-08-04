package de.ambertation.wover.tag.api.builder;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import org.jetbrains.annotations.ApiStatus;

/**
 * Allows you to add Elements to a given {@link TagKey}.
 * <p>
 * A Builder is generated whenever a TagRegistry needs to get
 * bootstraped either because it is generated during the
 * DataGen process or because the game did reload the
 * available Tags.
 * <p>
 * You should never need to implement this interface yourself or manually
 * instantiate classes implementing it.
 *
 * @param <T> The type of the Elements/{@link TagKey}.
 * @see de.ambertation.wover.tag.api.BlockTagDataProvider
 */
public interface TagBuilder<T> {
    /**
     * Adds the given elements as required to the given Tag.
     *
     * @param tagID    The Tag to add the elements to.
     * @param elements The elements to add.
     */
    @SuppressWarnings("unchecked")
    void add(TagKey<T> tagID, T... elements);


    /**
     * Adds the given element as required to the given Tags.
     *
     * @param element The element to add.
     * @param tags    The Tags to add the element to.
     */
    @SuppressWarnings("unchecked")
    void add(T element, TagKey<T>... tags);

    /**
     * Adds tags as required to the given Tag.
     *
     * @param tagID The Tag to add the other tags to.
     * @param tags  The Tags to add.
     */
    @SuppressWarnings("unchecked")
    void add(TagKey<T> tagID, TagKey<T>... tags);

    /**
     * Adds the elements represented by the passed {@link ResourceKey}s as
     * required to the given Tag.
     *
     * @param tagID The Tag to add the elements to.
     * @param keys  The keys to add.
     */

    @SuppressWarnings("unchecked")
    void add(TagKey<T> tagID, ResourceKey<T>... keys);


    /**
     * Adds the given elements as optional to the given Tag.
     *
     * @param tagID    The Tag to add the elements to.
     * @param elements The elements to add.
     */
    @SuppressWarnings("unchecked")
    void addOptional(TagKey<T> tagID, T... elements);

    /**
     * Adds the given element as optional to the given Tags.
     *
     * @param element The element to add.
     * @param tags    The Tags to add the element to.
     */
    @SuppressWarnings("unchecked")
    void addOptional(T element, TagKey<T>... tags);

    /**
     * Adds tags as optional to the given Tag.
     *
     * @param tagID The Tag to add the other tags to.
     * @param tags  The Tags to add.
     */
    @SuppressWarnings("unchecked")
    void addOptional(TagKey<T> tagID, TagKey<T>... tags);

    /**
     * Adds the elements represented by the passed {@link ResourceKey}s as
     * optional to the given Tag.
     *
     * @param tagID The Tag to add the elements to.
     * @param keys  The keys to add.
     */
    @SuppressWarnings("unchecked")
    void addOptional(TagKey<T> tagID, ResourceKey<T>... keys);

    /**
     * Makes sure the tag is created. This is useful if you want to define a Tag but do not yet have the elements to add.
     * A WoverTagProvider will call this automatically for all Tags that are in the Set of forced Tags .
     *
     * @param tagID The Tag to ensure is created.
     */
    @ApiStatus.Internal
    void asPlaceholder(TagKey<T> tagID);
}
