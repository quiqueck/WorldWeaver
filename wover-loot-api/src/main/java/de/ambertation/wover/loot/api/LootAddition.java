package de.ambertation.wover.loot.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * One entry of a {@link LootAdditionFile}: a list of {@link LootAdditionOp operations} plus the loot tables
 * they are applied to.
 * <pre>{@code
 * {
 *   "target": ["minecraft:chests/ruined_portal", "minecraft:chests/nether_bridge"],
 *   "required": true,
 *   "operations": [
 *     { "type": "add_pool", "pools": [ { "rolls": 1, "entries": [ ... ] } ] }
 *   ]
 * }
 * }</pre>
 * {@code "target"} accepts a single id or a list of ids. {@code "required"} defaults to {@code true} and works
 * exactly like the field of the same name on a vanilla tag entry: with {@code "required": false} an addition
 * whose target loot table does not exist is skipped silently, which is what an addition aimed at a table
 * another mod ships wants. With {@code "required": true} the missing target is reported as an error and the
 * addition is skipped; loading never fails hard, because one broken pack must not make a world unloadable.
 *
 * @param targets    the loot tables to extend
 * @param required   whether a missing target is an error (default {@code true}) or silently skipped
 * @param operations the operations to run, in order
 */
public record LootAddition(
        List<ResourceKey<LootTable>> targets,
        boolean required,
        List<LootAdditionOp> operations
) {
    /**
     * Codec for a single addition.
     */
    public static final Codec<LootAddition> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    ExtraCodecs
                            .nonEmptyList(ExtraCodecs.compactListCodec(ResourceKey.codec(Registries.LOOT_TABLE)))
                            .fieldOf("target")
                            .forGetter(LootAddition::targets),
                    Codec.BOOL.optionalFieldOf("required", true).forGetter(LootAddition::required),
                    ExtraCodecs
                            .nonEmptyList(LootAdditionOp.LIST_CODEC)
                            .fieldOf("operations")
                            .forGetter(LootAddition::operations)
            )
            .apply(instance, LootAddition::new));

    /**
     * Runs every operation of this addition against {@code table}, in order.
     *
     * @param table the mutable view of the loot table being extended
     */
    public void applyTo(MutableLootTable table) {
        for (LootAdditionOp op : operations) {
            op.applyTo(table);
        }
    }

    /**
     * Starts building an addition for the given loot tables.
     *
     * @param targets the loot tables to extend
     * @return a new builder
     */
    @SafeVarargs
    public static Builder forTables(ResourceKey<LootTable>... targets) {
        return new Builder(List.of(targets));
    }

    /**
     * Builds a {@link LootAddition} with the same verbs {@link MutableLootTable} offers, so that datagen code
     * reads like the Java appender it replaces.
     * <p>
     * Obtained from {@link LootAddition#forTables(ResourceKey[])} or, more usually, from
     * {@link LootAdditionFile.Builder#forTables(ResourceKey[])}.
     */
    public static final class Builder {
        private final List<ResourceKey<LootTable>> targets;
        private final List<LootAdditionOp> operations = new ArrayList<>();
        private boolean required = true;

        Builder(List<ResourceKey<LootTable>> targets) {
            if (targets.isEmpty()) throw new IllegalArgumentException("A loot addition needs at least one target");
            this.targets = List.copyOf(targets);
        }

        /**
         * Marks the targets as optional: if a target loot table does not exist, the addition is skipped
         * silently instead of being reported as an error. Use this for an addition aimed at a table another
         * mod ships.
         *
         * @return this builder, for chaining
         */
        public Builder optional() {
            this.required = false;
            return this;
        }

        /**
         * @param required whether a missing target is an error; see {@link LootAddition#required()}
         * @return this builder, for chaining
         */
        public Builder required(boolean required) {
            this.required = required;
            return this;
        }

        /**
         * Appends whole pools at the end of the table. Mirrors {@link MutableLootTable#addPool(LootPool)}.
         *
         * @param pools the pools to append
         * @return this builder, for chaining
         */
        public Builder addPool(LootPool.Builder... pools) {
            final List<LootPool> built = new ArrayList<>(pools.length);
            for (LootPool.Builder pool : pools) {
                built.add(Objects.requireNonNull(pool, "pool").build());
            }
            if (!built.isEmpty()) operations.add(new LootAdditionOp.AddPool(List.copyOf(built)));
            return this;
        }

        /**
         * Appends entries to the pool at {@code index}. Mirrors {@link MutableLootTable#addToPool(int, List)}.
         *
         * @param index   the index of the pool to extend
         * @param entries the entries to append
         * @return this builder, for chaining
         */
        public Builder addToPool(int index, LootPoolEntryContainer.Builder<?>... entries) {
            return addEntries(LootAdditionOp.PoolSelector.at(index), entries);
        }

        /**
         * Appends entries to every pool that exists when this operation runs. Mirrors
         * {@link MutableLootTable#addToEveryPool(List)}.
         *
         * @param entries the entries to append to each pool
         * @return this builder, for chaining
         */
        public Builder addToEveryPool(LootPoolEntryContainer.Builder<?>... entries) {
            return addEntries(LootAdditionOp.PoolSelector.EVERY, entries);
        }

        private Builder addEntries(LootAdditionOp.PoolSelector pool, LootPoolEntryContainer.Builder<?>... entries) {
            final List<LootPoolEntryContainer> built = new ArrayList<>(entries.length);
            for (LootPoolEntryContainer.Builder<?> entry : entries) {
                built.add(Objects.requireNonNull(entry, "entry").build());
            }
            if (!built.isEmpty()) operations.add(new LootAdditionOp.AddEntries(pool, List.copyOf(built)));
            return this;
        }

        /**
         * AND's a condition onto the pool at {@code index}. Mirrors
         * {@link MutableLootTable#addConditionToPool(int, LootItemCondition)}.
         *
         * @param index     the index of the pool to restrict
         * @param condition the condition to add
         * @return this builder, for chaining
         */
        public Builder addConditionToPool(int index, LootItemCondition.Builder condition) {
            return addCondition(LootAdditionOp.PoolSelector.at(index), condition);
        }

        /**
         * AND's a condition onto every pool that exists when this operation runs. Mirrors
         * {@link MutableLootTable#addConditionToEveryPool(LootItemCondition)}.
         *
         * @param condition the condition to add to each pool
         * @return this builder, for chaining
         */
        public Builder addConditionToEveryPool(LootItemCondition.Builder condition) {
            return addCondition(LootAdditionOp.PoolSelector.EVERY, condition);
        }

        private Builder addCondition(LootAdditionOp.PoolSelector pool, LootItemCondition.Builder condition) {
            operations.add(new LootAdditionOp.AddCondition(
                    pool,
                    Objects.requireNonNull(condition, "condition").build()
            ));
            return this;
        }

        /**
         * @return the finished addition
         */
        public LootAddition build() {
            if (operations.isEmpty()) {
                throw new IllegalStateException("A loot addition for " + targets + " has no operations");
            }
            return new LootAddition(targets, required, List.copyOf(operations));
        }
    }
}
