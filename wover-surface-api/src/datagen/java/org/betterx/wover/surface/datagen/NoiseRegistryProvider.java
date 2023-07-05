package org.betterx.wover.surface.datagen;

import org.betterx.wover.datagen.api.WoverFullRegistryProvider;
import org.betterx.wover.entrypoint.WoverSurface;
import org.betterx.wover.surface.api.noise.NoiseRegistry;
import org.betterx.wover.surface.impl.noise.NoiseRegistryImpl;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoiseRegistryProvider extends WoverFullRegistryProvider<NormalNoise.NoiseParameters> {
    public NoiseRegistryProvider() {
        super(WoverSurface.C, "Noise Registry Provider", NoiseRegistry.NOISE_REGISTRY);
    }

    @Override
    protected void bootstrap(BootstapContext<NormalNoise.NoiseParameters> ctx) {
        NoiseRegistryImpl.bootstrap(ctx);
    }
}
