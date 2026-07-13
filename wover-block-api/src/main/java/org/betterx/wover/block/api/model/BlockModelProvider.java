package org.betterx.wover.block.api.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * If a {@link net.minecraft.world.level.block.Block} implements this interface and is registered with a
 * {@link org.betterx.wover.block.api.BlockRegistry}, {@link #provideBlockModels(WoverBlockModelGenerators)}
 * is called during client-side model datagen by
 * {@link org.betterx.wover.datagen.api.provider.WoverModelProvider#addFromRegistry}, unless the block was
 * excluded via {@code WoverModelProvider.ModelOverides}.
 */
@Deprecated(forRemoval = true)
public interface BlockModelProvider {
    /**
     * Provides the blockstate/model files for this block.
     *
     * @param generator The generator helper used to emit blockstate and model files
     */
    @Environment(EnvType.CLIENT)
    void provideBlockModels(WoverBlockModelGenerators generator);
}
