package org.betterx.wover.testmod.sets.datagen;

import org.betterx.wover.block.api.trait.behaviour.LootTableTrait;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.provider.WoverLootTableProvider;
import org.betterx.wover.loot.api.LootLookupProvider;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;

public class TestLootProvider extends WoverLootTableProvider {

    public TestLootProvider(ModCore modCore) {
        super(modCore, LootContextParamSets.BLOCK);
    }

    @Override
    protected void boostrap(
            HolderLookup.@NotNull Provider lookup,
            @NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer
    ) {

        LootLookupProvider provider = new LootLookupProvider(lookup);
        LootTableTrait.bootstrapLootTables(this.modCore, provider, biConsumer);
    }
}
