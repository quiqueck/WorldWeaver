package de.ambertation.wover.loot.api;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.loot.impl.LootTableAppendersImpl;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

/**
 * Lets a mod add pools and entries to loot tables it does not own - vanilla's chest, entity and gameplay
 * tables, or tables shipped by another mod.
 * <p>
 * Fabric's own {@code fabric-loot-api-v3} ({@code LootTableEvents.MODIFY}) already does exactly this for a
 * single, code-only addition. This class exists for two reasons: it is the same mechanism
 * {@link LootAdditionFile} runs a datapack addition through, in the very same idempotent pass, so a mod that
 * needs both forms does not stack two independent systems; and it keeps working unchanged on later Minecraft
 * versions where Fabric no longer ships a loot module at all. Registrations are collected here at mod-init
 * time and applied by {@code de.ambertation.wover.loot.mixin.ReloadableServerRegistriesMixin} while the
 * reloadable loot table registry is being rebuilt, right before vanilla validates it - so a malformed addition
 * fails loudly at load time instead of at loot-roll time.
 * <p>
 * <b>Prefer the data form for anything static.</b> {@link LootAdditionFile} expresses exactly these verbs as
 * {@code data/<namespace>/wover/loot_addition/<name>.json}, is emitted from datagen by
 * {@link de.ambertation.wover.datagen.api.provider.WoverLootAdditionProvider}, and runs through this very same
 * code in this very same pass. It is inspectable, gateable on another mod being present, and a pack author can
 * override it. This class stays for additions that genuinely have to be computed at runtime. Java appenders
 * are applied first and the datapack additions afterwards, so a pack always has the last word.
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * public static void register() {
 *     LootTableAppenders.register(
 *             MyMod.C,
 *             table -> table.addPool(LootPool.lootPool()
 *                                            .setRolls(UniformGenerator.between(0, 4))
 *                                            .add(LootItem.lootTableItem(MyBlocks.SHINY.asItem()).setWeight(1))
 *                                            .add(EmptyLootItem.emptyItem().setWeight(9))),
 *             BuiltInLootTables.RUINED_PORTAL, BuiltInLootTables.NETHER_BRIDGE
 *     );
 * }
 * }</pre>
 *
 * <h2>Guarantees</h2>
 * <ul>
 *     <li><b>Registration is init-time safe.</b> Nothing is looked up when you register; the
 *     {@link LootTableAppender} only runs once a server is loading its data. Calling this from your
 *     {@link net.fabricmc.api.ModInitializer} is the intended usage.</li>
 *     <li><b>Several mods may extend the same table.</b> Additions are never replacements.</li>
 *     <li><b>The order is deterministic and independent of mod load order:</b> appenders are applied sorted
 *     by {@link ModCore#modId}, and within one mod in registration order.</li>
 *     <li><b>Applying is idempotent.</b> The loot registry is rebuilt from scratch on every {@code /reload}
 *     and every world load, and each pass re-derives the table from the pools the datapacks provided rather
 *     than from whatever the previous pass left behind. A {@code /reload} therefore never stacks duplicate
 *     pools, no matter how often it happens.</li>
 * </ul>
 */
public final class LootTableAppenders {
    private LootTableAppenders() {
    }

    /**
     * Registers an appender for a single loot table.
     *
     * @param mod      the mod that owns the addition; used for the deterministic ordering and for log output
     * @param table    the table to extend
     * @param appender the addition to make
     */
    public static void register(ModCore mod, ResourceKey<LootTable> table, LootTableAppender appender) {
        LootTableAppendersImpl.register(mod, appender, table);
    }

    /**
     * Registers the same appender for several loot tables.
     *
     * @param mod      the mod that owns the addition; used for the deterministic ordering and for log output
     * @param appender the addition to make
     * @param tables   the tables to extend
     */
    @SafeVarargs
    public static void register(ModCore mod, LootTableAppender appender, ResourceKey<LootTable>... tables) {
        LootTableAppendersImpl.register(mod, appender, tables);
    }

    /**
     * Convenience for the most common case: append one additional pool to each of the given tables.
     * <p>
     * The {@link Supplier} is invoked once per table and per reload, so it must build a fresh
     * {@link LootPool.Builder} on every call.
     *
     * @param mod    the mod that owns the addition; used for the deterministic ordering and for log output
     * @param pool   builds the pool to append
     * @param tables the tables to extend
     */
    @SafeVarargs
    public static void addPool(ModCore mod, Supplier<LootPool.Builder> pool, ResourceKey<LootTable>... tables) {
        LootTableAppendersImpl.register(mod, table -> table.addPool(pool.get()), tables);
    }
}
