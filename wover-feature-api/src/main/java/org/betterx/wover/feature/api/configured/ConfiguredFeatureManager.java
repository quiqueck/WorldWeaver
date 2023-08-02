package org.betterx.wover.feature.api.configured;

import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.feature.api.configured.builders.*;
import org.betterx.wover.feature.api.placed.PlacedFeatureKey;
import org.betterx.wover.feature.impl.configured.*;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ConfiguredFeatureManager {
    /**
     * The event that is fired when the Registry for a {@link ConfiguredFeature}
     * is being bootstrapped. In general, it is best to generate presets
     * in the data generator whenever possible (see WoverRegistryProvider)
     * for Details.
     */
    public static final Event<OnBootstrapRegistry<ConfiguredFeature<?, ?>>> BOOTSTRAP_CONFIGURED_FEATURES = FeatureConfiguratorImpl.BOOTSTRAP_CONFIGURED_FEATURES;

    /**
     * A builder to create an anonymous (or inline) {@link ConfiguredFeature}s.
     * <p>
     * You can use the result from an inline builder (after calling {@link FeatureConfigurator#directHolder()})
     * in {@link PlacedFeatureKey#place(Holder)}
     * <p>
     * However, you should not normally need to use this, as you can create inline configurations using
     * {@link PlacedFeatureKey#inlineConfiguration()}.
     * <p>
     */
    public final static InlineBuilder INLINE_BUILDER = new InlineBuilderImpl();

    public static ConfiguredFeatureKey<ForSimpleBlock> simple(ResourceLocation id) {
        return new ForSimpleBlockImpl.Key(id);
    }

    public static ConfiguredFeatureKey<RandomPatch> randomPatch(ResourceLocation id) {
        return new RandomPatchImpl.Key(id);
    }

    public static ConfiguredFeatureKey<AsOre> ore(ResourceLocation id) {
        return new AsOreImpl.Key(id);
    }

    public static ConfiguredFeatureKey<AsPillar> pillar(ResourceLocation id) {
        return new AsPillarImpl.Key(id);
    }

    public static ConfiguredFeatureKey<AsSequence> sequence(ResourceLocation id) {
        return new AsSequenceImpl.Key(id);
    }

    public static ConfiguredFeatureKey<AsBlockColumn> blockColumn(ResourceLocation id) {
        return new AsBlockColumnImpl.Key(id);
    }

    public static ConfiguredFeatureKey<WithTemplates> templates(ResourceLocation id) {
        return new WithTemplatesImpl.Key(id);
    }

    public static ConfiguredFeatureKey<NetherForrestVegetation> netherForrestVegetation(ResourceLocation id) {
        return new NetherForrestVegetationImpl.Key(id);
    }

    public static <F extends Feature<FC>, FC extends FeatureConfiguration> ConfiguredFeatureKey<WithConfiguration<F, FC>> configuration(
            ResourceLocation id,
            F feature
    ) {
        return new WithConfigurationImpl.Key<>(id, feature);
    }

    public static ConfiguredFeatureKey<FacingBlock> facingBlock(ResourceLocation id) {
        return new FacingBlockImpl.Key(id);
    }

    public static ConfiguredFeatureKey<WeightedBlockPatch> randomBlockPatch(ResourceLocation id) {
        return new WeightedBlockPatchImpl.Key(id);
    }

    public static ConfiguredFeatureKey<WeightedBlockPatch> bonemealPatch(ResourceLocation id) {
        return new WeightedBlockPatchImpl.KeyBonemeal(id);
    }

    public static ConfiguredFeatureKey<WeightedBlock> randomBlock(ResourceLocation id) {
        return new WeightedBlockImpl.Key(id);
    }

    public static ConfiguredFeatureKey<AsRandomSelect> randomFeature(ResourceLocation id) {
        return new AsRandomSelectImpl.Key(id);
    }

    public static ConfiguredFeatureKey<AsMultiPlaceRandomSelect> multiPlaceRandomSelect(ResourceLocation id) {
        return new AsMultiPlaceRandomSelectImpl.Key(id);
    }

    public interface InlineBuilder {
        AsOre ore();
        AsPillar pillar();
        AsSequence sequence();
        AsBlockColumn blockColumn();
        WithTemplates templates();
        NetherForrestVegetation netherForrestVegetation();
        <F extends Feature<FC>, FC extends FeatureConfiguration> WithConfiguration<F, FC> configuration(F feature);
        FacingBlock facingBlock();
        WeightedBlockPatch randomBlockPatch();
        WeightedBlockPatch bonemealPatch();
        WeightedBlock randomBlock();
        AsRandomSelect randomFeature();
        AsMultiPlaceRandomSelect multiPlaceRandomSelect();
        ForSimpleBlock simple();
        RandomPatch randomPatch();
    }

    @Nullable
    public static Holder<ConfiguredFeature<?, ?>> getHolder(
            @Nullable HolderGetter<ConfiguredFeature<?, ?>> getter,
            @NotNull ResourceKey<ConfiguredFeature<?, ?>> key
    ) {
        return FeatureConfiguratorImpl.getHolder(getter, key);
    }

    private ConfiguredFeatureManager() {
    }
}
