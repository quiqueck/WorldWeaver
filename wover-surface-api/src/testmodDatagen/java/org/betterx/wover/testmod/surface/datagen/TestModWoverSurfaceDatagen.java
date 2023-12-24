package org.betterx.wover.testmod.surface.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.PackBuilder;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;
import org.betterx.wover.testmod.entrypoint.TestModWoverSurface;

import net.minecraft.resources.ResourceLocation;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class TestModWoverSurfaceDatagen extends WoverDataGenEntryPoint {

    @Override
    protected void onInitializeProviders(PackBuilder globalPackBuilder) {
        globalPackBuilder
                .addRegistryProvider(SurfaceRuleProvider::new);

        addDatapack(TestModWoverSurface.ADDON_PACK)
                .callOnInitializeDatapack(this::onInitializeAddonDatapack)
                .addRegistryProvider(AddonSurfaceRuleProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return TestModWoverSurface.C;
    }

    void onInitializeAddonDatapack(
            FabricDataGenerator fabricDataGenerator,
            FabricDataGenerator.Pack pack,
            ResourceLocation location
    ) {
        modCore().log.info("Initializing addon datapack: " + location);
    }

}
