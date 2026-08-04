package de.ambertation.wover.generator.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverRegistryContentProvider;
import de.ambertation.wover.generator.impl.chunkgenerator.WoverChunkGenerator;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class NoiseGeneratorSettingsProvider extends WoverRegistryContentProvider<NoiseGeneratorSettings> {
    /**
     * Creates a new instance of {@link WoverRegistryContentProvider}.
     *
     * @param modCore The ModCore instance of the Mod that is providing this instance.
     */
    public NoiseGeneratorSettingsProvider(
            ModCore modCore
    ) {
        super(modCore, "Noise Generator Settings", Registries.NOISE_SETTINGS);
    }

    @Override
    protected void bootstrap(BootstrapContext<NoiseGeneratorSettings> bootstrapContext) {
        bootstrapContext.register(
                WoverChunkGenerator.AMPLIFIED_NETHER,
                WoverChunkGenerator.amplifiedNether(bootstrapContext)
        );
    }
}
