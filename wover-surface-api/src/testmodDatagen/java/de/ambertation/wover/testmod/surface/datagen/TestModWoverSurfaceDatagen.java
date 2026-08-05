package de.ambertation.wover.testmod.surface.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover.testmod.entrypoint.TestModWoverSurface;

import net.minecraft.resources.Identifier;

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
            Identifier location
    ) {
        modCore().log.info("Initializing addon datapack: " + location);
    }

}
