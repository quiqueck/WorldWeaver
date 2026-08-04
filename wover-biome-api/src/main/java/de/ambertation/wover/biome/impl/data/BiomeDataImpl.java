package de.ambertation.wover.biome.impl.data;

import de.ambertation.wover.biome.api.data.BiomeData;
import de.ambertation.wover.biome.api.data.BiomeGenerationDataContainer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import org.jetbrains.annotations.NotNull;

public class BiomeDataImpl {
    public static class CodecAttributes<T extends BiomeData> {
        public RecordCodecBuilder<T, Float> t0 = Codec.FLOAT.optionalFieldOf("fogDensity", 1.0f)
                                                            .forGetter((T o1) -> o1.fogDensity);
        public RecordCodecBuilder<T, ResourceKey<Biome>> t1 =
                ResourceKey.codec(Registries.BIOME).fieldOf("biome")
                           .forGetter((T o) -> o.biomeKey);
        public RecordCodecBuilder<T, BiomeGenerationDataContainer> t2 =
                BiomeGenerationDataContainer.CODEC
                        .optionalFieldOf("generation_data", BiomeGenerationDataContainer.EMPTY)
                        .forGetter(o -> o.generationData);
    }

    public static class InMemoryBiomeData extends BiomeData {

        public InMemoryBiomeData(
                float fogDensity,
                @NotNull ResourceKey<Biome> biome,
                @NotNull BiomeGenerationDataContainer generationData
        ) {
            super(fogDensity, biome, generationData);
        }

        public boolean isTemp() {
            return true;
        }
    }
}
