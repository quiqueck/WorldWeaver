package de.ambertation.wover.surface.datagen;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverFullRegistryProvider;
import de.ambertation.wover.surface.impl.noise.NoiseRegistryImpl;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoiseRegistryProvider extends WoverFullRegistryProvider<NormalNoise> {
    public NoiseRegistryProvider(ModCore modCore) {
        super(modCore, "Noise Registry Provider", Registries.NOISE);
    }

    @Override
    protected void bootstrap(BootstrapContext<NormalNoise> ctx) {
        NoiseRegistryImpl.bootstrap(ctx);
    }
}
