package de.ambertation.wover.generator.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover.entrypoint.LibWoverWorldGenerator;

public class WoverWorldGeneratorDatagen extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {
        globalPack.addRegistryProvider(WorldPresetProvider::new);
        globalPack.addRegistryProvider(NoiseGeneratorSettingsProvider::new);
        globalPack.addMultiProvider(VanillaBiomeDataProvider::new);
        globalPack.addRegistryProvider(WorldPresetInfoProvider::new);
    }

    @Override
    protected ModCore modCore() {
        return LibWoverWorldGenerator.C;
    }

}
