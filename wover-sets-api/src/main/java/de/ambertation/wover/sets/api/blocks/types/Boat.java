package de.ambertation.wover.sets.api.blocks.types;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.item.api.ItemDefinition;
import de.ambertation.wover.item.api.ItemRegistry;
import de.ambertation.wover.item.api.trait.ItemTrait;
import de.ambertation.wover.item.api.trait.ItemRecipeTrait;
import de.ambertation.wover.item.api.trait.ItemTraitLookup;
import de.ambertation.wover.item.api.trait.ItemTraits;
import de.ambertation.wover.recipe.api.RecipeMaterial;
import de.ambertation.wover.recipe.api.RecipeTraitLibrary;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.ItemSlotFromDefinition;
import de.ambertation.wover.sets.api.blocks.SlotType;
import de.ambertation.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.world.item.Item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builds the {@link SlotType#BOAT}/{@link SlotType#CHEST_BOAT} (or, with {@code isRaft = true},
 * {@link SlotType#RAFT}/{@link SlotType#CHEST_RAFT}) slot: an item-only slot (no block) registering a boat/raft
 * item via {@link de.ambertation.wover.item.api.ItemRegistry#defineBoatItem}, carrying
 * {@link de.ambertation.wover.item.api.trait.ItemTraits#BOAT_ITEM}, with a boat/chest-boat item model and an
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

    @Override
    protected ItemTrait<Item, ?> buildModel(BlockSet<?> set, ItemTraitLookup traitLookup) {
        return withChest ? ModelTraitLibrary.chestBoat() : ModelTraitLibrary.boat();
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
