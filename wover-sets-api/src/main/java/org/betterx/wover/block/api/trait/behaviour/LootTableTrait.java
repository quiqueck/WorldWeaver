package org.betterx.wover.block.api.trait.behaviour;

import org.betterx.wover.block.api.trait.BlockTrait;
import org.betterx.wover.block.api.trait.BlockTraitBuilder;
import org.betterx.wover.block.impl.trait.behaviour.LootTableTraitBuilder;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link BlockTrait} that attaches a code-driven loot table factory to a block.
 * <p>
 * This is an alternative to implementing {@code BlockLootProvider} directly on the block class: any of the
 * {@link Builder} shortcuts (or a fully custom {@link LootTableFactory}) can be handed to
 * {@link org.betterx.wover.block.api.BlockDefinition#addTrait} while the block is being configured, and the
 * resulting loot table is generated automatically for every block carrying the trait via
 * {@link #bootstrapLootTables}.
 */
public interface LootTableTrait extends BlockTrait<Block, LootTableTrait> {
    /**
     * Builds a block's loot table at datagen time.
     */
    interface LootTableFactory {
        /**
         * Builds the loot table for a single block.
         *
         * @param tableKey the resource key the resulting table will be registered under
         * @param blockKey the registry key of the block the table is for
         * @param block    the block the table is for
         * @param provider helper with common vanilla loot conditions/functions and enchantment lookups
         * @return the built loot table
         */
        LootTable.Builder buildBlockLoot(
                @NotNull ResourceKey<LootTable> tableKey,
                @NotNull ResourceKey<Block> blockKey,
                @NotNull Block block,
                @NotNull LootLookupProvider provider
        );
    }

    /**
     * Builds {@link LootTableTrait} instances, including shortcuts for the most common vanilla loot patterns.
     */
    interface Builder extends BlockTraitBuilder.WithDefault<Block, LootTableTrait> {
        /**
         * Creates a trait using a fully custom {@link LootTableFactory}.
         *
         * @param lootTableFactory builds the loot table
         * @return the new trait, or {@code null} outside of a datagen environment
         */
        @Nullable LootTableTrait with(@NotNull LootTableFactory lootTableFactory);

        /**
         * Creates a trait that drops the block as a named entity (e.g. preserving a custom name/nbt), matching
         * vanilla's block-entity-aware self-drop behavior.
         *
         * @return the new trait, or {@code null} outside of a datagen environment
         */
        @Nullable LootTableTrait dropNamedEntity();

        /**
         * Creates a trait that simply drops the block itself.
         *
         * @return the new trait, or {@code null} outside of a datagen environment
         */
        @Nullable LootTableTrait dropSelf();

        /**
         * Creates a trait using vanilla's slab drop logic (drops 1 or 2 depending on the slab's {@code TYPE}).
         *
         * @return the new trait, or {@code null} outside of a datagen environment
         */
        @Nullable LootTableTrait dropSlab();

        /**
         * Creates a trait that drops the block itself only when mined with Silk Touch, and nothing otherwise.
         *
         * @return the new trait, or {@code null} outside of a datagen environment
         */
        @Nullable LootTableTrait dropWithSilktouch();

        /**
         * Creates a trait that drops the block with Silk Touch, or {@code otherwise} when mined normally.
         *
         * @param otherwise the item dropped without Silk Touch
         * @return the new trait, or {@code null} outside of a datagen environment
         */
        @Nullable LootTableTrait dropWithSilktouch(ItemLike otherwise);

        /**
         * Creates a trait that drops the block with Silk Touch, or a given {@code amount} of {@code otherwise}
         * when mined normally.
         *
         * @param otherwise the item dropped without Silk Touch
         * @param amount    the amount of {@code otherwise} to drop
         * @return the new trait, or {@code null} outside of a datagen environment
         */
        @Nullable LootTableTrait dropWithSilktouch(ItemLike otherwise, NumberProvider amount);

        /**
         * Creates a trait that drops the block itself when mined with Silk Touch, a hoe, or shears.
         *
         * @return the new trait, or {@code null} outside of a datagen environment
         */
        @Nullable LootTableTrait dropWithSilktouchOrHoeOrShears();

        /**
         * Creates a trait using vanilla's ore-drop logic: Silk Touch drops the block itself, otherwise a
         * fortune-boosted {@code count} of {@code drop}. Reproduces bclib's {@code BaseOreBlock} loot table.
         *
         * @param drop  supplies the item the ore drops (read lazily, so it may reference a not-yet-assigned
         *              registry field)
         * @param count the base number of items to drop before the fortune bonus
         * @return the new trait, or {@code null} outside of a datagen environment
         */
        @Nullable LootTableTrait dropOre(@NotNull Supplier<Item> drop, @NotNull NumberProvider count);

        /**
         * Creates a trait using vanilla's ore-drop logic with a {@code min..max} uniform drop count. Shorthand
         * for {@link #dropOre(Supplier, NumberProvider)} with {@code UniformGenerator.between(min, max)}.
         *
         * @param drop supplies the item the ore drops
         * @param min  the minimum number of items to drop before the fortune bonus
         * @param max  the maximum number of items to drop before the fortune bonus
         * @return the new trait, or {@code null} outside of a datagen environment
         */
        @Nullable LootTableTrait dropOre(@NotNull Supplier<Item> drop, int min, int max);

        /**
         * Creates a trait using vanilla's leaves drop logic (chance-based sapling drop, plus sticks).
         *
         * @param saplingBlock the sapling dropped by this leaves block, or {@code null} if it drops none
         * @return the new trait, or {@code null} outside of a datagen environment
         */
        @Nullable LootTableTrait dropLeaves(@Nullable Block saplingBlock);

        /**
         * Creates a trait using vanilla's leaves drop logic with a custom sapling drop chance.
         *
         * @param dropChance   the chance the sapling drops
         * @param saplingBlock the sapling dropped by this leaves block, or {@code null} if it drops none
         * @return the new trait, or {@code null} outside of a datagen environment
         */
        @Nullable LootTableTrait dropLeaves(float dropChance, @Nullable Block saplingBlock);
    }

    /**
     * @return the factory that will build this trait's loot table
     */
    LootTableFactory lootTableFactory();

    /**
     * Builds and registers the loot table for every block registered under {@code modCore} that carries this
     * trait. Called automatically during loot table datagen; only needed directly for custom filtering.
     *
     * @param modCore      the mod whose blocks should be scanned
     * @param lookup       helper with common vanilla loot conditions/functions and enchantment lookups
     * @param tableConsumer receives each built loot table
     */
    static void bootstrapLootTables(
            @NotNull ModCore modCore,
            @NotNull LootLookupProvider lookup,
            @NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> tableConsumer
    ) {
        LootTableTraitBuilder.bootstrapLootTables(modCore, lookup, tableConsumer, (b, block) -> true);
    }

    /**
     * Builds and registers the loot table for every block registered under {@code modCore} that carries this
     * trait and matches {@code filter}.
     *
     * @param modCore      the mod whose blocks should be scanned
     * @param lookup       helper with common vanilla loot conditions/functions and enchantment lookups
     * @param tableConsumer receives each built loot table
     * @param filter       restricts which blocks are processed
     */
    static void bootstrapLootTables(
            @NotNull ModCore modCore,
            @NotNull LootLookupProvider lookup,
            @NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> tableConsumer,
            @NotNull BiPredicate<ResourceKey<Block>, Block> filter
    ) {
        LootTableTraitBuilder.bootstrapLootTables(modCore, lookup, tableConsumer, filter);
    }
}
