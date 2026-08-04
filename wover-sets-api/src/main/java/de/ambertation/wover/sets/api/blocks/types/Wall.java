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

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builds a wall slot: a {@link WallBlock} carrying {@link BlockTraits#WALL_BLOCK}, with a vanilla-style wall
 * model. For a wood-family set the recipe is a WoVer-specific "wooden wall" (planks over fences, see
 * {@link de.ambertation.wover.recipe.api.RecipeTraitLibrary#woodWall}); for stone-like sets it is a vanilla-style
 * wall recipe from a source block plus a matching stonecutting recipe.
 */
public class Wall extends SlotFromDefinition {
    protected final @Nullable SlotType baseBlockType;

    /**
     * Creates a factory for the {@link SlotType#WALL} slot, cut from the set's base block.
     */
    public Wall() {
        this(null, SlotType.WALL);
    }

    /**
     * @param baseBlockType the slot (with fallback to the set's base slot, then vanilla stone) this wall is cut
     *                      from, or {@code null} to always use the set's base block
     * @param slot          the slot to register this wall under
     */
    public Wall(@Nullable SlotType baseBlockType, @NotNull SlotType slot) {
        super(slot);
        this.baseBlockType = baseBlockType;
    }

    @Override
    protected BlockDefinition<?, ?> startBlockDefinition(
            BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        return registry.defineDefaultBlockWithProps(name, WallBlock::new);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.WALL_BLOCK);
    }

    @Override
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.wall(baseBlockType == null
                ? set::getBaseBlock
                : () -> set.getBlockWithFallback(baseBlockType)
        );
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        final boolean wood = blockTraitLookup.hasTrait(BlockTraits.WOOD_BLOCK);

        return wood ? RecipeTraitLibrary.woodWall(
                set.recipeBaseMaterial(),
                set.recipeMaterial(SlotType.FENCE)
        ) : RecipeTraitLibrary.wall(
                baseBlockType == null
                        ? set.recipeBaseMaterial()
                        : set.recipeMaterialWithFallback(baseBlockType));

    }
}
