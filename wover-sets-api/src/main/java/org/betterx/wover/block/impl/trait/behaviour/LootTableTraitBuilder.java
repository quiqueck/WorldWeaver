package org.betterx.wover.block.impl.trait.behaviour;

import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.trait.AbstractBlockTraitBuilder;
import org.betterx.wover.block.api.trait.BlockTraitKey;
import org.betterx.wover.block.api.trait.behaviour.LootTableTrait;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverBlock;
import org.betterx.wover.entrypoint.LibWoverRecipe;
import org.betterx.wover.loot.api.LootLookupProvider;
import org.betterx.wover.loot.api.LootTableManager;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import org.jetbrains.annotations.NotNull;

public class LootTableTraitBuilder extends AbstractBlockTraitBuilder<Block, LootTableTrait> implements LootTableTrait.Builder {
    public static final LootTableTrait.Builder BUILDER = new LootTableTraitBuilder();

    protected LootTableTraitBuilder() {
        super(BlockTraitKey.of(LibWoverBlock.C, "loot"));
    }

    @Override
    public LootTableTrait with(LootTableTrait.@NotNull LootTableFactory lootTableFactory) {
        if (!ModCore.isDatagen()) return null;
        return new Trait(lootTableFactory);
    }
    
    public static void bootstrapLootTables(
            @NotNull ModCore modCore,
            @NotNull LootLookupProvider lookup,
            @NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> tableConsumer,
            @NotNull BiPredicate<ResourceKey<Block>, Block> filter
    ) {
        BlockRegistry
                .forMod(modCore)
                .allEntries()
                .filter(b -> filter.test(b.getKey(), b.getValue()))
                .forEach(b -> {
                    var runtimeTraits = BUILDER.getRuntimeTraits(b.getValue());
                    if (runtimeTraits == null) return;
                    runtimeTraits.forEach(trait -> {
                        try {
                            var tableKey = LootTableManager.getBlockLootTableKey(b.getKey());
                            var builder = trait.lootTableFactory().buildBlockLoot(
                                    tableKey,
                                    b.getKey(),
                                    b.getValue(),
                                    lookup
                            );

                            if (builder != null)
                                tableConsumer.accept(tableKey, builder);
                        } catch (Exception ex) {
                            LibWoverRecipe.C.LOG.error("Failed to build loot-table for block: " + b.getKey(), ex);
                        }
                    });
                });
    }

    class Trait extends BlockTraitImpl<Block, LootTableTrait> implements LootTableTrait {
        final LootTableFactory lootTableFactory;

        public Trait(LootTableFactory lootTableFactory) {
            super();
            this.lootTableFactory = lootTableFactory;
        }

        @Override
        public BlockTraitKey key() {
            return traitKey;
        }

        public LootTableFactory lootTableFactory() {
            return lootTableFactory;
        }

        @Override
        public LootTableTrait forRuntime() {
            return this;
        }
    }
}
