package de.ambertation.wover{postfix}.{subPackage}.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.PackBuilder;
import de.ambertation.wover.datagen.api.WoverDataGenEntryPoint;
import de.ambertation.wover{postfix}.entrypoint.{mainClass};

public class {datagenClass} extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders(PackBuilder globalPack) {

    }

    @Override
    protected ModCore modCore() {
        return {mainClass}.C;
    }

}
