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

import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Sign extends WoodenSlotFromDefinition {
    public static final SlotType WALL_SIGN = new SlotType("wall_sign");

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
            blockDefinitionConsumer.accept(WALL_SIGN, signType.wallSignBlock());
        } else {
            throw new IllegalArgumentException("HangingSign can only be used with SignBlockDefinition.");
        }
    }

    @Override
    protected void addSlotSpecificDefinitions(BlockSet<?> set, BlockDefinition<?, ?> def) {
        def.addTrait(BlockTraits.SIGN_BLOCK);
    }


    @Environment(EnvType.CLIENT)
    @Override
    protected BlockModelTrait buildModel(BlockSet<?> set, BlockTraitLookup blockTraitLookup) {
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