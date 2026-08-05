package de.ambertation.wover.feature.impl.placed;

import de.ambertation.wover.feature.api.placed.BasePlacedFeatureKey;
import de.ambertation.wover.feature.api.placed.FeaturePlacementBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaseFeatureKeyImpl<K extends BasePlacedFeatureKey<K>> implements BasePlacedFeatureKey<K> {
    public final ResourceKey<PlacedFeature> key;

    protected GenerationStep.Decoration decoration = GenerationStep.Decoration.VEGETAL_DECORATION;

    BaseFeatureKeyImpl(Identifier featureId) {
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
    public Holder<PlacedFeature> getHolder(@Nullable RegistryAccess access) {
        if (access == null) {
            return null;
        }
        return this.getHolder(access.lookupOrThrow(Registries.PLACED_FEATURE));
    }

    @Nullable
    public Holder<PlacedFeature> getHolder(@NotNull BootstrapContext<?> context) {
        return getHolder(context.lookup(Registries.PLACED_FEATURE));
    }

    protected FeaturePlacementBuilder place(
            @NotNull BootstrapContext<PlacedFeature> bootstrapContext,
            Holder<ConfiguredFeature<?, ?>> holder
    ) {
        return new FeaturePlacementBuilderImpl(bootstrapContext, key, holder);
    }
}


