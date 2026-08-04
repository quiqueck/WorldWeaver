package de.ambertation.wover.block.impl.trait.behaviour;

import de.ambertation.wover.block.api.trait.behaviour.LootTableTrait;
import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverAutoProvider;
import de.ambertation.wover.datagen.api.provider.WoverLootTableProvider;
import de.ambertation.wover.loot.api.LootLookupProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * Generates loot tables for every Block that was registered with a
 * {@link de.ambertation.wover.block.api.trait.behaviour.LootTableTrait}.
 * <p>
 * This provider is automatically registered to the global datapack by {@link de.ambertation.wover.datagen.api.WoverDataGenEntryPoint}.
 */
public class AutoBlockTraitLootProvider extends WoverLootTableProvider implements WoverAutoProvider {
    public AutoBlockTraitLootProvider(ModCore modCore) {
        super(modCore, "Auto Block Trait Loot", LootContextParamSets.BLOCK);
    }

    @Override
    protected void boostrap(
            HolderLookup.@NotNull Provider lookup,
            @NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer
    ) {
        LootTableTrait.bootstrapLootTables(modCore, new LootLookupProvider(lookup), biConsumer);
    }
}
