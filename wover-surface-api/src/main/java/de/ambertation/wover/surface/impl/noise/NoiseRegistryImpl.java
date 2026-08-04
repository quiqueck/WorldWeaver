package de.ambertation.wover.surface.impl.noise;

import de.ambertation.wover.surface.api.noise.NoiseParameterManager;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.HashMap;
import org.jetbrains.annotations.ApiStatus;

public class NoiseRegistryImpl {
    public static ResourceKey<NormalNoise.NoiseParameters> createKey(ResourceLocation loc) {
        return ResourceKey.create(Registries.NOISE, loc);
    }

    public static void register(
            BootstrapContext<NormalNoise.NoiseParameters> bootstapContext,
            ResourceKey<NormalNoise.NoiseParameters> resourceKey,
            int firstOctave,
            double firstAmplitude,
            double... amplitudes
    ) {
        bootstapContext.register(resourceKey, new NormalNoise.NoiseParameters(firstOctave, firstAmplitude, amplitudes));
    }


    @ApiStatus.Internal
    public static void bootstrap(BootstrapContext<NormalNoise.NoiseParameters> bootstapContext) {
        register(bootstapContext, NoiseParameterManager.ROUGHNESS_NOISE, 2, 1.0D, 1.0, 1.0, 1.0, 1.0);
    }
}
