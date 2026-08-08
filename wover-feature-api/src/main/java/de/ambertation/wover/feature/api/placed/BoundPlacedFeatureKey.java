package de.ambertation.wover.feature.api.placed;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link BasePlacedFeatureKey} whose {@link Feature} is already fixed ("bound"), so unlike
 * {@link PlacedFeatureKey} you do not pass a feature to every {@code place(...)} call.
 * <p>
 * Instances are created with
 * {@link PlacedFeatureManager#createKey(net.minecraft.resources.Identifier, net.minecraft.resources.ResourceKey)}
 * or one of its overloads.
 * <p>
 * This was called {@code PlacedConfiguredFeatureKey} up to 26.1. 26.3 deleted {@code ConfiguredFeature}, so
 * that name referred to a type that no longer exists; the direct translation
 * ({@code PlacedFeatureKey}) was already taken by the sibling interface, so the name now says what actually
 * distinguishes the two.
 */
public interface BoundPlacedFeatureKey extends BasePlacedFeatureKey<BoundPlacedFeatureKey> {
    /**
     * Places the {@link Feature} used for creating this instance
     * from the given {@link BootstrapContext}.
     * <p>
     * This method internally looks up {@link Registries#FEATURE}. If you need to perform
     * a lot of placements, it is recommended to manually lookup the
     * Registry first and use {@link #place(BootstrapContext, HolderGetter)} instead.
     *
     * @param ctx A {@link BootstrapContext} to get the {@link Feature} from
     * @return A {@link FeaturePlacementBuilder} to setup the placement Modifiers
     */
    FeaturePlacementBuilder place(@NotNull BootstrapContext<PlacedFeature> ctx);

    /**
     * Places the {@link Feature} used for creating this instance
     * from the given {@link HolderGetter}.
     * <p>
     * When configuration is finished, you should call {@link FeaturePlacementBuilder#register()}
     * to add it to the registry.
     *
     * @param ctx    A {@link BootstrapContext} to register the {@link PlacedFeature} in
     * @param getter A {@link HolderGetter} for {@link Feature}s
     * @return A {@link FeaturePlacementBuilder} to setup the placement Modifiers
     */
    FeaturePlacementBuilder place(
            @NotNull BootstrapContext<PlacedFeature> ctx,
            @NotNull HolderGetter<Feature> getter
    );
}
