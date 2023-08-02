package org.betterx.wover.feature.api.configured.builders;

import org.betterx.wover.feature.api.placed.FeaturePlacementBuilder;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.feature.RandomSelectorFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public interface AsMultiPlaceRandomSelect extends FeatureConfigurator<RandomFeatureConfiguration, RandomSelectorFeature> {
    AsMultiPlaceRandomSelect addAllStates(Block block, int weight);
    AsMultiPlaceRandomSelect addAll(int weight, Block... blocks);
    AsMultiPlaceRandomSelect addAllStatesFor(IntegerProperty prop, Block block, int weight);
    AsMultiPlaceRandomSelect add(Block block, float weight);
    AsMultiPlaceRandomSelect add(BlockState state, float weight);
    AsMultiPlaceRandomSelect add(BlockStateProvider provider, float weight);
    AsMultiPlaceRandomSelect addAllStates(Block block, int weight, int id);
    AsMultiPlaceRandomSelect addAll(int weight, int id, Block... blocks);
    AsMultiPlaceRandomSelect addAllStatesFor(IntegerProperty prop, Block block, int weight, int id);
    AsMultiPlaceRandomSelect add(Block block, float weight, int id);
    AsMultiPlaceRandomSelect add(BlockState state, float weight, int id);
    AsMultiPlaceRandomSelect add(BlockStateProvider provider, float weight, int id);
    AsMultiPlaceRandomSelect placement(Placer placer);
    @FunctionalInterface
    interface Placer {
        Holder<PlacedFeature> place(
                FeaturePlacementBuilder placer,
                int id
        );
    }
}
