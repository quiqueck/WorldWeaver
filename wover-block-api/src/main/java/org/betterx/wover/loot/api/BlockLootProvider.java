package org.betterx.wover.loot.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;

import org.jetbrains.annotations.NotNull;

/**
 * If a {@link net.minecraft.world.level.block.Block} implements this interface and is registered with a
 * {@link org.betterx.wover.block.api.BlockRegistry}, {@link #registerBlockLoot} is called during loot table
 * datagen by {@link org.betterx.wover.block.api.BlockRegistry#bootstrapBlockLoot}, normally invoked
 * automatically via {@link org.betterx.wover.datagen.api.provider.AutoBlockLootProvider}.
 */
@Deprecated(forRemoval = true)
public interface BlockLootProvider {
    /**
     * Builds the loot table for this block.
     *
     * @param location The location of the block as it was registered with the {@link org.betterx.wover.block.api.BlockRegistry}
     * @param provider Helper with vanilla-equivalent loot table building blocks
     * @param tableKey The resource key the loot table will be written to
     * @return The loot table builder, or {@code null} to skip generating a loot table for this block
     */
    LootTable.Builder registerBlockLoot(
            @NotNull ResourceLocation location,
            @NotNull LootLookupProvider provider,
            @NotNull ResourceKey<LootTable> tableKey
    );
}
