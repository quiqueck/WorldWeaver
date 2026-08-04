package de.ambertation.wover.tag.api.event.context;

import de.ambertation.wover.tag.api.builder.ItemTagBuilder;

import net.minecraft.world.item.Item;

/**
 * Context for {@link de.ambertation.wover.tag.api.TagRegistry#bootstrapEvent()} for {@link Item} tags.
 */
public interface ItemTagBootstrapContext extends TagBootstrapContext<Item>, ItemTagBuilder {
}
