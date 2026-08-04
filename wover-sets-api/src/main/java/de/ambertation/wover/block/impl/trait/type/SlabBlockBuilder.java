package de.ambertation.wover.block.impl.trait.type;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.*;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverSets;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class SlabBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new SlabBlockBuilder();

    private SlabBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_slab"));
    }

    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen()) return null;
        return combine(new Trait(), BlockTraits.LOOT_TABLE.dropSlab());
    }

    private class Trait extends BlockTraitImpl.Generic {
        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            definition.addTags(BlockTags.SLABS);
            definition.addItemTags(ItemTags.SLABS);

            if (definition.hasTrait(BlockTraits.WOOD_BLOCK)) {
                definition.addTags(BlockTags.WOODEN_SLABS);
                definition.addItemTags(ItemTags.WOODEN_SLABS);
            }
        }
    }
}
