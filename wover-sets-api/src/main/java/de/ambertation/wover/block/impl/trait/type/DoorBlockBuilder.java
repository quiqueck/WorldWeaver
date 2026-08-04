package de.ambertation.wover.block.impl.trait.type;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.render.BlockRenderTraits;
import de.ambertation.wover.block.api.trait.*;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverSets;
import de.ambertation.wover.loot.api.LootLookupProvider;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class DoorBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new DoorBlockBuilder();
    private final Trait DEFAULT;

    private DoorBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_door"));
        this.DEFAULT = new Trait();
    }

    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        if (!ModCore.isDatagen())
            return combine(DEFAULT, BlockRenderTraits.RENDER_LAYER.cutout());
        return combine(
                DEFAULT,
                BlockTraits.LOOT_TABLE.with(DoorBlockBuilder::drops),
                BlockRenderTraits.RENDER_LAYER.cutout()
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
            definition
                    .strength(3F, 3F)
                    .noOcclusion();

            if (ModCore.isDatagen()) {
                definition.addTags(BlockTags.DOORS);
                definition.addItemTags(ItemTags.DOORS);

                if (definition.hasTrait(BlockTraits.WOOD_BLOCK)) {
                    definition.addTags(BlockTags.WOODEN_DOORS);
                    definition.addItemTags(ItemTags.WOODEN_DOORS);
                }
            }
        }
    }
}