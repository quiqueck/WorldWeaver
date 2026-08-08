package de.ambertation.wover.loot.api;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

/**
 * A short-lived, mutable view of a {@link LootTable} that was just loaded from the datapacks.
 * <p>
 * An instance is handed to every {@link LootTableAppender} registered for the table, once per resource
 * reload, while the reloadable loot table registry is being rebuilt and before it is validated. It is only
 * valid for the duration of the {@link LootTableAppender#appendTo(MutableLootTable)} call - do not keep a
 * reference to it, and do not keep a reference to {@link #table()} either: both are replaced on every
 * {@code /reload}.
 * <p>
 * Everything this view offers is additive. There is deliberately no way to remove or replace what the
 * datapacks provided, so several mods can extend the same table without invalidating each other.
 */
public interface MutableLootTable {
    /**
     * The key the table is registered under, e.g. {@link net.minecraft.world.level.storage.loot.BuiltInLootTables#BASTION_TREASURE}.
     *
     * @return the loot table's registry key
     */
    ResourceKey<LootTable> key();

    /**
     * The loot table that is being extended, in its original, datapack-provided state.
     * <p>
     * Reading is safe; the pools this returns are the ones the datapacks shipped, without any additions made
     * through this view (those are only written back once every appender has run).
     *
     * @return the loot table being extended
     */
    LootTable table();

    /**
     * The (already tag-updated) static registries of the server that is loading. Use this when an addition
     * needs to look something up, e.g. an enchantment.
     * <p>
     * Note that this does <em>not</em> contain the reloadable registries themselves - the loot table registry
     * is still being assembled at this point.
     *
     * @return the static registry lookup
     */
    HolderLookup.Provider registries();

    /**
     * Resolves another loot table from the loot table registry that is currently being assembled, as a
     * {@link Holder} suitable for
     * {@link net.minecraft.world.level.storage.loot.entries.NestedLootTable#lootTableReference(Holder)}.
     * <p>
     * Unlike {@link #registries()}, which deliberately excludes the reloadable loot table registry because it
     * is still being assembled, this resolves against that very registry - safe to do here because every loot
     * table's JSON has already been parsed by the time appenders run; only the cross-table validation vanilla
     * performs afterward hasn't happened yet. Typical use is nesting one of the mod's own datagen-emitted
     * tables into a vanilla table it is extending.
     *
     * @param key the loot table to reference
     * @return a holder referencing that loot table
     * @throws IllegalStateException if no loot table is registered under {@code key}
     */
    Holder<LootTable> lootTable(ResourceKey<LootTable> key);

    /**
     * The pools currently queued for the table: the datapack-provided ones plus everything appenders have
     * added so far, in order.
     *
     * @return an unmodifiable snapshot of the pools
     */
    List<LootPool> pools();

    /**
     * Appends a new pool at the end of the table.
     *
     * @param pool the pool to append
     * @return this view, for chaining
     */
    MutableLootTable addPool(LootPool pool);

    /**
     * Appends a new pool at the end of the table.
     *
     * @param pool the pool to build and append
     * @return this view, for chaining
     */
    MutableLootTable addPool(LootPool.Builder pool);

    /**
     * Appends entries to a single pool of the table, identified by its index in {@link #pools()}.
     * <p>
     * Use this (or {@link #addToEveryPool(LootPoolEntryContainer.Builder[])}) instead of
     * {@link #addPool(LootPool)} whenever the table is a "roll exactly one thing" table like
     * {@link net.minecraft.world.level.storage.loot.BuiltInLootTables#PIGLIN_BARTERING} - an extra pool there
     * would hand out an extra stack on every roll instead of joining the weighted choice.
     *
     * @param index   the index of the pool to extend
     * @param entries the entries to append to that pool
     * @return this view, for chaining
     * @throws IndexOutOfBoundsException if {@code index} does not address a pool
     */
    MutableLootTable addToPool(int index, LootPoolEntryContainer.Builder<?>... entries);

    /**
     * {@link #addToPool(int, LootPoolEntryContainer.Builder[])} for entries that are already built - the form
     * the datapack path uses, because {@code LootPoolEntries.CODEC} decodes finished
     * {@link LootPoolEntryContainer}s rather than builders.
     *
     * @param index   the index of the pool to extend
     * @param entries the entries to append to that pool
     * @return this view, for chaining
     * @throws IndexOutOfBoundsException if {@code index} does not address a pool
     */
    MutableLootTable addToPool(int index, List<LootPoolEntryContainer> entries);

    /**
     * Appends entries to every pool the table currently has - the datapack-provided pools plus any pool an
     * earlier appender added.
     *
     * @param entries the entries to append to each pool
     * @return this view, for chaining
     */
    MutableLootTable addToEveryPool(LootPoolEntryContainer.Builder<?>... entries);

    /**
     * {@link #addToEveryPool(LootPoolEntryContainer.Builder[])} for entries that are already built - the form
     * the datapack path uses.
     *
     * @param entries the entries to append to each pool
     * @return this view, for chaining
     */
    MutableLootTable addToEveryPool(List<LootPoolEntryContainer> entries);

    /**
     * Adds a condition to a pool that already exists, identified by its index in {@link #pools()}.
     * <p>
     * The new condition is AND'd together with whatever conditions the pool already has - from the datapacks,
     * or from an earlier appender - so this can only narrow when a pool applies, never widen it. Use this to
     * restrict a vanilla pool to a subset of the situations it used to apply to (e.g. only outside a
     * particular dimension) while still leaving all of its original entries and functions untouched.
     *
     * @param index     the index of the pool to restrict
     * @param condition the condition to add
     * @return this view, for chaining
     * @throws IndexOutOfBoundsException if {@code index} does not address a pool
     */
    MutableLootTable addConditionToPool(int index, LootItemCondition.Builder condition);

    /**
     * {@link #addConditionToPool(int, LootItemCondition.Builder)} for a condition that is already built - the
     * form the datapack path uses.
     *
     * @param index     the index of the pool to restrict
     * @param condition the condition to add
     * @return this view, for chaining
     * @throws IndexOutOfBoundsException if {@code index} does not address a pool
     */
    MutableLootTable addConditionToPool(int index, LootItemCondition condition);

    /**
     * Adds a condition to every pool the table currently has - the datapack-provided pools plus any pool an
     * earlier appender added. See {@link #addConditionToPool(int, LootItemCondition.Builder)} for the AND
     * semantics.
     *
     * @param condition the condition to add to each pool
     * @return this view, for chaining
     */
    MutableLootTable addConditionToEveryPool(LootItemCondition.Builder condition);

    /**
     * {@link #addConditionToEveryPool(LootItemCondition.Builder)} for a condition that is already built - the
     * form the datapack path uses.
     *
     * @param condition the condition to add to each pool
     * @return this view, for chaining
     */
    MutableLootTable addConditionToEveryPool(LootItemCondition condition);
}
