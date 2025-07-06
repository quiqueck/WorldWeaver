package org.betterx.wover.sets.api.blocks.types;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.model.ModelTraitLibrary;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.trait.BlockRecipeTrait;
import org.betterx.wover.block.api.trait.BlockTraitLookup;
import org.betterx.wover.block.api.trait.BlockTraits;
import org.betterx.wover.block.api.trait.SignBlockDefinition;
import org.betterx.wover.recipe.api.RecipeTraitLibrary;
import org.betterx.wover.sets.api.blocks.BlockSet;
import org.betterx.wover.sets.api.blocks.SlotType;
import org.betterx.wover.sets.api.blocks.WoodenBlockSet;
import org.betterx.wover.sets.api.blocks.WoodenSlotFromDefinition;

import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.WallHangingSignBlock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HangingSign extends WoodenSlotFromDefinition {
    public static final SlotType HANGING_WALL_SIGN = new SlotType("hanging_wall_sign");

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
            throw new IllegalArgumentException("Gate slot can only be used with WoodenBlockSet.");
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
            blockDefinitionConsumer.accept(HANGING_WALL_SIGN, signType.wallSignBlock());
        } else {
            throw new IllegalArgumentException("HangingSign can only be used with SignBlockDefinition.");
        }
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.HANGING_SIGN_BLOCK);
    }


    @Environment(EnvType.CLIENT)
    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
        return ModelTraitLibrary.hangingSign(
                () -> set.getBlock(SlotType.STRIPPED_LOG),
                () -> set.getBlock(HANGING_WALL_SIGN)
        );
    }

    @Override
    protected BlockRecipeTrait buildRecipe(BlockSet<?> set, BlockTraitLookup traitLookup) {
        return RecipeTraitLibrary.hangingSign(set.recipeMaterial(SlotType.STRIPPED_LOG));
    }
}
