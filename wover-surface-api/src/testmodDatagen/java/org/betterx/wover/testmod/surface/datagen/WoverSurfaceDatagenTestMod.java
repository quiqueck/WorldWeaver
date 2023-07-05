package org.betterx.wover.testmod.surface.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;
import org.betterx.wover.testmod.entrypoint.WoverSurfaceTestMod;

import net.minecraft.resources.ResourceLocation;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class WoverSurfaceDatagenTestMod extends WoverDataGenEntryPoint {

    @Override
    protected void onInitializeProviders() {
        addRegistryProvider(new SurfaceRuleProvider());
        addRegistryProvider(new AddonSurfaceRuleProvider());

        addDatapackProvider(WoverSurfaceTestMod.ADDON_PACK, this::onInitializeAddonDatapack);
    }

    @Override
    protected ModCore modCore() {
        return WoverSurfaceTestMod.C;
    }

    void onInitializeAddonDatapack(
            FabricDataGenerator fabricDataGenerator,
            FabricDataGenerator.Pack pack,
            ResourceLocation location
    ) {
        modCore().log.info("Initializing addon datapack: " + location);

    }

    @Override
    protected void onInitializeDataGenerator(
            FabricDataGenerator fabricDataGenerator,
            FabricDataGenerator.Pack globalPack
    ) {
        super.onInitializeDataGenerator(fabricDataGenerator, globalPack);
        modCore().log.info("Initializing global datapack");
    }
}
