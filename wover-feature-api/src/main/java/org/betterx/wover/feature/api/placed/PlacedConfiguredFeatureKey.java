package org.betterx.wover.feature.api.placed;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;

/**
 * A {@link BasePlacedFeatureKey} that places one specific, already known {@link ConfiguredFeature}.
 * <p>
 * Instances are created with {@link PlacedFeatureManager#createKey(net.minecraft.resources.ResourceLocation, net.minecraft.resources.ResourceKey)}
 * or one of its overloads, so unlike {@link PlacedFeatureKey} you do not need to pass a
 * {@link ConfiguredFeature} reference to every {@code place(...)} call.
 */
public interface PlacedConfiguredFeatureKey extends BasePlacedFeatureKey<PlacedConfiguredFeatureKey> {
    /**
     * Places the  {@link ConfiguredFeature} used for creating this instance
     * from the given {@link BootstrapContext}.
     * <p>
     * This method internally looks up {@link Registries#CONFIGURED_FEATURE}. If you need to perform
     * a lot of placements, it is recommended to manually lookup the
     * Registry first and use {@link #place(BootstrapContext, HolderGetter)} instead.
     *
     * @param ctx A {@link BootstrapContext} to get the {@link ConfiguredFeature} from
     * @return A {@link FeaturePlacementBuilder} to setup the placement Modifiers
     */
    FeaturePlacementBuilder place(@NotNull BootstrapContext<PlacedFeature> ctx);

    /**
     * Places the  {@link ConfiguredFeature} used for creating this instance
     * from the given {@link HolderGetter}.
     * <p>
     * When configuration is finished, you should call {@link FeaturePlacementBuilder#register()}
     * to add it to the registry.
     *
     * @param getter A {@link HolderGetter} for {@link ConfiguredFeature}s
     * @return A {@link FeaturePlacementBuilder} to setup the placement Modifiers
     */
    FeaturePlacementBuilder place(
            @NotNull BootstrapContext<PlacedFeature> ctx,
            @NotNull HolderGetter<ConfiguredFeature<?, ?>> getter
    );
}
