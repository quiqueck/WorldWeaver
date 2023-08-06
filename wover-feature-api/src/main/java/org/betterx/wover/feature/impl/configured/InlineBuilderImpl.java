package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.feature.api.configured.ConfiguredFeatureManager;
import org.betterx.wover.feature.api.configured.configurators.*;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import org.jetbrains.annotations.ApiStatus;

public class InlineBuilderImpl implements ConfiguredFeatureManager.InlineBuilder {
    private final ResourceKey<PlacedFeature> key;
    private final BootstapContext<PlacedFeature> bootstapContext;

    public InlineBuilderImpl() {
        this(null, null);
    }

    /**
     * For internal use only
     *
     * @param key             A Transitive key for the source {@link PlacedFeature}
     * @param bootstapContext A bootstrap context for the source {@link PlacedFeature}
     */
    @ApiStatus.Internal
    public InlineBuilderImpl(BootstapContext<PlacedFeature> bootstapContext, ResourceKey<PlacedFeature> key) {
        this.key = key;
        this.bootstapContext = bootstapContext;
    }

    @Override
    public AsOre ore() {
        final var res = new AsOreImpl(null, null);
        res.setTransitive(bootstapContext, key);
        return res;
    }

    @Override
    public AsPillar pillar() {
        final var res = new AsPillarImpl(null, null);
        res.setTransitive(bootstapContext, key);
        return res;
    }

    @Override
    public AsSequence sequence() {
        final var res = new AsSequenceImpl(null, null);
        res.setTransitive(bootstapContext, key);
        return res;
    }

    @Override
    public AsBlockColumn blockColumn() {
        final var res = new AsBlockColumnImpl(null, null);
        res.setTransitive(bootstapContext, key);
        return res;
    }

    @Override
    public WithTemplates templates() {
        final var res = new WithTemplatesImpl(null, null);
        res.setTransitive(bootstapContext, key);
        return res;
    }

    @Override
    public NetherForrestVegetation netherForrestVegetation() {
        final var res = new NetherForrestVegetationImpl(null, null);
        res.setTransitive(bootstapContext, key);
        return res;
    }

    @Override
    public <F extends Feature<FC>, FC extends FeatureConfiguration> WithConfiguration<F, FC> configuration(F feature) {
        final var res = new WithConfigurationImpl<F, FC>(null, null);
        res.feature(feature);
        res.setTransitive(bootstapContext, key);
        return res;
    }

    @Override
    public FacingBlock facingBlock() {
        final var res = new FacingBlockImpl(null, null);
        res.setTransitive(bootstapContext, key);
        return res;
    }

    @Override
    public WeightedBlockPatch randomBlockPatch() {
        final var res = new WeightedBlockPatchImpl(null, null);
        res.setTransitive(bootstapContext, key);
        return res;
    }

    @Override
    public WeightedBlockPatch bonemealPatch() {
        final var res = new WeightedBlockPatchImpl(null, null);
        res.likeDefaultBonemeal();
        res.setTransitive(bootstapContext, key);
        return res;
    }

    @Override
    public WeightedBlock randomBlock() {
        final var res = new WeightedBlockImpl(null, null);
        res.setTransitive(bootstapContext, key);
        return res;
    }

    @Override
    public AsRandomSelect randomFeature() {
        final var res = new AsRandomSelectImpl(null, null);
        res.setTransitive(bootstapContext, key);
        return res;
    }

    @Override
    public AsMultiPlaceRandomSelect multiPlaceRandomFeature() {
        final var res = new AsMultiPlaceRandomSelectImpl(null, null);
        res.setTransitive(bootstapContext, key);
        return res;
    }

    @Override
    public ForSimpleBlock simple() {
        final var res = new ForSimpleBlockImpl(null, null);
        res.setTransitive(bootstapContext, key);
        return res;
    }

    @Override
    public RandomPatch randomPatch() {
        final var res = new RandomPatchImpl(null, null);
        res.setTransitive(bootstapContext, key);
        return res;
    }
}
