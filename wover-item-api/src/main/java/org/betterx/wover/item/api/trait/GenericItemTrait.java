package org.betterx.wover.item.api.trait;

import net.minecraft.world.item.Item;

/**
 * Convenience specialization of {@link ItemTrait} for traits that apply to any {@link Item} rather than a
 * specific item subtype (e.g. behavioral traits like "is fireproof" that don't need extra runtime data).
 *
 * @see AbstractItemTraitBuilder.Generic
 */
public interface GenericItemTrait extends ItemTrait<Item, GenericItemTrait> {
    /**
     * A {@link GenericItemTrait} builder that can produce several default-configured traits at once.
     */
    interface BuilderWithDefaults extends ItemTraitBuilder.WithDefaults<Item, GenericItemTrait> {
    }

    /**
     * A {@link GenericItemTrait} builder that can produce a single default-configured trait.
     */
    interface BuilderWithDefault extends ItemTraitBuilder.WithDefault<Item, GenericItemTrait> {
    }

    /**
     * A plain {@link GenericItemTrait} builder without a default-configuration shortcut.
     */
    interface Builder extends ItemTraitBuilder<Item, GenericItemTrait> {
    }
}

