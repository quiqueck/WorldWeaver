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
 * Builds the {@link SlotType#BOAT}/{@link SlotType#CHEST_BOAT} (or, with {@code isRaft = true},
 * {@link SlotType#RAFT}/{@link SlotType#CHEST_RAFT}) slot: an item-only slot (no block) registering a boat/raft
 * item via {@link org.betterx.wover.item.api.ItemRegistry#defineBoatItem}, carrying
 * {@link org.betterx.wover.item.api.trait.ItemTraits#BOAT_ITEM}, with a boat/chest-boat item model and an
 * auto-generated recipe (planks for a plain boat; a boat plus a chest for the chest variant). A raft uses the same
 * item model/recipe shape as a boat - only the entity ({@code Raft}/{@code ChestRaft} instead of
 * {@code Boat}/{@code ChestBoat}) and its renderer differ, matching how vanilla's bamboo raft relates to its boats.
 */
public class Boat extends ItemSlotFromDefinition {
    private final boolean withChest;
    private final boolean isRaft;

    /**
     * @param withChest whether this is the chest-boat variant ({@link SlotType#CHEST_BOAT}) rather than a plain
     *                  boat ({@link SlotType#BOAT})
     */
    public Boat(boolean withChest) {
        this(withChest, false);
    }

    /**
     * @param withChest whether this is the chest variant rather than a plain boat/raft
     * @param isRaft    whether this is a raft ({@link SlotType#RAFT}/{@link SlotType#CHEST_RAFT}), rendered with
     *                  {@code RaftRenderer}/{@code RaftModel} and backed by a {@code Raft}/{@code ChestRaft}
     *                  entity, rather than a boat ({@link SlotType#BOAT}/{@link SlotType#CHEST_BOAT})
     */
    public Boat(boolean withChest, boolean isRaft) {
        super(slotFor(withChest, isRaft));
        this.withChest = withChest;
        this.isRaft = isRaft;
    }

    private static SlotType slotFor(boolean withChest, boolean isRaft) {
        if (isRaft) return withChest ? SlotType.CHEST_RAFT : SlotType.RAFT;
        return withChest ? SlotType.CHEST_BOAT : SlotType.BOAT;
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
        return registry.defineBoatItem(name, withChest, isRaft);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, ItemDefinition<?, ?> def) {
        def.addTrait(ItemTraits.BOAT_ITEM.with(withChest, isRaft));
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
                        set.recipeMaterial(isRaft ? SlotType.RAFT : SlotType.BOAT),
                        RecipeMaterial.of(CommonItemTags.CHEST)
                ) :
                RecipeTraitLibrary.boat(set.recipeBaseMaterial());
    }
}
