package de.ambertation.wover.sets.api.blocks.types;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.model.ModelTraitLibrary;
import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockRecipeTrait;
import de.ambertation.wover.block.api.trait.BlockTraitLookup;
import de.ambertation.wover.block.api.trait.BlockTraits;
import de.ambertation.wover.block.api.trait.SignBlockDefinition;
import de.ambertation.wover.recipe.api.RecipeTraitLibrary;
import de.ambertation.wover.sets.api.blocks.BlockSet;
import de.ambertation.wover.sets.api.blocks.SlotType;
import de.ambertation.wover.sets.api.blocks.WoodenBlockSet;
import de.ambertation.wover.sets.api.blocks.WoodenSlotFromDefinition;

import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;


import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builds the {@link SlotType#SIGN} slot (plus its {@link #WALL_SIGN} companion): a
 * {@link de.ambertation.wover.block.api.trait.SignBlockDefinition} pairing a {@link StandingSignBlock} with a
 * {@link WallSignBlock}, both using the set's {@link WoodenBlockSet#woodType()}, carrying
 * {@link BlockTraits#SIGN_BLOCK}, with a vanilla-style sign model and an auto-generated recipe from the set's
 * base block. Only usable on a {@link WoodenBlockSet}.
 */
public class Sign extends WoodenSlotFromDefinition {
    /** The slot the wall variant of this sign is registered under. */
    public static final SlotType WALL_SIGN = new SlotType("wall_sign");

    /**
     * Creates a factory for the {@link SlotType#SIGN} slot.
     */
    public Sign() {
        super(SlotType.SIGN);
    }

    @Override
    protected @Nullable BlockDefinition<?, ?> startBlockDefinition(
            @NotNull BlockRegistry registry,
            @NotNull BlockSet<?> set,
            @NotNull String name
    ) {
        if (set instanceof WoodenBlockSet<?> woodenBlockSet) {
            return new SignBlockDefinition(
                    registry,
                    name,
                    set.baseName + "_" + WALL_SIGN.suffix(),
                    (def) -> new StandingSignBlock(woodenBlockSet.woodType(), def.getProperties()),
                    (def) -> new WallSignBlock(woodenBlockSet.woodType(), def.getProperties()),
                    SignItem::new
            );
        } else {
            throw new IllegalArgumentException("Sign slot can only be used with WoodenBlockSet.");
        }
    }

    @Override
    protected void finalizeBlockDefinitions(
            BlockSet<?> set,
            BlockDefinition<?, ? extends BlockDefinition<?, ?>> definition,
            BiConsumer<SlotType, Block> blockDefinitionConsumer
    ) {
        if (definition instanceof SignBlockDefinition signDef) {
            var signType = signDef.buildAndRegisterSign();
            blockDefinitionConsumer.accept(slot, signType.signBlock());
            blockDefinitionConsumer.accept(WALL_SIGN, signType.wallSignBlock());
        } else {
            throw new IllegalArgumentException("Sign can only be used with SignBlockDefinition.");
        }
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.SIGN_BLOCK);
    }


    @Override
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.sign(
                () -> set.getBaseBlock(),
                () -> set.getBlock(WALL_SIGN)
        );
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return RecipeTraitLibrary.sign(set.recipeBaseMaterial());
    }
}