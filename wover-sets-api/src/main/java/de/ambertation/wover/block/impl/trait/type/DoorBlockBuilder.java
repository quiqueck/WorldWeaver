package de.ambertation.wover.block.impl.trait.type;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.render.BlockRenderTraits;
import de.ambertation.wover.block.api.trait.*;
import de.ambertation.wover.block.impl.trait.BlockTraitImpl;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.entrypoint.LibWoverSets;
import de.ambertation.wover.loot.api.LootLookupProvider;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockItemTags;
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
                //26.2 pruned the ItemTags handles for the tags that exist as a block/item pair
                //(DOORS, BUTTONS, STONE_BUTTONS, FENCES, STAIRS, SLABS, TRAPDOORS) and the BlockTags
                //handle LOGS_THAT_BURN, and replaced them with the new BlockItemTags registry of
                //BlockItemTagId records. Only the Java handle moved: BlockItemTagId.create("doors")
                //builds both keys from the same minecraft:doors location, so .item() is exactly the
                //old ItemTags.DOORS (and .block() the old BlockTags.LOGS_THAT_BURN) and neither the
                //live tag nor the generated JSON changes. The block-tag siblings that kept their
                //constants (BlockTags.DOORS, BUTTONS, SLABS, ...) are still used directly.
                definition.addItemTags(BlockItemTags.DOORS.item());

                if (definition.hasTrait(BlockTraits.WOOD_BLOCK)) {
                    definition.addTags(BlockTags.WOODEN_DOORS);
                    definition.addItemTags(ItemTags.WOODEN_DOORS);
                }
            }
        }
    }
}