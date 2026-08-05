package de.ambertation.wover.loot.impl;

import de.ambertation.wover.loot.api.MutableLootTable;
import de.ambertation.wover.loot.mixin.LootPoolAccessor;
import de.ambertation.wover.loot.mixin.LootTableAccessor;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * The {@link MutableLootTable} handed to the appenders of one loot table during one reload.
 * <p>
 * Nothing is written back while the appenders run: the additions are collected here and flushed by
 * {@link #commit()} once all of them are done. The working state always starts from the snapshot of the
 * table's original pools (and, per pool, its original entries and conditions), never from whatever a previous
 * reload left behind - that is what makes re-application idempotent.
 */
final class MutableLootTableImpl implements MutableLootTable {
    private final ResourceKey<LootTable> key;
    private final LootTable table;
    private final HolderLookup.Provider registries;
    private final HolderLookup.RegistryLookup<LootTable> lootTables;

    /** The datapack-provided pools plus the pools appenders added, in order. */
    private final List<LootPool> pools;
    private final int originalPoolCount;

    /** Entries to append to pools that already exist. Keyed by identity - {@link LootPool} has no equals. */
    private final Map<LootPool, List<LootPoolEntryContainer>> extraEntries = new IdentityHashMap<>();

    /** Conditions to AND onto pools that already exist. Keyed by identity - {@link LootPool} has no equals. */
    private final Map<LootPool, List<LootItemCondition>> extraConditions = new IdentityHashMap<>();

    MutableLootTableImpl(
            ResourceKey<LootTable> key,
            LootTable table,
            HolderLookup.Provider registries,
            HolderLookup.RegistryLookup<LootTable> lootTables,
            List<LootPool> originalPools
    ) {
        this.key = key;
        this.table = table;
        this.registries = registries;
        this.lootTables = lootTables;
        this.pools = new ArrayList<>(originalPools);
        this.originalPoolCount = originalPools.size();
    }

    @Override
    public ResourceKey<LootTable> key() {
        return key;
    }

    @Override
    public LootTable table() {
        return table;
    }

    @Override
    public HolderLookup.Provider registries() {
        return registries;
    }

    @Override
    public Holder<LootTable> lootTable(ResourceKey<LootTable> key) {
        return lootTables.getOrThrow(Objects.requireNonNull(key, "key"));
    }

    @Override
    public List<LootPool> pools() {
        return List.copyOf(pools);
    }

    @Override
    public MutableLootTable addPool(LootPool pool) {
        pools.add(Objects.requireNonNull(pool, "pool"));
        return this;
    }

    @Override
    public MutableLootTable addPool(LootPool.Builder pool) {
        return addPool(Objects.requireNonNull(pool, "pool").build());
    }

    @Override
    public MutableLootTable addToPool(int index, LootPoolEntryContainer.Builder<?>... entries) {
        addTo(pools.get(index), build(entries));
        return this;
    }

    @Override
    public MutableLootTable addToPool(int index, List<LootPoolEntryContainer> entries) {
        addTo(pools.get(index), List.copyOf(Objects.requireNonNull(entries, "entries")));
        return this;
    }

    @Override
    public MutableLootTable addToEveryPool(List<LootPoolEntryContainer> entries) {
        return addToAllPools(List.copyOf(Objects.requireNonNull(entries, "entries")));
    }

    @Override
    public MutableLootTable addToEveryPool(LootPoolEntryContainer.Builder<?>... entries) {
        return addToAllPools(build(entries));
    }

    private MutableLootTable addToAllPools(List<LootPoolEntryContainer> built) {
        if (built.isEmpty()) return this;
        // Entry containers are immutable, so the same instances can be shared between pools.
        for (LootPool pool : pools) {
            addTo(pool, built);
        }
        return this;
    }

    private static List<LootPoolEntryContainer> build(LootPoolEntryContainer.Builder<?>... entries) {
        if (entries == null || entries.length == 0) return List.of();
        final List<LootPoolEntryContainer> built = new ArrayList<>(entries.length);
        for (LootPoolEntryContainer.Builder<?> entry : entries) {
            built.add(Objects.requireNonNull(entry, "entry").build());
        }
        return built;
    }

    private void addTo(LootPool pool, List<LootPoolEntryContainer> built) {
        if (built.isEmpty()) return;
        extraEntries.computeIfAbsent(pool, p -> new ArrayList<>()).addAll(built);
    }

    @Override
    public MutableLootTable addConditionToPool(int index, LootItemCondition.Builder condition) {
        return addConditionToPool(index, Objects.requireNonNull(condition, "condition").build());
    }

    @Override
    public MutableLootTable addConditionToEveryPool(LootItemCondition.Builder condition) {
        return addConditionToEveryPool(Objects.requireNonNull(condition, "condition").build());
    }

    @Override
    public MutableLootTable addConditionToPool(int index, LootItemCondition condition) {
        addCondition(pools.get(index), Objects.requireNonNull(condition, "condition"));
        return this;
    }

    @Override
    public MutableLootTable addConditionToEveryPool(LootItemCondition condition) {
        Objects.requireNonNull(condition, "condition");
        // Only the pools present *now* - a pool an earlier addPool() call in this same pass just added is not
        // retroactively restricted by a condition queued before it existed.
        for (LootPool pool : pools) {
            addCondition(pool, condition);
        }
        return this;
    }

    private void addCondition(LootPool pool, LootItemCondition condition) {
        extraConditions.computeIfAbsent(pool, p -> new ArrayList<>()).add(condition);
    }

    /** How many pools this pass appended; used for the summary log line. */
    int addedPoolCount() {
        return pools.size() - originalPoolCount;
    }

    /** How many pools the datapacks provided; used for the per-table log line. */
    int originalPoolCount() {
        return originalPoolCount;
    }

    /** How many entries this pass appended to pools that already existed; used for the per-table log line. */
    int addedEntryCount() {
        return extraEntries.values().stream().mapToInt(List::size).sum();
    }

    /** How many pools this pass added a condition to; used for the per-table log line. */
    int restrictedPoolCount() {
        return extraConditions.size();
    }

    /**
     * Writes the collected additions to the {@link LootTable} and its pools.
     *
     * @return {@code true} if anything was written
     */
    boolean commit() {
        final boolean poolsChanged = pools.size() != originalPoolCount;
        if (!poolsChanged && extraEntries.isEmpty() && extraConditions.isEmpty()) return false;

        for (Map.Entry<LootPool, List<LootPoolEntryContainer>> entry : extraEntries.entrySet()) {
            final LootPool pool = entry.getKey();
            final List<LootPoolEntryContainer> original = LootTableAppendersImpl.ORIGINAL_ENTRIES
                    .computeIfAbsent(pool, p -> List.copyOf(p.entries));

            final List<LootPoolEntryContainer> merged = new ArrayList<>(original.size() + entry.getValue().size());
            merged.addAll(original);
            merged.addAll(entry.getValue());
            ((LootPoolAccessor) (Object) pool).wover_setEntries(List.copyOf(merged));
        }

        for (Map.Entry<LootPool, List<LootItemCondition>> entry : extraConditions.entrySet()) {
            final LootPool pool = entry.getKey();
            final LootPoolAccessor accessor = (LootPoolAccessor) (Object) pool;
            final List<LootItemCondition> original = LootTableAppendersImpl.ORIGINAL_CONDITIONS
                    .computeIfAbsent(pool, p -> List.copyOf(accessor.wover_getConditions()));

            final List<LootItemCondition> merged = new ArrayList<>(original.size() + entry.getValue().size());
            merged.addAll(original);
            merged.addAll(entry.getValue());
            final List<LootItemCondition> mergedList = List.copyOf(merged);
            accessor.wover_setConditions(mergedList);
            // conditions alone is not what addRandomItems tests - compositeCondition is, and it is only ever
            // derived once, in the constructor. Recompute it exactly the way LootPool itself does, or the
            // conditions we just wrote would silently never be evaluated.
            final Predicate<LootContext> compositeCondition = Util.allOf(mergedList);
            accessor.wover_setCompositeCondition(compositeCondition);
        }

        ((LootTableAccessor) (Object) table).wover_setPools(List.copyOf(pools));
        return true;
    }
}
