package org.betterx.wover.block.impl.trait.type;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.block.api.client.trait.RenderLayerTrait;
import org.betterx.wover.block.api.trait.*;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverSets;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class DoorBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new DoorBlockBuilder();

    private DoorBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_door"));
    }

    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen()) return combine(ClientBlockTraits.RENDER_LAYER.with(RenderLayerTrait.Layer.CUTOUT));
        return combine(
                new Trait(),
                BlockTraits.LOOT_TABLE.with(DoorBlockBuilder::drops),
                ClientBlockTraits.RENDER_LAYER.with(RenderLayerTrait.Layer.CUTOUT)
        );
    }

    private static LootTable.Builder drops(
            ResourceKey<LootTable> tableKey,
            ResourceKey<Block> blockKey,
            Block block,
            LootLookupProvider provider
    ) {
        return provider.dropDoor(block);
    }

    private class Trait extends BlockTraitImpl.Generic {
        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            definition.addTags(BlockTags.DOORS);
            definition.addItemTags(ItemTags.DOORS);

            if (definition.hasTrait(BlockTraits.WOOD_BLOCK)) {
                definition.addTags(BlockTags.WOODEN_DOORS);
                definition.addItemTags(ItemTags.WOODEN_DOORS);
            }
        }
    }
}