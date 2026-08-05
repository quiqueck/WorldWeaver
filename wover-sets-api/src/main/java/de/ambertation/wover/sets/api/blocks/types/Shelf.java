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
import net.minecraft.world.level.block.ShelfBlock;
import net.minecraft.world.level.block.SoundType;

import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builds the {@link SlotType#SHELF} slot: a vanilla {@link ShelfBlock} - the wall-mounted, three-slot display
 * shelf that a redstone signal makes items sit on top of - carrying {@link BlockTraits#SHELF_BLOCK}, with a
 * vanilla-style shelf model and the vanilla stripped-log recipe.
 * <p>
 * Unlike most wooden slots, a shelf does <em>not</em> read the set's planks texture for its faces: every one of
 * them is packed into a single dedicated 32x32 {@code block/<name>_shelf} texture (see
 * {@code minecraft:block/oak_shelf}), so a set adding this slot has to ship that texture. Only the particles
 * come from another block - see {@link #buildModel}.
 */
public class Shelf extends SlotFromDefinition {
    /**
     * Creates a factory for the {@link SlotType#SHELF} slot.
     */
    public Shelf() {
        this(SlotType.SHELF);
    }

    /**
     * @param slot the slot to register this shelf under
     */
    public Shelf(SlotType slot) {
        super(slot);
    }

    @Override
    protected @Nullable BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        return registry.defineDefaultBlockWithProps(name, ShelfBlock::new);
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.SHELF_BLOCK.withDefault());
    }

    @Override
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        // Vanilla points a shelf's particles at the stripped log it is crafted from, because vanilla's
        // stripped logs have one flat block/stripped_<wood>_log texture. A modded log family is a rotated
        // pillar with _side/_top textures and no texture under its own bare name, so the particle mapping
        // would resolve to a file that does not exist. The set's base block (its planks) is the closest
        // thing that does have one, and it is a near-identical tone for every wood here.
        return ModelTraitLibrary.shelf(set::getBaseBlock);
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return RecipeTraitLibrary.shelf(set.recipeMaterialWithFallback(SlotType.STRIPPED_LOG));
    }

    @Override
    protected void finalizeBlockDefinitions(
            BlockSet<?> set,
            BlockDefinition<?, ? extends BlockDefinition<?, ?>> definition,
            BiConsumer<SlotType, Block> blockDefinitionConsumer
    ) {
        // Vanilla gives every shelf SoundType.SHELF regardless of the wood - crimson and warped shelves use it
        // just like oak's - so this has to override both WOOD_BLOCK's SoundType.WOOD and any per-set sound the
        // material applied in addCommonBlockDefinitions. Setters and traits are applied in call order, so it
        // only wins from here, the last point before the block is built. (This branch has no
        // finalizeDefinitions hook, and no SULFUR_CUBE_ARCHETYPE trait to opt out of either.)
        definition.sound(SoundType.SHELF);

        super.finalizeBlockDefinitions(set, definition, blockDefinitionConsumer);
    }
}
