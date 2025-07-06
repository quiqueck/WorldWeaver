package org.betterx.wover.block.api.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

//TODO: @Deprecated(forRemoval = true)
public interface BlockModelProvider {
    @Environment(EnvType.CLIENT)
    void provideBlockModels(WoverBlockModelGenerators generator);
}
