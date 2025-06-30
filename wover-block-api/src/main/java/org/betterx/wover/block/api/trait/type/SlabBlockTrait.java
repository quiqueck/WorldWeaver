package org.betterx.wover.block.api.trait.type;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitKey;
import org.betterx.wover.block.api.trait.material.WoodMaterialBlockTrait;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverBlock;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.SlabBlock;

import org.jetbrains.annotations.Nullable;

public class SlabBlockTrait extends BlockTrait<SlabBlock, BlockTrait.VoidRuntime<SlabBlock>> {
    public static final SlabBlockTrait.Builder BUILDER = new SlabBlockTrait.Builder();

    public static class Builder extends BlockTrait.TraitBuilder {
        private Builder() {
            super(BlockTraitKey.of(LibWoverBlock.C, "type_slab"));
        }

        public @Nullable BlockTrait<?, ?> withDefault() {
            if (!ModCore.isDatagen()) return null;
            return new SlabBlockTrait();
        }
    }
    
    protected SlabBlockTrait() {
        super(BUILDER.ID);
    }

    @Override
    public void configure(BlockDefinition<SlabBlock, ? extends BlockDefinition<SlabBlock, ?>> definition) {
        definition.addTags(BlockTags.SLABS);
        definition.addItemTags(ItemTags.SLABS);

        if (definition.hasTrait(WoodMaterialBlockTrait.BUILDER.ID)) {
            definition.addTags(BlockTags.WOODEN_SLABS);
            definition.addItemTags(ItemTags.WOODEN_SLABS);
        }
    }
}
