package org.betterx.wover.block.api.trait.behaviour;

import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitBuilder;
import org.betterx.wover.block.impl.trait.behaviour.LootTableTraitBuilder;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import org.jetbrains.annotations.NotNull;

public interface LootTableTrait extends BlockTrait<Block, LootTableTrait> {
    interface LootTableFactory {
        LootTable.Builder buildBlockLoot(
                @NotNull ResourceKey<LootTable> tableKey,
                @NotNull ResourceKey<Block> blockKey,
                @NotNull Block block,
                @NotNull LootLookupProvider provider
        );
    }

    interface Builder extends BlockTraitBuilder<Block, LootTableTrait> {
        LootTableTrait with(@NotNull LootTableFactory lootTableFactory);
    }

    LootTableFactory lootTableFactory();
    static void bootstrapLootTables(
            @NotNull ModCore modCore,
            @NotNull LootLookupProvider lookup,
            @NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> tableConsumer
    ) {
        LootTableTraitBuilder.bootstrapLootTables(modCore, lookup, tableConsumer, (b, block) -> true);
    }

    static void bootstrapLootTables(
            @NotNull ModCore modCore,
            @NotNull LootLookupProvider lookup,
            @NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> tableConsumer,
            @NotNull BiPredicate<ResourceKey<Block>, Block> filter
    ) {
        LootTableTraitBuilder.bootstrapLootTables(modCore, lookup, tableConsumer, filter);
    }
}
