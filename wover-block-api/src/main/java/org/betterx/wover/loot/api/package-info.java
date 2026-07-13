/**
 * Java API for generating block loot tables during datagen.
 * <p>
 * A {@link net.minecraft.world.level.block.Block} that implements
 * {@link org.betterx.wover.loot.api.BlockLootProvider} and is registered with a
 * {@link org.betterx.wover.block.api.BlockRegistry} automatically has its
 * {@link org.betterx.wover.loot.api.BlockLootProvider#registerBlockLoot} called during loot table datagen,
 * passing a {@link org.betterx.wover.loot.api.LootLookupProvider} with vanilla-equivalent building blocks
 * for common drop patterns (silk touch, ore, leaves, crops, slabs, composters, ...).
 * {@link org.betterx.wover.loot.api.LootTableManager} creates the {@link net.minecraft.resources.ResourceKey}s
 * loot tables are stored under.
 */
package org.betterx.wover.loot.api;
