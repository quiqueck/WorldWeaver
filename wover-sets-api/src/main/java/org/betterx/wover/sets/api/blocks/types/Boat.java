package org.betterx.wover.sets.api.blocks.types;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.item.api.ItemDefinition;
import org.betterx.wover.item.api.ItemRegistry;
import org.betterx.wover.item.api.client.model.ItemModelTraitLibrary;
import org.betterx.wover.item.api.client.trait.ItemModelTrait;
import org.betterx.wover.item.api.trait.ItemRecipeTrait;
import org.betterx.wover.item.api.trait.ItemTraitLookup;
import org.betterx.wover.item.api.trait.ItemTraits;
import org.betterx.wover.recipe.api.RecipeMaterial;
import org.betterx.wover.recipe.api.RecipeTraitLibrary;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.ItemSlotFromDefinition;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.tag.api.predefined.CommonItemTags;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builds the {@link SlotType#BOAT}/{@link SlotType#CHEST_BOAT} slot: an item-only slot (no block) registering a
 * boat item via {@link org.betterx.wover.item.api.ItemRegistry#defineBoatItem}, carrying
 * {@link org.betterx.wover.item.api.trait.ItemTraits#BOAT_ITEM}, with a boat/chest-boat item model and an
 * auto-generated recipe (planks for a plain boat; a boat plus a chest for the chest variant).
 */
public class Boat extends ItemSlotFromDefinition {
    private final boolean withChest;

    /**
     * @param withChest whether this is the chest-boat variant ({@link SlotType#CHEST_BOAT}) rather than a plain
     *                  boat ({@link SlotType#BOAT})
     */
    public Boat(boolean withChest) {
        super(withChest ? SlotType.CHEST_BOAT : SlotType.BOAT);
        this.withChest = withChest;
    }

    @Override
    protected @Nullable BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set, @NotNull String name
    ) {
        return null;
    }

    @Override
    protected @Nullable ItemDefinition<?, ?> startItemDefinition(@NotNull ItemRegistry registry, @NotNull String name) {
        return registry.defineBoatItem(name, withChest);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, ItemDefinition<?, ?> def) {
        def.addTrait(ItemTraits.BOAT_ITEM.with(withChest));
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected ItemModelTrait buildModel(BlockSet<?> set, ItemTraitLookup traitLookup) {
        return withChest ? ItemModelTraitLibrary.chestBoat() : ItemModelTraitLibrary.boat();
    }

    @Override
    protected ItemRecipeTrait buildRecipe(BlockSet<?> set, ItemTraitLookup traitLookup) {
        return withChest ?
                RecipeTraitLibrary.chestBoat(
                        set.recipeMaterial(SlotType.BOAT),
                        RecipeMaterial.of(CommonItemTags.CHEST)
                ) :
                RecipeTraitLibrary.boat(set.recipeBaseMaterial());
    }
}
