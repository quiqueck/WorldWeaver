package de.ambertation.wover.sets.api.blocks.types;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.recipe.api.RecipeTraitLibrary;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotFromDefinition;
import de.ambertation.wover.sets.api.blocks.SlotType;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Bars extends SlotFromDefinition {
    public Bars() {
        super(SlotType.BARS);
    }

    @Override
    protected @Nullable BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        return registry.defineDefaultBlock(name, (def) -> new IronBarsBlock(def.getProperties()));
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.BARS_BLOCK.withDefault());
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return RecipeTraitLibrary.bars(set.recipeMaterial(SlotType.INGOT));
    }

    @Override
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.bars();
    }

    @Override
    protected void finalizeDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        // Not a full cube (a thin grid panel), so it must not inherit the set material's sulfur cube
        // archetype: vanilla lists no bars-shaped block in any archetype tag, and a cube renders what it
        // swallowed as a block model.
        def.addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable());
    }
}
