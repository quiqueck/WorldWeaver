package de.ambertation.wover.surface.impl.noise;

import de.ambertation.wover.surface.api.noise.NoiseParameterManager;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.levelgen.synth.Noise;

import org.jetbrains.annotations.ApiStatus;

public class NoiseRegistryImpl {
    public static ResourceKey<NormalNoise> createKey(Identifier loc) {
        return ResourceKey.create(Registries.NOISE, loc);
    }

    // 26.3 replaced NormalNoise.NoiseParameters(firstOctave, firstAmplitude, amplitudes) with the
    // NormalNoise.Parameters record (baseOctave, baseAmplitude, octaveCount, amplitudeModifiers) -
    // amplitudes maps 1:1 onto amplitudeModifiers, same as vanilla's data/minecraft/worldgen/noise/*.json.
    public static void register(
            BootstrapContext<NormalNoise> bootstapContext,
            ResourceKey<NormalNoise> resourceKey,
            int firstOctave,
            double firstAmplitude,
            double... amplitudes
    ) {
        NormalNoise.Builder builder = NormalNoise.builder()
                                                  .setBaseOctave(firstOctave)
                                                  .setBaseAmplitude(firstAmplitude)
                                                  .setOctaveCount(amplitudes.length);
        for (int i = 0; i < amplitudes.length; i++) {
            builder.setAmplitudeModifier(i, amplitudes[i]);
        }
        bootstapContext.register(resourceKey, builder.build());
    }


    @ApiStatus.Internal
    public static void bootstrap(BootstrapContext<NormalNoise> bootstapContext) {
        register(bootstapContext, NoiseParameterManager.ROUGHNESS_NOISE, 2, 1.0D, 1.0, 1.0, 1.0, 1.0);
    }
}
