package org.betterx.wover.tag.api.builder;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

/**
 * Allows you to add Items to a given {@link TagKey}.
 * <p>
 * This is an extension of {@link TagBuilder} that is
 * adding some convenience methods for {@link Item}s.
 * <p>
 * A Builder is generated whenever a TagRegistry needs to get
 * bootstraped either because it is generated during the
 * DataGen process or because the game did reload the
 * available Tags.
 * <p>
 * You should never need to implement this interface yourself or manually
 * instantiate classes implementing it.
 *
 * @see org.betterx.wover.tag.api.ItemTagDataProvider
 */
public interface ItemTagBuilder extends TagBuilder<Item> {
    /**
     * Adds the given elements as required to the given Tag.
     *
     * @param tagID    The Tag to add the elements to.
     * @param elements The elements to add.
     */
    void add(TagKey<Item> tagID, ItemLike... elements);

    /**
     * Adds the given element as required to the given Tags.
     *
     * @param element The element to add.
     * @param tags    The Tags to add the element to.
     */
    void add(ItemLike element, TagKey<Item>... tags);


    /**
     * Adds given elements as optional to the given Tag.
     *
     * @param tagID    The Tag to add the elements to.
     * @param elements The elements to add.
     */
    void addOptional(TagKey<Item> tagID, ItemLike... elements);

    /**
     * Adds the given element as optional to the given Tags.
     *
     * @param element The element to add.
     * @param tags    The Tags to add the element to.
     */
    void addOptional(ItemLike element, TagKey<Item>... tags);
}
