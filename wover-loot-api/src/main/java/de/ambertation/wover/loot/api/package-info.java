/**
 * Java API for loot tables.
 * <p>
 * <b>Datagen.</b> {@link de.ambertation.wover.loot.api.LootLookupProvider} provides vanilla-equivalent
 * building blocks for common drop patterns (silk touch, ore, leaves, crops, slabs, composters, ...), passed to
 * loot table factories during datagen. {@link de.ambertation.wover.loot.api.LootTableManager} creates the
 * {@link net.minecraft.resources.ResourceKey}s loot tables are stored under.
 * <p>
 * <b>Runtime.</b> {@link de.ambertation.wover.loot.api.LootTableAppenders} adds pools and entries to loot
 * tables that already exist - vanilla's chest and gameplay tables, or another mod's. Fabric's own
 * {@code fabric-loot-api-v3} ({@code LootTableEvents.MODIFY}) already does this and remains the simpler choice
 * for a single, code-only addition; this module exists so the same registration also works from a datapack,
 * and so mods that need both forms share one idempotent application pass instead of stacking two independent
 * mechanisms. Registrations are collected at mod-init time and applied through a
 * {@link de.ambertation.wover.loot.api.LootTableAppender} while the reloadable loot table registry is being
 * rebuilt, just before vanilla validates it.
 * <p>
 * <b>Data.</b> {@link de.ambertation.wover.loot.api.LootAdditionFile} is the same thing in data:
 * {@code data/<namespace>/wover/loot_addition/<name>.json}, loaded from the datapacks with the merge semantics
 * of a vanilla tag file and applied through the very same verbs, in the very same pass. Nothing in Fabric's
 * own loot API offers a datapack route - this is the capability {@code LootTableEvents.MODIFY} does not have.
 * Emit it from datagen with {@link de.ambertation.wover.datagen.api.provider.WoverLootAdditionProvider}, and
 * keep the Java API for additions that are genuinely dynamic.
 */
package de.ambertation.wover.loot.api;
