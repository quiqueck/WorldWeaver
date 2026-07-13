package org.betterx.wover.entrypoint;

import org.betterx.wover.block.impl.trait.behaviour.AutoBlockTraitLootProvider;
import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;

import net.fabricmc.api.ModInitializer;

public class LibWoverSets implements ModInitializer {
    public static final ModCore C = ModCore.create("wover-sets", "wover");

    @Override
    public void onInitialize() {
        //make sure the Datagen will automatically generate loot tables for all Blocks
        //that were registered with a LootTableTrait
        WoverDataGenEntryPoint.registerAutoProvider(AutoBlockTraitLootProvider::new);
    }
}