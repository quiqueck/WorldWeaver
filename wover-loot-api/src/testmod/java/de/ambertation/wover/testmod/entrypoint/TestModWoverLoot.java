package de.ambertation.wover.testmod.entrypoint;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.loot.api.LootTableAppenders;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetNameFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import net.fabricmc.api.ModInitializer;

/**
 * Main entrypoint of the loot-api testmod.
 * <p>
 * It registers the two shapes of addition the API supports, on two vanilla tables that ship no such item,
 * so {@code LootAppenderGameTest} can tell "the addition landed" apart from "vanilla already had that":
 * <ul>
 *     <li>a whole extra pool on {@link #EXTENDED_TABLE}. It rolls exactly once and has a single entry, so
 *     <em>every</em> roll of that table yields exactly one {@link Items#DRAGON_EGG} carrying the custom name
 *     {@link #POOL_MARKER_NAME} - which makes both "was it added?" and "was it added twice?" directly
 *     observable, whether by rolling the table or by opening a chest that fills from it;</li>
 *     <li>an extra entry inside the pools {@link BuiltInLootTables#ABANDONED_MINESHAFT} already has, which is
 *     what a "roll exactly one thing" table needs.</li>
 * </ul>
 * Registration deliberately happens here, in plain mod init, long before any registry exists - that is the
 * usage the API is designed for.
 * <p>
 * {@link #EXTENDED_TABLE} and {@link #CONTROL_TABLE} exist so {@code LootContainerGameTest} can fill a real
 * chest from an extended table and from an untouched one and tell the two apart. Keep {@link #CONTROL_TABLE}
 * out of every registration below - the moment it is extended, that test stops being a control.
 */
public class TestModWoverLoot implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-loot-testmod");

    /** The vanilla table this testmod extends with a guaranteed {@link #POOL_MARKER} pool. */
    public static final ResourceKey<LootTable> EXTENDED_TABLE = BuiltInLootTables.SIMPLE_DUNGEON;

    /**
     * A vanilla chest table this testmod deliberately does <b>not</b> extend, used as the negative control:
     * a chest filled from it must never see {@link #POOL_MARKER}. Every one of its rolls yields at least two
     * stacks, so "no marker" can be told apart from "the chest never filled".
     */
    public static final ResourceKey<LootTable> CONTROL_TABLE = BuiltInLootTables.STRONGHOLD_CORRIDOR;

    /** Appended to {@link #EXTENDED_TABLE} as its own, always-rolled pool. */
    public static final Item POOL_MARKER = Items.DRAGON_EGG;

    /**
     * Stamped onto every {@link #POOL_MARKER} the appended pool produces. Neither the item nor the name occurs
     * anywhere in vanilla, so a marker found in a container can only have come from this testmod's appender.
     */
    public static final String POOL_MARKER_NAME = "WoVer Loot Appender Marker";

    /** Appended to the existing pools of {@link BuiltInLootTables#ABANDONED_MINESHAFT}. */
    public static final Item ENTRY_MARKER = Items.BEACON;

    /**
     * A vanilla table this testmod gates to "never roll" via {@code addConditionToEveryPool}, then extends
     * with its own always-rolled pool - exactly BetterEnd's "suppress the vanilla pools, add a replacement"
     * shape for its in-End fishing override. {@code LootAppenderGameTest} rolls it to prove the vanilla pools
     * really stopped firing, not just that the marker was added alongside them.
     */
    public static final ResourceKey<LootTable> CONDITIONED_TABLE = BuiltInLootTables.DESERT_PYRAMID;

    /** Appended to {@link #CONDITIONED_TABLE} as its own, always-rolled pool, after its vanilla pools are gated off. */
    public static final Item CONDITION_MARKER = Items.NETHER_STAR;

    /** Stamped onto every {@link #CONDITION_MARKER} the appended pool produces. */
    public static final String CONDITION_MARKER_NAME = "WoVer Loot Condition Marker";

    @Override
    public void onInitialize() {
        LootTableAppenders.addPool(
                C,
                () -> LootPool.lootPool()
                              .setRolls(ConstantValue.exactly(1.0f))
                              .add(LootItem.lootTableItem(POOL_MARKER)
                                           .apply(SetNameFunction.setName(
                                                   Component.literal(POOL_MARKER_NAME),
                                                   SetNameFunction.Target.CUSTOM_NAME
                                           ))),
                EXTENDED_TABLE
        );

        LootTableAppenders.register(
                C,
                BuiltInLootTables.ABANDONED_MINESHAFT,
                table -> table.addToEveryPool(LootItem.lootTableItem(ENTRY_MARKER).setWeight(1))
        );

        LootTableAppenders.register(C, CONDITIONED_TABLE, table -> {
            // Gate every vanilla pool off before adding the replacement, same order BetterEnd's fishing
            // override uses - addConditionToEveryPool only sees the pools present *now*, so the marker pool
            // added afterward is deliberately left unrestricted.
            table.addConditionToEveryPool(LootItemRandomChanceCondition.randomChance(0.0f));
            table.addPool(LootPool
                    .lootPool()
                    .setRolls(ConstantValue.exactly(1.0f))
                    .add(LootItem.lootTableItem(CONDITION_MARKER)
                                 .apply(SetNameFunction.setName(
                                         Component.literal(CONDITION_MARKER_NAME),
                                         SetNameFunction.Target.CUSTOM_NAME
                                 ))));
        });
    }
}
