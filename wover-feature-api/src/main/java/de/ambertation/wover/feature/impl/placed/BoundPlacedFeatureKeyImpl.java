package de.ambertation.wover.feature.impl.placed;

import de.ambertation.wover.feature.api.configured.FeatureContentManager;
import de.ambertation.wover.feature.api.configured.FeatureKey;
import de.ambertation.wover.feature.api.placed.BoundPlacedFeatureKey;
import de.ambertation.wover.feature.api.placed.FeaturePlacementBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;

public class BoundPlacedFeatureKeyImpl extends BaseFeatureKeyImpl<BoundPlacedFeatureKey> implements BoundPlacedFeatureKey {
    @FunctionalInterface
    private interface HolderProvider {
        Holder<Feature> getHolder(@NotNull HolderGetter<Feature> getter);
    }

    @NotNull
    private final HolderProvider holderProvider;

    public BoundPlacedFeatureKeyImpl(Identifier featureId, FeatureKey<?> linked) {
        super(featureId);
        this.holderProvider = linked::getHolder;
    }

    public BoundPlacedFeatureKeyImpl(Identifier featureId, ResourceKey<Feature> linked) {
        super(featureId);
        this.holderProvider = (getter) -> FeatureContentManager.getHolder(getter, linked);
    }


    @Override
    public FeaturePlacementBuilder place(@NotNull BootstrapContext<PlacedFeature> ctx) {
        return this.place(ctx, ctx.lookup(Registries.FEATURE));
    }

    @Override
    public FeaturePlacementBuilder place(
            @NotNull BootstrapContext<PlacedFeature> ctx,
            @NotNull HolderGetter<Feature> getter
    ) {
        return super.place(ctx, holderProvider.getHolder(getter));
    }
}
