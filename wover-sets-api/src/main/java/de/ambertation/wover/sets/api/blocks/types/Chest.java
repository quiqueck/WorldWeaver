package de.ambertation.wover.sets.api.blocks.types;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.recipe.api.RecipeTraitLibrary;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotFromDefinition;
import de.ambertation.wover.sets.api.blocks.SlotType;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityTypes;


import org.jetbrains.annotations.NotNull;

/**
 * Builds the {@link SlotType#CHEST} slot: a {@link ChestBlock} using the vanilla chest block entity, carrying
 * {@link BlockTraits#CHEST_BLOCK}, with a vanilla-style chest model and an auto-generated recipe from the set's
 * base block.
 */
public class Chest extends SlotFromDefinition {
    /**
     * Creates a factory for the {@link SlotType#CHEST} slot.
     */
    public Chest() {
        super(SlotType.CHEST);
    }

    @Override
    protected BlockDefinition<?, ?> startBlockDefinition(
            BlockRegistry registry,
            @NotNull BlockSet<?> set,
            String name
    ) {
        return registry.defineDefaultBlock(
                name,
                (def) -> new ChestBlock(
                        () -> BlockEntityTypes.CHEST,
                        SoundEvents.CHEST_OPEN,
                        SoundEvents.CHEST_CLOSE,
                        def.getProperties()
                )
        );
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.CHEST_BLOCK);
    }

    @Override
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.chest(set::getBaseBlock);
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return RecipeTraitLibrary.chest(set.recipeBaseMaterial());
    }

    @Override
    protected void finalizeDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        // Not a full cube (a sub-cube box drawn by a block entity renderer), so it must not inherit the set
        // material's sulfur cube archetype: vanilla lists no chest-shaped block in any archetype tag, and a
        // cube renders what it swallowed as a block model.
        def.addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable());
    }
}
