package org.betterx.wover.feature.impl.placed;

import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureManager;
import org.betterx.wover.feature.api.placed.FeaturePlacementBuilder;
import org.betterx.wover.feature.api.placed.PlacedConfiguredFeatureKey;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;

public class PlacedConfiguredFeatureKeyImpl extends BaseFeatureKeyImpl<PlacedConfiguredFeatureKey> implements PlacedConfiguredFeatureKey {
    @FunctionalInterface
    private interface HolderProvider {
        Holder<ConfiguredFeature<?, ?>> getHolder(@NotNull HolderGetter<ConfiguredFeature<?, ?>> getter);
    }

    @NotNull
    private final HolderProvider holderProvider;

    public PlacedConfiguredFeatureKeyImpl(ResourceLocation featureId, ConfiguredFeatureKey<?> linked) {
        super(featureId);
        this.holderProvider = linked::getHolder;
    }

    public PlacedConfiguredFeatureKeyImpl(ResourceLocation featureId, ResourceKey<ConfiguredFeature<?, ?>> linked) {
        super(featureId);
        this.holderProvider = (getter) -> ConfiguredFeatureManager.getHolder(getter, linked);
    }


    @Override
    public FeaturePlacementBuilder place(@NotNull BootstapContext<PlacedFeature> ctx) {
        return this.place(ctx, ctx.lookup(Registries.CONFIGURED_FEATURE));
    }

    @Override
    public FeaturePlacementBuilder place(
            @NotNull BootstapContext<PlacedFeature> ctx,
            @NotNull HolderGetter<ConfiguredFeature<?, ?>> getter
    ) {
        return super.place(ctx, holderProvider.getHolder(getter));
    }
}
