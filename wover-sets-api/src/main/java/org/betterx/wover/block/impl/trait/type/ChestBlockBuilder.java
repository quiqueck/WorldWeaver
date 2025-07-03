package org.betterx.wover.block.impl.trait.type;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.client.trait.ClientBlockTraits;
import org.betterx.wover.block.api.trait.*;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverSets;
import org.betterx.wover.loot.api.LootLookupProvider;
import org.betterx.wover.tag.api.predefined.CommonBlockTags;
import org.betterx.wover.tag.api.predefined.CommonItemTags;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class ChestBlockBuilder extends AbstractBlockTraitBuilder.Generic implements GenericBlockTrait.BuilderWithDefaults {
    public static final GenericBlockTrait.BuilderWithDefaults BUILDER = new ChestBlockBuilder();
    private final Trait DEFAULT;

    private ChestBlockBuilder() {
        super(BlockTraitKey.ofUnique(LibWoverSets.C, "is_chest"));
        this.DEFAULT = new Trait();
    }

    public @Nullable List<BlockTrait<?, ?>> withDefault() {
        if (ModCore.isDatagen()) {
            return combine(
                    DEFAULT,
                    BlockTraits.MAGIC_SOURCE.withDefault(),
                    BlockTraits.VALID_BLOCK_ENTITY.with(BlockEntityType.CHEST),
                    BlockTraits.LOOT_TABLE.with(ChestBlockBuilder::drops)
            );
        } else {
            return combine(
                    DEFAULT,
                    BlockTraits.VALID_BLOCK_ENTITY.with(BlockEntityType.CHEST),
                    ClientBlockTraits.CHEST_RENDERER.withDefault()
            );
        }

    }

    private static LootTable.Builder drops(
            ResourceKey<LootTable> tableKey,
            ResourceKey<Block> blockKey,
            Block block,
            LootLookupProvider provider
    ) {
        LootTable.Builder builder = LootTable.lootTable();
        var pool = LootPool.lootPool()
                           .setRolls(ConstantValue.exactly(1.0f))
                           .add(LootItem.lootTableItem(block).apply(CopyComponentsFunction
                                   .copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                   .include(DataComponents.CUSTOM_NAME)
                                   .include(DataComponents.CONTAINER)
                                   .include(DataComponents.LOCK)
                                   .include(DataComponents.CONTAINER_LOOT)))
                           .when(ExplosionCondition.survivesExplosion());
        builder.setRandomSequence(blockKey.location());

        return builder.withPool(pool);
    }

    private class Trait extends BlockTraitImpl.Generic {
        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        @Override
        public void configure(BlockDefinition<Block, ? extends BlockDefinition<Block, ?>> definition) {
            definition.noOcclusion();

            if (ModCore.isDatagen()) {
                definition.addTags(CommonBlockTags.CHEST);
                definition.addItemTags(CommonItemTags.CHEST);

                if (definition.hasTrait(BlockTraits.WOOD_BLOCK)) {
                    definition.addTags(CommonBlockTags.WOODEN_CHEST);
                    definition.addItemTags(CommonItemTags.WOODEN_CHEST);
                }
            }
        }
    }
}
