package org.betterx.wover.feature.api.placed;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;

public interface PlacedConfiguredFeatureKey extends BasePlacedFeatureKey<PlacedConfiguredFeatureKey> {
    /**
     * Places the  {@link ConfiguredFeature} used for creating this instance
     * from the given {@link BootstapContext}.
     * <p>
     * This method internally looks up {@link Registries#CONFIGURED_FEATURE}. If you need to perform
     * a lot of placements, it is recommended to manually lookup the
     * Registry first and use {@link #place(BootstapContext, HolderGetter)} instead.
     *
     * @param ctx A {@link BootstapContext} to get the {@link ConfiguredFeature} from
     * @return A {@link FeaturePlacementBuilder} to setup the placement Modifiers
     */
    FeaturePlacementBuilder place(@NotNull BootstapContext<PlacedFeature> ctx);

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
            @NotNull BootstapContext<PlacedFeature> ctx,
            @NotNull HolderGetter<ConfiguredFeature<?, ?>> getter
    );
}
