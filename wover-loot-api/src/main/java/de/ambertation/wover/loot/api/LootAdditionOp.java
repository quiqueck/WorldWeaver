package de.ambertation.wover.loot.api;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;

/**
 * One operation of a {@link LootAddition}: the data form of a single {@link MutableLootTable} verb.
 * <p>
 * The three operation types map 1:1 onto the verbs that interface offers - deliberately, so that a datapack
 * cannot express anything the Java API cannot, and so that both paths run through the exact same, idempotent
 * implementation:
 * <table border="1">
 *     <caption>Operations and the verbs they call</caption>
 *     <tr><th>{@code "type"}</th><th>calls</th></tr>
 *     <tr><td>{@code add_pool}</td><td>{@link MutableLootTable#addPool(LootPool)}</td></tr>
 *     <tr><td>{@code add_entries} with {@code "pool": <int>}</td><td>{@link MutableLootTable#addToPool(int, List)}</td></tr>
 *     <tr><td>{@code add_entries} with {@code "pool": "every"}</td><td>{@link MutableLootTable#addToEveryPool(List)}</td></tr>
 *     <tr><td>{@code add_condition} with {@code "pool": <int>}</td><td>{@link MutableLootTable#addConditionToPool(int, LootItemCondition)}</td></tr>
 *     <tr><td>{@code add_condition} with {@code "pool": "every"}</td><td>{@link MutableLootTable#addConditionToEveryPool(LootItemCondition)}</td></tr>
 * </table>
 * <p>
 * The operations of one addition are applied <b>in the order they are written</b>, which matters: the pools an
 * {@code add_entries}/{@code add_condition} sees are the datapack-provided ones <em>plus</em> whatever earlier
 * operations (of this addition, of an earlier addition, or of a Java appender) already added - exactly as in
 * the Java API. "Gate every vanilla pool off, then append a replacement pool" is therefore written as an
 * {@code add_condition} followed by an {@code add_pool}, and never the other way round.
 */
public sealed interface LootAdditionOp {
    /**
     * Codec for a single operation, dispatched on its {@code "type"} field.
     */
    Codec<LootAdditionOp> CODEC = Kind.CODEC.dispatch("type", LootAdditionOp::kind, Kind::codec);

    /**
     * Codec for a list of operations. Accepts a single object as a one-element list.
     */
    Codec<List<LootAdditionOp>> LIST_CODEC = ExtraCodecs.compactListCodec(CODEC);

    /**
     * @return the discriminator this operation serializes as
     */
    Kind kind();

    /**
     * Runs this operation against the table being extended.
     *
     * @param table the mutable view of the loot table
     */
    void applyTo(MutableLootTable table);

    /**
     * Which pool(s) an operation addresses: either a single pool by its index in
     * {@link MutableLootTable#pools()}, or every pool that exists at the moment the operation runs.
     * <p>
     * Serializes as either the string {@code "every"} or a non-negative integer.
     *
     * @param index the pool index, or {@code -1} for {@link #EVERY}
     */
    record PoolSelector(int index) {
        /** Addresses every pool the table currently has. */
        public static final PoolSelector EVERY = new PoolSelector(-1);

        private static final String EVERY_NAME = "every";

        /**
         * {@code "every"} or a non-negative pool index.
         */
        public static final Codec<PoolSelector> CODEC = Codec
                .either(
                        Codec.STRING.comapFlatMap(
                                s -> EVERY_NAME.equals(s)
                                        ? DataResult.success(EVERY)
                                        : DataResult.error(() -> "Not a pool selector: '" + s
                                                + "' (expected \"" + EVERY_NAME + "\" or a pool index)"),
                                s -> EVERY_NAME
                        ),
                        Codec.intRange(0, Integer.MAX_VALUE).xmap(PoolSelector::new, PoolSelector::index)
                )
                .xmap(
                        either -> either.map(l -> l, r -> r),
                        selector -> selector.isEvery() ? Either.left(selector) : Either.right(selector)
                );

        /**
         * Addresses the pool at {@code index}.
         *
         * @param index the index of the pool in {@link MutableLootTable#pools()}
         * @return a selector for that single pool
         */
        public static PoolSelector at(int index) {
            if (index < 0) throw new IllegalArgumentException("Pool index must not be negative: " + index);
            return new PoolSelector(index);
        }

        /**
         * @return {@code true} if this selector addresses every pool rather than a single one
         */
        public boolean isEvery() {
            return index < 0;
        }

        @Override
        public String toString() {
            return isEvery() ? EVERY_NAME : Integer.toString(index);
        }
    }

    /**
     * The operation discriminator, i.e. the value of the {@code "type"} field.
     */
    enum Kind implements StringRepresentable {
        /** {@link AddPool} */
        ADD_POOL("add_pool"),
        /** {@link AddEntries} */
        ADD_ENTRIES("add_entries"),
        /** {@link AddCondition} */
        ADD_CONDITION("add_condition");

        /** Codec for the discriminator itself. */
        public static final Codec<Kind> CODEC = StringRepresentable.fromEnum(Kind::values);

        private final String name;

        Kind(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        MapCodec<? extends LootAdditionOp> codec() {
            return switch (this) {
                case ADD_POOL -> AddPool.MAP_CODEC;
                case ADD_ENTRIES -> AddEntries.MAP_CODEC;
                case ADD_CONDITION -> AddCondition.MAP_CODEC;
            };
        }
    }

    /**
     * Appends whole pools at the end of the table.
     * <pre>{@code { "type": "add_pool", "pools": [ { "rolls": 1, "entries": [ ... ] } ] } }</pre>
     * {@code "pools"} also accepts a single pool object instead of a list.
     *
     * @param pools the pools to append, in order
     */
    record AddPool(List<LootPool> pools) implements LootAdditionOp {
        /** The codec for the body of an {@code add_pool} operation. */
        public static final MapCodec<AddPool> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(
                        ExtraCodecs
                                .nonEmptyList(ExtraCodecs.compactListCodec(LootPool.CODEC))
                                .fieldOf("pools")
                                .forGetter(AddPool::pools)
                )
                .apply(instance, AddPool::new));

        @Override
        public Kind kind() {
            return Kind.ADD_POOL;
        }

        @Override
        public void applyTo(MutableLootTable table) {
            for (LootPool pool : pools) {
                table.addPool(pool);
            }
        }
    }

    /**
     * Appends entries to pools that already exist. This is what a "roll exactly one thing" table like
     * {@code minecraft:gameplay/piglin_bartering} needs - an extra pool there would hand out an extra stack on
     * every roll instead of joining the weighted choice.
     * <pre>{@code { "type": "add_entries", "pool": "every",
     *   "entries": [ { "type": "minecraft:item", "name": "minecraft:diamond", "weight": 5 } ] } }</pre>
     * {@code "pool"} defaults to {@code "every"}; {@code "entries"} also accepts a single entry object.
     *
     * @param pool    which pool(s) to extend
     * @param entries the entries to append to each addressed pool
     */
    record AddEntries(PoolSelector pool, List<LootPoolEntryContainer> entries) implements LootAdditionOp {
        /** The codec for the body of an {@code add_entries} operation. */
        public static final MapCodec<AddEntries> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(
                        PoolSelector.CODEC
                                .optionalFieldOf("pool", PoolSelector.EVERY)
                                .forGetter(AddEntries::pool),
                        ExtraCodecs
                                .nonEmptyList(ExtraCodecs.compactListCodec(LootPoolEntries.CODEC))
                                .fieldOf("entries")
                                .forGetter(AddEntries::entries)
                )
                .apply(instance, AddEntries::new));

        @Override
        public Kind kind() {
            return Kind.ADD_ENTRIES;
        }

        @Override
        public void applyTo(MutableLootTable table) {
            if (pool.isEvery()) table.addToEveryPool(entries);
            else table.addToPool(pool.index(), entries);
        }
    }

    /**
     * Adds a condition to pools that already exist. The condition is AND'd onto whatever condition the pool
     * already had, so this can only narrow when a pool applies, never widen it.
     * <pre>{@code { "type": "add_condition", "pool": "every",
     *   "condition": { "condition": "minecraft:random_chance", "chance": 0.0 } } }</pre>
     * {@code "pool"} defaults to {@code "every"}. {@code "condition"} is always inlined - there is no
     * predicate registry to reference an entry of, unlike on later Minecraft versions.
     *
     * @param pool      which pool(s) to restrict
     * @param condition the condition to AND onto each addressed pool
     */
    record AddCondition(PoolSelector pool, LootItemCondition condition) implements LootAdditionOp {
        /** The codec for the body of an {@code add_condition} operation. */
        public static final MapCodec<AddCondition> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(
                        PoolSelector.CODEC
                                .optionalFieldOf("pool", PoolSelector.EVERY)
                                .forGetter(AddCondition::pool),
                        LootItemCondition.DIRECT_CODEC
                                .fieldOf("condition")
                                .forGetter(AddCondition::condition)
                )
                .apply(instance, AddCondition::new));

        @Override
        public Kind kind() {
            return Kind.ADD_CONDITION;
        }

        @Override
        public void applyTo(MutableLootTable table) {
            if (pool.isEvery()) table.addConditionToEveryPool(condition);
            else table.addConditionToPool(pool.index(), condition);
        }
    }
}
