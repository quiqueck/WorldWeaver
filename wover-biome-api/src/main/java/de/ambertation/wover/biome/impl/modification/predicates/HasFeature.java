package de.ambertation.wover.biome.impl.modification.predicates;

import de.ambertation.wover.biome.api.modification.predicates.BiomePredicate;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Optional;

/**
 * Tests whether any {@link PlacedFeature} of the Biome resolves to the {@link Feature} with the given key.
 * <p>
 * 26.3 folded {@code ConfiguredFeature} into {@link Feature} and renamed the registry
 * {@code Registries.CONFIGURED_FEATURE} to {@link Registries#FEATURE}. This class was called
 * {@code HasConfiguredFeature} and its serialized type was {@code wover:has_configured_feature}; both
 * were named after a vanilla type that no longer exists, so both follow the vanilla rename. The name now
 * also lines up with its sibling {@link HasPlacedFeature}, mirroring vanilla's
 * {@code FEATURE} / {@code PLACED_FEATURE} pair.
 */
public record HasFeature(ResourceKey<Feature> key) implements BiomePredicate {
    public static final MapCodec<HasFeature> CODEC = ResourceKey.codec(Registries.FEATURE)
            .xmap(HasFeature::new, HasFeature::key)
            .fieldOf("feature_key");


    @Override
    public MapCodec<? extends BiomePredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean test(Context ctx) {
        for (HolderSet<PlacedFeature> featuresForStep : ctx.biome.getGenerationSettings().features()) {
            for (Holder<PlacedFeature> holders : featuresForStep) {
                if (holders.value()
                           .getFeatures()
                           .map(Holder::value)
                           .map(ctx.features::getResourceKey)
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
