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
import org.betterx.wover.recipe.api.RecipeTraitLibrary;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.ItemSlotDefinition;
import org.betterx.wover.sets.api.blocks.SlotType;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Boat extends ItemSlotDefinition {
    public Boat() {
        super(SlotType.BOAT);
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
        return registry.defineBoatItem(name);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, ItemDefinition<?, ?> def) {
        def.addTrait(ItemTraits.BOAT_ITEM);
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected ItemModelTrait buildModel(BlockSet<?> set, ItemTraitLookup traitLookup) {
        return ItemModelTraitLibrary.boat();
    }

    @Override
    protected ItemRecipeTrait buildRecipe(BlockSet<?> set, ItemTraitLookup traitLookup) {
        return RecipeTraitLibrary.boat(set.recipeBaseMaterial());
    }
}
