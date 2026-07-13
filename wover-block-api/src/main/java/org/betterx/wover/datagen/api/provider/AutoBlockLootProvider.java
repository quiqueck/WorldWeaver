package org.betterx.wover.datagen.api.provider;

import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverAutoProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * Generates the loot table of every {@link Block} registered with a
 * {@link BlockRegistry} that implements {@link org.betterx.wover.loot.api.BlockLootProvider}.
 * <p>
 * This provider is automatically registered to the global datapack by {@link org.betterx.wover.datagen.api.WoverDataGenEntryPoint}.
 */
public class AutoBlockLootProvider extends WoverLootTableProvider implements WoverAutoProvider {
    /**
     * Creates a new provider for the given mod.
     *
     * @param modCore The mod this provider generates loot tables for
     */
    public AutoBlockLootProvider(
            ModCore modCore
    ) {
        super(modCore, "Auto Block Loot", LootContextParamSets.BLOCK);
    }

    @Override
    protected void boostrap(
            HolderLookup.@NotNull Provider lookup,
            @NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer
    ) {
        BlockRegistry.streamAll().forEach(registry -> registry.bootstrapBlockLoot(lookup, biConsumer));
    }
}
