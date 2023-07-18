package org.betterx.wover.tag.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.PackBuilder;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;
import org.betterx.wover.entrypoint.WoverTag;

import net.minecraft.resources.ResourceLocation;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class WoverTagDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.callOnInitializeDatapack(this::onInitGlobalPack);
    }

    private void onInitGlobalPack(
            FabricDataGenerator generator,
            FabricDataGenerator.Pack pack,
            ResourceLocation location
    ) {
        pack.addProvider(BlockTagProvider::new);
        pack.addProvider(BiomeTagProvider::new);
        pack.addProvider(ItemTagProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return WoverTag.C;
    }
}
