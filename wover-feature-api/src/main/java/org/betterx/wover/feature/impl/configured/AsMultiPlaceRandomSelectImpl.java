package org.betterx.wover.feature.impl.configured;

import org.betterx.wover.block.api.BlockHelper;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureKey;
import org.betterx.wover.feature.api.configured.ConfiguredFeatureManager;
import org.betterx.wover.feature.api.configured.configurators.AsMultiPlaceRandomSelect;
import org.betterx.wover.feature.api.placed.FeaturePlacementBuilder;
import org.betterx.wover.util.Triple;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.RandomSelectorFeature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AsMultiPlaceRandomSelectImpl extends FeatureConfiguratorImpl<RandomFeatureConfiguration, RandomSelectorFeature> implements AsMultiPlaceRandomSelect {

    private final List<Triple<BlockStateProvider, Float, Integer>> features = new LinkedList<>();

    private Placer modFunction;

    private static int lastID = 0;

    AsMultiPlaceRandomSelectImpl(
            @Nullable BootstapContext<ConfiguredFeature<?, ?>> ctx,
            @Nullable ResourceKey<ConfiguredFeature<?, ?>> key
    ) {
        super(ctx, key);
    }

    @Override
    public AsMultiPlaceRandomSelect addAllStates(Block block, int weight) {
        return addAllStates(block, weight, lastID + 1);
    }

    @Override
    public AsMultiPlaceRandomSelect addAll(int weight, Block... blockSet) {
        return addAll(weight, lastID + 1, blockSet);
    }

    @Override
    public AsMultiPlaceRandomSelect addAllStatesFor(IntegerProperty prop, Block block, int weight) {
        return addAllStatesFor(prop, block, weight, lastID + 1);
    }

    @Override
    public AsMultiPlaceRandomSelect add(Block block, float weight) {
        return add(BlockStateProvider.simple(block), weight);
    }

    @Override
    public AsMultiPlaceRandomSelect add(BlockState state, float weight) {
        return add(BlockStateProvider.simple(state), weight);
    }

    @Override
    public AsMultiPlaceRandomSelect add(BlockStateProvider provider, float weight) {
        return add(provider, weight, lastID + 1);
    }


    @Override
    public AsMultiPlaceRandomSelect addAllStates(Block block, int weight, int id) {
        Set<BlockState> states = BlockHelper.getPossibleStates(block);
        SimpleWeightedRandomList.Builder<BlockState> builder = SimpleWeightedRandomList.builder();
        states.forEach(s -> builder.add(block.defaultBlockState(), 1));

        this.add(new WeightedStateProvider(builder.build()), weight, id);
        return this;
    }

    @Override
    public AsMultiPlaceRandomSelect addAll(int weight, int id, Block... blocks) {
        SimpleWeightedRandomList.Builder<BlockState> builder = SimpleWeightedRandomList.builder();
        for (Block block : blocks) {
            builder.add(block.defaultBlockState(), 1);
        }

        this.add(new WeightedStateProvider(builder.build()), weight, id);
        return this;
    }

    @Override
    public AsMultiPlaceRandomSelect addAllStatesFor(IntegerProperty prop, Block block, int weight, int id) {
        Collection<Integer> values = prop.getPossibleValues();
        SimpleWeightedRandomList.Builder<BlockState> builder = SimpleWeightedRandomList.builder();
        values.forEach(s -> builder.add(block.defaultBlockState().setValue(prop, s), 1));
        this.add(new WeightedStateProvider(builder.build()), weight, id);
        return this;
    }

    @Override
    public AsMultiPlaceRandomSelect add(Block block, float weight, int id) {
        return add(BlockStateProvider.simple(block), weight, id);
    }

    @Override
    public AsMultiPlaceRandomSelect add(BlockState state, float weight, int id) {
        return add(BlockStateProvider.simple(state), weight, id);
    }

    @Override
    public AsMultiPlaceRandomSelect add(BlockStateProvider provider, float weight, int id) {
        features.add(new Triple<>(provider, weight, id));
        lastID = Math.max(lastID, id);
        return this;
    }

    @Override
    public AsMultiPlaceRandomSelect placement(Placer placer) {
        this.modFunction = placer;
        return this;
    }

    private Holder<PlacedFeature> place(BlockStateProvider p, int id) {
        final FeaturePlacementBuilder builder = ConfiguredFeatureManager
                .INLINE_BUILDER
                .simple()
                .block(p)
                .inlinePlace();

        return modFunction.place(builder, id);
    }

    @Override
    public @NotNull RandomFeatureConfiguration createConfiguration() {
        if (modFunction == null) {
            throwStateError("AsMultiPlaceRandomSelect needs a placement.modification Function");
        }
        float sum = this.features.stream().map(p -> p.second).reduce(0.0f, Float::sum);
        List<WeightedPlacedFeature> features = this.features.stream()
                                                            .map(p -> new WeightedPlacedFeature(
                                                                    this.place(p.first, p.third),
                                                                    p.second / sum
                                                            ))
                                                            .toList();


        return new RandomFeatureConfiguration(
                features.subList(0, features.size() - 1),
                features.get(features.size() - 1).feature
        );
    }

    @Override
    protected @NotNull RandomSelectorFeature getFeature() {
        return (RandomSelectorFeature) Feature.RANDOM_SELECTOR;
    }

    public static class Key extends ConfiguredFeatureKey<AsMultiPlaceRandomSelect> {
        public Key(ResourceLocation id) {
            super(id);
        }

        @Override
        public AsMultiPlaceRandomSelect bootstrap(@NotNull BootstapContext<ConfiguredFeature<?, ?>> ctx) {
            return new AsMultiPlaceRandomSelectImpl(ctx, key);
        }
    }
}
