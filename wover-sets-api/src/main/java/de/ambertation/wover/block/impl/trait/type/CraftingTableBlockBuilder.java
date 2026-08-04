package de.ambertation.wover.block.impl.trait.type;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.trait.*;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverSets;
import de.ambertation.wover.tag.api.predefined.CommonBlockTags;
import de.ambertation.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.world.level.block.Block;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class CraftingTableBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new CraftingTableBlockBuilder();

    private CraftingTableBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_crafting_table"));
    }

    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen()) return null;
        return combine(new Trait(), BlockTraits.LOOT_TABLE.dropSelf());
    }

    private class Trait extends BlockTraitImpl.Generic {
        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            definition.addTags(CommonBlockTags.WORKBENCHES);
            definition.addItemTags(CommonItemTags.WORKBENCHES);
        }
    }
}