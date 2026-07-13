package org.betterx.wover.generator.api.client.biomesource.client;

import org.betterx.wover.common.generator.api.biomesource.BiomeSourceConfig;
import org.betterx.wover.common.generator.api.biomesource.BiomeSourceWithConfig;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.level.biome.BiomeSource;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.jetbrains.annotations.NotNull;


/**
 * Extends {@link BiomeSourceWithConfig} with a client-side settings panel, letting a
 * {@link org.betterx.wover.common.generator.api.biomesource.BiomeSourceWithSeed BiomeSourceWithSeed} expose
 * its configuration in the world-creation screen. Implemented by both of WoVer's own biome sources
 * ({@link org.betterx.wover.generator.impl.biomesource.nether.WoverNetherBiomeSource
 * WoverNetherBiomeSource}/{@link org.betterx.wover.generator.impl.biomesource.end.WoverEndBiomeSource
 * WoverEndBiomeSource}).
 *
 * @param <B> the {@link BiomeSource} type
 * @param <C> the {@link BiomeSourceConfig} type
 */
public interface BiomeSourceWithConfigScreen<B extends BiomeSource, C extends BiomeSourceConfig<B>> extends BiomeSourceWithConfig<B, C> {
    /**
     * Creates the settings panel used to edit this source's {@link BiomeSourceConfig}.
     *
     * @param parent the screen the panel will be shown from
     * @return the new panel
     */
    @Environment(EnvType.CLIENT)
    BiomeSourceConfigPanel<B, C> biomeSourceConfigPanel(
            @NotNull Screen parent
    );
}
