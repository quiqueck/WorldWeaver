package org.betterx.wover.item.api.trait;

import org.betterx.wover.item.impl.trait.BoatItemBuilder;
import org.betterx.wover.item.impl.trait.ElytraItemTraitBuilder;
import org.betterx.wover.item.impl.trait.FireproofItemBuilder;
import org.betterx.wover.item.impl.trait.ItemRecipeTraitBuilder;

public class ItemTraits {
    // Behaviour
    public static final GenericItemTrait.BuilderWithDefault IS_FIREPROOF = FireproofItemBuilder.BUILDER;

    // Type traits
    public static final ElytraItemTrait.Builder ELYTRA_ITEM = ElytraItemTraitBuilder.BUILDER;
    public static final BoatItemTrait.Builder BOAT_ITEM = BoatItemBuilder.BUILDER;
    public static final ItemRecipeTrait.Builder RECIPE_ITEM = ItemRecipeTraitBuilder.BUILDER;
}
