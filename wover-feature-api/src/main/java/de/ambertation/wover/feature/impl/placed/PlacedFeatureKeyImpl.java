package de.ambertation.wover.feature.impl.placed;

import de.ambertation.wover.feature.api.configured.ConfiguredFeatureKey;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureManager;
import de.ambertation.wover.feature.api.configured.configurators.FeatureConfigurator;
import de.ambertation.wover.feature.api.placed.FeaturePlacementBuilder;
import de.ambertation.wover.feature.api.placed.PlacedFeatureKey;
import de.ambertation.wover.feature.impl.configured.InlineBuilderImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.NotNull;


public class PlacedFeatureKeyImpl extends BaseFeatureKeyImpl<PlacedFeatureKey> implements PlacedFeatureKey {
    public PlacedFeatureKeyImpl(ResourceLocation featureId) {
        super(featureId);
    }

    @Override
    public FeaturePlacementBuilder place(
            BootstrapContext<PlacedFeature> bootstrapContext,
            ResourceKey<ConfiguredFeature<?, ?>> key
    ) {
        return super.place(
                bootstrapContext,
                ConfiguredFeatureManager.getHolder(bootstrapContext.lookup(Registries.CONFIGURED_FEATURE), key)
        );
    }

    @Override
    public FeaturePlacementBuilder place(
            @NotNull BootstrapContext<PlacedFeature> bootstrapContext,
            Holder<ConfiguredFeature<?, ?>> holder
    ) {
        return super.place(bootstrapContext, holder);
    }

    @Override
    public <B extends FeatureConfigurator<?, ?>> FeaturePlacementBuilder place(
            @NotNull BootstrapContext<PlacedFeature> bootstrapContext,
            ConfiguredFeatureKey<B> key
    ) {
        return super.place(bootstrapContext, key.getHolder(bootstrapContext));
    }

    @Override
    public ConfiguredFeatureManager.InlineBuilder inlineConfiguration(@NotNull BootstrapContext<PlacedFeature> bootstrapContext) {
        return new InlineBuilderImpl(bootstrapContext, this.key);
    }
}
