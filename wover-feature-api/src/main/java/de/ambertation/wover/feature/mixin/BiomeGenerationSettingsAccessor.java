package de.ambertation.wover.feature.mixin;

import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

@Mixin(BiomeGenerationSettings.class)
public interface BiomeGenerationSettingsAccessor {
    @Accessor("features")
    List<HolderSet<PlacedFeature>> wover_getFeatures();

    @Accessor("features")
    @Mutable
    void wover_setFeatures(List<HolderSet<PlacedFeature>> value);

    @Accessor("featureSet")
    void wover_setFeatureSet(Supplier<Set<PlacedFeature>> featureSet);

    // 26.1 renamed BiomeGenerationSettings.flowerFeatures to boneMealFeatures; the accessor method name is
    // kept for API compatibility with downstream callers. 26.3 retyped the field from
    // Supplier<List<ConfiguredFeature<?, ?>>> to Supplier<List<Feature>>, because ConfiguredFeature was
    // folded into Feature - the @Accessor signature has to follow, or it silently hands out the wrong type.
    @Accessor("boneMealFeatures")
    void wover_setFlowerFeatures(Supplier<List<Feature>> flowerFeatures);

    // 26.3: Registries.CONFIGURED_CARVER -> Registries.CARVER, ConfiguredWorldCarver<?> -> WorldCarver
    // (carvers now carry their own configuration, same collapse as ConfiguredFeature -> Feature).
    @Accessor("carvers")
    HolderSet<WorldCarver> wover_getCarvers();

    @Accessor("carvers")
    @Mutable
    void wover_setCarvers(HolderSet<WorldCarver> carvers);
}
