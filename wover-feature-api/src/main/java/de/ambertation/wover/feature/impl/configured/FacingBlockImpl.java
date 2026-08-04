package de.ambertation.wover.feature.impl.configured;

import de.ambertation.wover.block.api.BlockHelper;
import de.ambertation.wover.feature.api.Features;
import de.ambertation.wover.feature.api.configured.ConfiguredFeatureKey;
import de.ambertation.wover.feature.api.configured.configurators.FacingBlock;
import de.ambertation.wover.feature.api.features.PlaceBlockFeature;
import de.ambertation.wover.feature.api.features.config.PlaceFacingBlockConfig;

import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FacingBlockImpl extends FeatureConfiguratorImpl<PlaceFacingBlockConfig, PlaceBlockFeature<PlaceFacingBlockConfig>> implements de.ambertation.wover.feature.api.configured.configurators.FacingBlock {
    private final WeightedList.Builder<BlockState> stateBuilder = WeightedList.builder();
    BlockState firstState;
    private int count = 0;
    private List<Direction> directions = BlockHelper.HORIZONTAL;

    FacingBlockImpl(
            @Nullable BootstrapContext<ConfiguredFeature<?, ?>> ctx,
            @Nullable ResourceKey<ConfiguredFeature<?, ?>> key
    ) {
        super(ctx, key);
    }


    @Override
    public FacingBlock allHorizontal() {
        directions = BlockHelper.HORIZONTAL;
        return this;
    }

    @Override
    public FacingBlock allVertical() {
        directions = BlockHelper.VERTICAL;
        return this;
    }

    @Override
    public FacingBlock allDirections() {
        directions = BlockHelper.ALL;
        return this;
    }

    @Override
    public FacingBlock add(Block block) {
        return add(block, 1);
    }

    @Override
    public FacingBlock add(BlockState state) {
        return this.add(state, 1);
    }

    @Override
    public FacingBlock add(Block block, int weight) {
        return add(block.defaultBlockState(), weight);
    }

    @Override
    public FacingBlock add(BlockState state, int weight) {
        if (firstState == null) firstState = state;
        count++;
        stateBuilder.add(state, weight);
        return this;
    }

    @Override
    public FacingBlock addAllStates(Block block, int weight) {
        Set<BlockState> states = BlockHelper.getPossibleStates(block);
        states.forEach(s -> add(s, Math.max(1, weight / states.size())));
        return this;
    }

    @Override
    public FacingBlock addAllStatesFor(IntegerProperty prop, Block block, int weight) {
        Collection<Integer> values = prop.getPossibleValues();
        values.forEach(s -> add(block.defaultBlockState().setValue(prop, s), Math.max(1, weight / values.size())));
        return this;
    }


    @Override
    public @NotNull PlaceFacingBlockConfig createConfiguration() {
        BlockStateProvider provider = null;
        if (count == 1) {
            provider = SimpleStateProvider.simple(firstState);
        } else {
            WeightedList<BlockState> list = stateBuilder.build();
            if (!list.isEmpty()) {
                provider = new WeightedStateProvider(list);
            }
        }

        if (provider == null) {
            throw new IllegalStateException("Facing Blocks need a State Provider.");
        }
        return new PlaceFacingBlockConfig(provider, directions);
    }

    @Override
    protected @NotNull PlaceBlockFeature<PlaceFacingBlockConfig> getFeature() {
        return (PlaceBlockFeature) Features.PLACE_BLOCK;
    }

    public static class Key extends ConfiguredFeatureKey<FacingBlock> {
        public Key(ResourceLocation id) {
            super(id);
        }

        @Override
        public FacingBlock bootstrap(@NotNull BootstrapContext<ConfiguredFeature<?, ?>> ctx) {
            return new FacingBlockImpl(ctx, key);
        }
    }
}
