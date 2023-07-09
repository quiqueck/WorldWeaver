package org.betterx.wover.surface.impl.noise;

import org.betterx.wover.surface.api.noise.NoiseParameterManager;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;

public class NoiseRegistryImpl {
    public static ResourceKey<NormalNoise.NoiseParameters> createKey(ResourceLocation loc) {
        return ResourceKey.create(Registries.NOISE, loc);
    }

    private static NormalNoise createNoise(
            Registry<NormalNoise.NoiseParameters> registry,
            RandomSource randomSource,
            ResourceKey<NormalNoise.NoiseParameters> resourceKey
    ) {
        Holder<NormalNoise.NoiseParameters> holder = registry.getHolderOrThrow(resourceKey);
        return NormalNoise.create(randomSource, holder.value());
    }

    private static final Map<ResourceKey<NormalNoise.NoiseParameters>, NormalNoise> noiseIntances = new HashMap<>();

    public static NormalNoise getOrCreateNoise(
            RegistryAccess registryAccess,
            RandomSource randomSource,
            ResourceKey<NormalNoise.NoiseParameters> noise
    ) {
        final Registry<NormalNoise.NoiseParameters> registry = registryAccess.registryOrThrow(Registries.NOISE);
        return noiseIntances.computeIfAbsent(
                noise,
                (key) -> NoiseRegistryImpl.createNoise(registry, randomSource, noise)
        );
    }

    public static void register(
            BootstapContext<NormalNoise.NoiseParameters> bootstapContext,
            ResourceKey<NormalNoise.NoiseParameters> resourceKey,
            int firstOctave,
            double firstAmplitude,
            double... amplitudes
    ) {
        bootstapContext.register(resourceKey, new NormalNoise.NoiseParameters(firstOctave, firstAmplitude, amplitudes));
    }


    @ApiStatus.Internal
    public static void bootstrap(BootstapContext<NormalNoise.NoiseParameters> bootstapContext) {
        register(bootstapContext, NoiseParameterManager.ROUGHNESS_NOISE, 2, 1.0D, 1.0, 1.0, 1.0, 1.0);
    }
}
