package org.betterx.wover.surface.datagen;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.api.WoverFullRegistryProvider;
import org.betterx.wover.surface.impl.noise.NoiseRegistryImpl;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoiseRegistryProvider extends WoverFullRegistryProvider<NormalNoise.NoiseParameters> {
    public NoiseRegistryProvider(ModCore modCore) {
        super(modCore, "Noise Registry Provider", Registries.NOISE);
    }

    @Override
    protected void bootstrap(BootstapContext<NormalNoise.NoiseParameters> ctx) {
        NoiseRegistryImpl.bootstrap(ctx);
    }
}
