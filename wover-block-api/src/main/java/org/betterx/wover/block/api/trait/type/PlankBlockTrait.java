package org.betterx.wover.block.api.trait.type;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitKey;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverBlock;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

public class PlankBlockTrait extends BlockTrait<Block, BlockTrait.VoidRuntime<Block>> {
    public static final PlankBlockTrait.Builder BUILDER = new PlankBlockTrait.Builder();

    public static class Builder extends BlockTrait.TraitBuilder {
        private Builder() {
            super(BlockTraitKey.of(LibWoverBlock.C, "type_plank"));
        }

        public @Nullable BlockTrait<?, ?> withDefault() {
            if (!ModCore.isDatagen()) return null;
            return new PlankBlockTrait();
        }
    }


    protected PlankBlockTrait() {
        super(BUILDER.ID);
    }

    @Override
    public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
        definition.addTags(BlockTags.PLANKS);
        definition.addItemTags(ItemTags.PLANKS);
    }
}
