package org.betterx.wover.feature.impl.placed;

import org.betterx.wover.feature.api.placed.BasePlacedFeatureKey;
import org.betterx.wover.feature.api.placed.FeaturePlacementBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaseFeatureKeyImpl<K extends BasePlacedFeatureKey<K>> implements BasePlacedFeatureKey<K> {
    public final ResourceKey<PlacedFeature> key;

    protected GenerationStep.Decoration decoration = GenerationStep.Decoration.VEGETAL_DECORATION;

    BaseFeatureKeyImpl(ResourceLocation featureId) {
        this.key = ResourceKey.create(Registries.PLACED_FEATURE, featureId);
    }


    @Override
    public GenerationStep.Decoration getDecoration() {
        return decoration;
    }

    @Override
    public K setDecoration(GenerationStep.Decoration decoration) {
        this.decoration = decoration;
        return (K) this;
    }

    @Override
    public ResourceKey<PlacedFeature> key() {
        return null;
    }

    @Nullable
    public Holder<PlacedFeature> getHolder(@Nullable HolderGetter<PlacedFeature> getter) {
        return PlacedFeatureManagerImpl.getHolder(getter, key);
    }

    @Nullable
    public Holder<PlacedFeature> getHolder(@NotNull BootstapContext<?> context) {
        return getHolder(context.lookup(Registries.PLACED_FEATURE));
    }

    protected FeaturePlacementBuilder place(
            @NotNull BootstapContext<PlacedFeature> bootstrapContext,
            Holder<ConfiguredFeature<?, ?>> holder
    ) {
        return new FeaturePlacementBuilderImpl(bootstrapContext, key, holder);
    }
}


