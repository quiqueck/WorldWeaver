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
 *
 * <h2>Cross-branch contract</h2>
 * This module's signatures are deliberately identical to the 26.1 / 26.2 / 26.3 branches, so that a mod's loot
 * code compiles unchanged on all four. The drift that used to exist here - 1.21.x reinventing loot injection
 * against {@code fabric-loot-api} while 26.x used this module - is the reason the module exists on this branch
 * at all, so <b>do not "simplify" any of it back onto {@code LootTableEvents.MODIFY}</b>; see
 * {@code de.ambertation.wover.loot.mixin.ReloadableServerRegistriesMixin} for what that would cost.
 * <p>
 * Only three signature differences remain, and every one of them is a vanilla or Fabric rename that affects
 * the whole codebase, not this module:
 * <ul>
 *     <li>{@link net.minecraft.resources.ResourceLocation} is called {@code Identifier} on 26.x (and
 *     {@code ResourceKey.location()} is {@code ResourceKey.identifier()} there). This shows up in
 *     {@link de.ambertation.wover.loot.api.LootTableManager#getBlockLootTableKey(de.ambertation.wover.core.api.ModCore, net.minecraft.resources.ResourceLocation)}
 *     and in the {@code BiConsumer} that
 *     {@link de.ambertation.wover.datagen.api.provider.WoverLootAdditionProvider#bootstrap} is handed.</li>
 *     <li>{@code FabricDataOutput} is called {@code FabricPackOutput} on 26.x. This is already true of every
 *     other {@code Wover*Provider}, because it comes from
 *     {@link de.ambertation.wover.datagen.api.WoverDataProvider} - overriding {@code bootstrap} rather than
 *     {@code getProvider} keeps a mod insulated from it.</li>
 *     <li>{@code net.minecraft.advancements.critereon} is {@code net.minecraft.advancements.predicates} on
 *     26.x, which surfaces in {@link de.ambertation.wover.loot.api.LootLookupProvider}'s condition helpers.</li>
 * </ul>
 * Everything else - class names, method names, parameter order, return types - matches 26.2 exactly.
 */
package de.ambertation.wover.loot.api;
