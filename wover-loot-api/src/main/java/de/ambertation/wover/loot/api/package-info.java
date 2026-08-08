/**
 * Java API for loot tables.
 * <p>
 * <b>Datagen.</b> {@link de.ambertation.wover.loot.api.LootLookupProvider} provides vanilla-equivalent
 * building blocks for common drop patterns (silk touch, ore, leaves, crops, slabs, composters, ...), passed to
 * loot table factories during datagen. {@link de.ambertation.wover.loot.api.LootTableManager} creates the
 * {@link net.minecraft.resources.ResourceKey}s loot tables are stored under.
 * <p>
 * <b>Runtime.</b> {@link de.ambertation.wover.loot.api.LootTableAppenders} adds pools and entries to loot
 * tables that already exist - vanilla's chest and gameplay tables, or another mod's - which 26.3 otherwise
 * makes impossible: loot tables became a datapack registry, where an entry is replaced wholesale and never
 * merged. Registrations are collected at mod-init time and applied through a
 * {@link de.ambertation.wover.loot.api.LootTableAppender} while the {@code minecraft:loot_table} registry is
 * being built, just before vanilla validates it.
 * <p>
 * <b>Data.</b> {@link de.ambertation.wover.loot.api.LootAdditionFile} is the same thing in data:
 * {@code data/<namespace>/wover/loot_addition/<name>.json}, loaded from the datapacks with the merge semantics
 * of a vanilla tag file and applied through the very same verbs, in the very same pass. Prefer it - a static
 * addition written as data is inspectable, gateable on another mod through Fabric's resource conditions, and
 * overridable by pack authors. Emit it from datagen with
 * {@link de.ambertation.wover.datagen.api.provider.WoverLootAdditionProvider}, and keep the Java API for
 * additions that are genuinely dynamic.
 */
package de.ambertation.wover.loot.api;
