package de.ambertation.wover.feature.impl.configured;

import de.ambertation.wover.block.api.BlockHelper;
import de.ambertation.wover.feature.api.configured.FeatureKey;
import de.ambertation.wover.feature.api.configured.configurators.FacingBlock;
import de.ambertation.wover.feature.api.features.PlaceFacingBlockFeature;

import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FacingBlockImpl extends FeatureConfiguratorImpl implements de.ambertation.wover.feature.api.configured.configurators.FacingBlock {
    private final WeightedList.Builder<BlockState> stateBuilder = WeightedList.builder();
    BlockState firstState;
    private int count = 0;
    private List<Direction> directions = BlockHelper.HORIZONTAL;

    FacingBlockImpl(
            @Nullable BootstrapContext<Feature> ctx,
            @Nullable ResourceKey<Feature> key
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
    protected @NotNull Feature createFeature() {
        BlockStateProvider provider = null;
        if (count == 1) {
            provider = new SimpleStateProvider(firstState);
        } else {
            WeightedList<BlockState> list = stateBuilder.build();
            if (!list.isEmpty()) {
                provider = new WeightedStateProvider(list);
            }
        }

        if (provider == null) {
            throw new IllegalStateException("Facing Blocks need a State Provider.");
        }
        return new PlaceFacingBlockFeature(provider, directions);
    }

    public static class Key extends FeatureKey<FacingBlock> {
        public Key(Identifier id) {
            super(id);
        }

        @Override
        public FacingBlock bootstrap(@NotNull BootstrapContext<Feature> ctx) {
            return new FacingBlockImpl(ctx, key);
        }
    }
}
