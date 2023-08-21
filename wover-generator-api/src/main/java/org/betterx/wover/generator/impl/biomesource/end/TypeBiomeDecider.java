package org.betterx.wover.generator.impl.biomesource.end;

import org.betterx.wover.generator.api.biomesource.end.BiomeDecider;

import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public abstract class TypeBiomeDecider extends BiomeDecider {
    protected final TagKey<Biome> assignedType;

    public TypeBiomeDecider(TagKey<Biome> assignedType) {
        this(null, null, assignedType);
    }

    protected TypeBiomeDecider(
            HolderGetter<Biome> biomeRegistry,
            ResourceKey<Biome> fallbackBiome,
            TagKey<Biome> assignedType
    ) {
        super(biomeRegistry, fallbackBiome, (biome) -> biomeRegistry.getOrThrow(biome.biomeKey).is(assignedType));
        this.assignedType = assignedType;
    }

    @Override
    public boolean canProvideBiome(TagKey<Biome> suggestedType) {
        return suggestedType.equals(assignedType);
    }
}
