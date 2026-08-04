package de.ambertation.wover.item.api.trait;

import de.ambertation.wover.item.impl.trait.BoatItemBuilder;
import de.ambertation.wover.item.impl.trait.ElytraItemTraitBuilder;
import de.ambertation.wover.item.impl.trait.FireproofItemBuilder;
import de.ambertation.wover.item.impl.trait.ItemRecipeTraitBuilder;

/**
 * Central registry of every ready-made {@link ItemTrait} builder shipped by {@code wover-sets-api}, mirroring
 * {@link de.ambertation.wover.block.api.trait.BlockTraits} on the item side.
 */
public class ItemTraits {
    // Behaviour
    /** Marks an item as fireproof (immune to fire/lava despawn). */
    public static final GenericItemTrait.BuilderWithDefault IS_FIREPROOF = FireproofItemBuilder.BUILDER;

    // Type traits
    /** Configures an item as a custom elytra, see {@link ElytraItemTrait}. */
    public static final ElytraItemTrait.Builder ELYTRA_ITEM = ElytraItemTraitBuilder.BUILDER;
    /** Configures an item as a (chest) boat, see {@link BoatItemTrait}. */
    public static final BoatItemTrait.Builder BOAT_ITEM = BoatItemBuilder.BUILDER;
    /** Attaches a code-driven recipe factory to an item, generated automatically during recipe datagen. */
    public static final ItemRecipeTrait.Builder RECIPE_ITEM = ItemRecipeTraitBuilder.BUILDER;
}
