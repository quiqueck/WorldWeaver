package de.ambertation.wover.biome.impl.modification.predicates;

import de.ambertation.wover.biome.api.modification.predicates.BiomePredicate;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Optional;

public record HasConfiguredFeature(ResourceKey<ConfiguredFeature<?, ?>> key) implements BiomePredicate {
    public static final KeyDispatchDataCodec<HasConfiguredFeature> CODEC = KeyDispatchDataCodec
            .of(ResourceKey.codec(Registries.CONFIGURED_FEATURE)
                           .xmap(HasConfiguredFeature::new, HasConfiguredFeature::key)
                           .fieldOf("feature_key")
            );


    @Override
    public KeyDispatchDataCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context ctx) {
        for (HolderSet<PlacedFeature> featuresForStep : ctx.biome.getGenerationSettings().features()) {
            for (Holder<PlacedFeature> holders : featuresForStep) {
                if (holders.value()
                           .getFeatures()
                           .map(Holder::value)
                           .map(ctx.configuredFeatures::getResourceKey)
                           .filter(Optional::isPresent)
                           .map(Optional::get)
                           .anyMatch(fkey -> fkey.equals(key))
                ) {
                    return true;
                }
            }
        }

        return false;
    }
}
