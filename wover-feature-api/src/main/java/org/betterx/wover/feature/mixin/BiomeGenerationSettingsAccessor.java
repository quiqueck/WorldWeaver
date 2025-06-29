package org.betterx.wover.feature.mixin;

import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BiomeGenerationSettings.class)
public interface BiomeGenerationSettingsAccessor {
//    @Accessor("features")
//    List<HolderSet<PlacedFeature>> wover_getFeatures();
//
//    @Accessor("features")
//    @Mutable
//    void wover_setFeatures(List<HolderSet<PlacedFeature>> value);
//
//    @Accessor("featureSet")
//    void wover_setFeatureSet(Supplier<Set<PlacedFeature>> featureSet);
//
//    @Accessor("flowerFeatures")
//    void wover_setFlowerFeatures(Supplier<List<ConfiguredFeature<?, ?>>> flowerFeatures);

    @Accessor("carvers")
    HolderSet<ConfiguredWorldCarver<?>> wover_getCarvers();

    @Accessor("carvers")
    @Mutable
    void wover_setCarvers(HolderSet<ConfiguredWorldCarver<?>> carvers);
}
