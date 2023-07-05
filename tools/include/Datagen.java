package org.betterx.wover{postfix}.{subPackage}.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverDataGenEntryPoint;
import org.betterx.wover{postfix}.entrypoint.{mainClass};

public class {datagenClass} extends WoverDataGenEntryPoint {
    @Override
    protected void onInitializeProviders() {

    }

    @Override
    protected ModCore modCore() {
        return {mainClass}.C;
    }

}
