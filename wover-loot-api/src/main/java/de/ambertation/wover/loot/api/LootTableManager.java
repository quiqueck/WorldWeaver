package de.ambertation.wover.loot.api;

import de.ambertation.wover.core.api.ModCore;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

/**
 * Static helpers for creating the {@link ResourceKey}s that identify {@link LootTable}s, in particular the
 * vanilla convention of storing block loot tables under the {@code blocks/} path prefix.
 */
public class LootTableManager {
    /**
     * Gets the loot table key for a block, following the vanilla {@code <namespace>:blocks/<path>} convention.
     *
     * @param blockKey The resource key of the block
     * @return The loot table's resource key
     */
    public static ResourceKey<LootTable> getBlockLootTableKey(ResourceKey<Block> blockKey) {
        return ResourceKey.create(Registries.LOOT_TABLE, blockKey.identifier().withPrefix("blocks/"));
    }

    /**
     * Gets the loot table key for a block identified by its location, following the vanilla
     * {@code <namespace>:blocks/<path>} convention.
     *
     * @param modCore The mod owning the block (unused, kept for API symmetry/future use)
     * @param blockId The location of the block
     * @return The loot table's resource key
     */
    public static ResourceKey<LootTable> getBlockLootTableKey(ModCore modCore, Identifier blockId) {
        return ResourceKey.create(Registries.LOOT_TABLE, blockId.withPrefix("blocks/"));
    }

    /**
     * Creates an arbitrary loot table key under the given mod's namespace, without any path prefix.
     *
     * @param modCore The mod that owns the loot table
     * @param name    The path of the loot table
     * @return The loot table's resource key
     */
    public static ResourceKey<LootTable> createLootTableKey(ModCore modCore, String name) {
        return ResourceKey.create(Registries.LOOT_TABLE, modCore.mk(name));
    }
}
