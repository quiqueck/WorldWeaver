package org.betterx.wover.item.api.client.trait;

import org.betterx.wover.block.impl.client.trait.ItemModelTraitBuilder;

/**
 * Central registry of ready-made client-only {@link org.betterx.wover.item.api.trait.ItemTrait} builders shipped
 * by {@code wover-sets-api}.
 */
public class ClientItemTraits {
    /** Attaches a code-driven client model factory to an item, see {@link ItemModelTrait}. */
    public static final ItemModelTrait.Builder MODEL = ItemModelTraitBuilder.BUILDER;
}
