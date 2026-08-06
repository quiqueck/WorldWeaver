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

import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;


import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builds the {@link SlotType#HANGING_SIGN} slot (plus its {@link #HANGING_WALL_SIGN} companion): a
 * {@link de.ambertation.wover.block.api.trait.SignBlockDefinition} pairing a {@link CeilingHangingSignBlock} with a
 * {@link WallHangingSignBlock}, both using the set's {@link WoodenBlockSet#woodType()}, carrying
 * {@link BlockTraits#HANGING_SIGN_BLOCK}, with a vanilla-style hanging sign model and an auto-generated recipe
 * from the set's stripped log. Only usable on a {@link WoodenBlockSet}.
 */
public class HangingSign extends WoodenSlotFromDefinition {
    /** The slot the wall variant of this hanging sign is registered under. */
    public static final SlotType HANGING_WALL_SIGN = new SlotType("hanging_wall_sign");

    /**
     * Creates a factory for the {@link SlotType#HANGING_SIGN} slot.
     */
    public HangingSign() {
        super(SlotType.HANGING_SIGN);
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
                    set.baseName + "_" + HANGING_WALL_SIGN.suffix(),
                    (def) -> new CeilingHangingSignBlock(woodenBlockSet.woodType(), def.getProperties()),
                    (def) -> new WallHangingSignBlock(woodenBlockSet.woodType(), def.getProperties()),
                    HangingSignItem::new
            );
        } else {
            throw new IllegalArgumentException("HangingSign slot can only be used with WoodenBlockSet.");
        }
    }

    @Override
    protected void buildAndRegisterBlocks(
            BlockSet<?> set,
            BlockDefinition<?, ? extends BlockDefinition<?, ?>> definition,
            BiConsumer<SlotType, Block> blockDefinitionConsumer
    ) {
        if (definition instanceof SignBlockDefinition signDef) {
            var signType = signDef.buildAndRegisterSign();
            blockDefinitionConsumer.accept(slot, signType.signBlock());
            blockDefinitionConsumer.accept(HANGING_WALL_SIGN, signType.wallSignBlock());
        } else {
            throw new IllegalArgumentException("HangingSign can only be used with SignBlockDefinition.");
        }
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.HANGING_SIGN_BLOCK);
    }


    @Override
    protected BlockTrait<Block, ?> buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.hangingSign(
                () -> set.getBlock(SlotType.STRIPPED_LOG),
                () -> set.getBlock(HANGING_WALL_SIGN)
        );
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return RecipeTraitLibrary.hangingSign(set.recipeMaterial(SlotType.STRIPPED_LOG));
    }

    @Override
    protected void finalizeWoodDefinitions(WoodenBlockSet<?> set, BlockDefinition<?, ?> def) {
        // Not a full cube (a thin board on a chain), so it must not inherit the set material's sulfur cube
        // archetype: vanilla lists no hanging sign-shaped block in any archetype tag, and a cube renders
        // what it swallowed as a block model.
        def.addTrait(BlockTraits.SULFUR_CUBE_ARCHETYPE.notSwallowable());
    }
}
