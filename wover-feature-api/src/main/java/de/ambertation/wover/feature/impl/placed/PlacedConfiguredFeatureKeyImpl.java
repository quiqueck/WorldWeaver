package de.ambertation.wover.feature.impl.placed;

import de.ambertation.wover.feature.api.configured.ConfiguredFeatureKey;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureManager;
import de.ambertation.wover.feature.api.placed.FeaturePlacementBuilder;
import de.ambertation.wover.feature.api.placed.PlacedConfiguredFeatureKey;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
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

    public PlacedConfiguredFeatureKeyImpl(Identifier featureId, ConfiguredFeatureKey<?> linked) {
        super(featureId);
        this.holderProvider = linked::getHolder;
    }

    public PlacedConfiguredFeatureKeyImpl(Identifier featureId, ResourceKey<ConfiguredFeature<?, ?>> linked) {
        super(featureId);
        this.holderProvider = (getter) -> ConfiguredFeatureManager.getHolder(getter, linked);
    }


    @Override
    public FeaturePlacementBuilder place(@NotNull BootstrapContext<PlacedFeature> ctx) {
        return this.place(ctx, ctx.lookup(Registries.CONFIGURED_FEATURE));
    }

    @Override
    public FeaturePlacementBuilder place(
            @NotNull BootstrapContext<PlacedFeature> ctx,
            @NotNull HolderGetter<ConfiguredFeature<?, ?>> getter
    ) {
        return super.place(ctx, holderProvider.getHolder(getter));
    }
}
