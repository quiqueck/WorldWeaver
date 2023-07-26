package org.betterx.wover.biome.impl.modification.predicates;

import org.betterx.wover.biome.api.modification.predicates.BiomePredicate;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public record HasPlacedFeature(ResourceKey<PlacedFeature> key) implements BiomePredicate {
    public static final KeyDispatchDataCodec<HasPlacedFeature> CODEC = KeyDispatchDataCodec
            .of(ResourceKey.codec(Registries.PLACED_FEATURE)
                           .xmap(HasPlacedFeature::new, HasPlacedFeature::key)
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
                var optionalKey = ctx.placedFeatures.getResourceKey(holders.value());
                if (optionalKey.map(fkey -> fkey.equals(key)).orElse(false)) {
                    return true;
                }
            }
        }

        return false;
    }
}