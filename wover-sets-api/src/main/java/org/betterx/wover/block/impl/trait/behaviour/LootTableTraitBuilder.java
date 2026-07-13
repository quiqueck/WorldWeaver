package org.betterx.wover.block.impl.trait.behaviour;

import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.trait.AbstractBlockTraitBuilder;
import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitKey;
import org.betterx.wover.block.api.trait.behaviour.LootTableTrait;
import org.betterx.wover.block.impl.trait.BlockTraitImpl;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.entrypoint.LibWoverBlock;
import org.betterx.wover.entrypoint.LibWoverRecipe;
import org.betterx.wover.loot.api.LootLookupProvider;
import org.betterx.wover.loot.api.LootTableManager;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LootTableTraitBuilder extends AbstractBlockTraitBuilder<Block, LootTableTrait> implements LootTableTrait.Builder {
    public static final LootTableTrait.Builder BUILDER = new LootTableTraitBuilder();

    protected LootTableTraitBuilder() {
        super(BlockTraitKey.of(LibWoverBlock.C, "loot"));
    }

    @Override
    public @Nullable BlockTrait<?, ?> withDefault() {
        return dropSelf();
    }

    @Override
    public LootTableTrait with(LootTableTrait.@NotNull LootTableFactory lootTableFactory) {
        if (!ModCore.isDatagen()) return null;
        return new Trait(lootTableFactory);
    }

    @Override
    public LootTableTrait dropNamedEntity() {
        if (!ModCore.isDatagen()) return null;
        return new Trait((tableKey, blockKey, block, provider) -> provider.dropNamedBlockEntity(block));
    }

    @Override
    public LootTableTrait dropSelf() {
        if (!ModCore.isDatagen()) return null;
        return new Trait((tableKey, blockKey, block, provider) -> provider.drop(block));
    }

    @Override
    public LootTableTrait dropSlab() {
        if (!ModCore.isDatagen()) return null;
        return new Trait((tableKey, blockKey, block, provider) -> provider.dropSlab(block));
    }

    @Override
    public @Nullable LootTableTrait dropWithSilktouchOrHoeOrShears() {
        if (!ModCore.isDatagen()) return null;
        return new Trait((tableKey, blockKey, block, provider)
                -> provider.dropWithSilkTouchOrHoeOrShears(block)
        );
    }

    @Override
    public @Nullable LootTableTrait dropWithSilktouch(ItemLike otherwise) {
        return dropWithSilktouch(otherwise, ConstantValue.exactly(1.0F));
    }

    @Override
    public @Nullable LootTableTrait dropWithSilktouch(ItemLike otherwise, NumberProvider amount) {
        if (!ModCore.isDatagen()) return null;
        return new Trait((tableKey, blockKey, block, provider)
                -> provider.dropWithSilkTouch(block, otherwise, amount)
        );
    }

    @Override
    public @Nullable LootTableTrait dropWithSilktouch() {
        if (!ModCore.isDatagen()) return null;
        return new Trait((tableKey, blockKey, block, provider)
                -> provider.dropWithSilkTouch(block)
        );
    }

    @Override
    public @Nullable LootTableTrait dropLeaves(@Nullable Block saplingBlock) {
        return new Trait((tableKey, blockKey, block, provider)
                -> provider.dropLeaves(block, saplingBlock == null ? block : saplingBlock)
        );
    }

    @Override
    public @Nullable LootTableTrait dropLeaves(float dropChance, @Nullable Block saplingBlock) {
        if (!ModCore.isDatagen()) return null;
        final float[] LEAVES_SAPLING_CHANCES = new float[]{
                0.8f * dropChance,
                dropChance,
                1.333f * dropChance,
                1.666f * dropChance
        };
        return new Trait((tableKey, blockKey, block, provider)
                -> provider.dropLeaves(block, saplingBlock == null ? block : saplingBlock, LEAVES_SAPLING_CHANCES)
        );
    }

    public static void bootstrapLootTables(
            @NotNull ModCore modCore,
            @NotNull LootLookupProvider lookup,
            @NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> tableConsumer
    ) {
        bootstrapLootTables(modCore, lookup, tableConsumer, (b, block) -> true);
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

        @Override
        public boolean keepLatestOnly() {
            return true;
        }
    }
}
