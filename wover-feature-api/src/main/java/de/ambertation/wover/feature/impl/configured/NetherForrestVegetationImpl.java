package de.ambertation.wover.feature.impl.configured;

import de.ambertation.wover.block.api.BlockHelper;
import de.ambertation.wover.feature.api.configured.FeatureKey;
import de.ambertation.wover.feature.api.configured.configurators.NetherForrestVegetation;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OverlayFeature;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.OffsetPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 26.3 deleted the {@code minecraft:nether_forest_vegetation} feature type together with
 * {@code NetherForestVegetationConfig}/{@code NetherForestVegetationFeature}. This configurator rebuilds
 * the old feature out of the pieces vanilla itself used when it migrated {@code warped_forest_vegetation},
 * {@code crimson_forest_vegetation} and {@code nylium_bonemeal}; the public API
 * ({@code spreadWidth}/{@code spreadHeight} plus the weighted block list) is unchanged.
 * <p>
 * Vanilla split the old feature into a bare {@link SimpleBlockFeature} plus a placement chain. For a
 * feature that is used from a placed feature ({@code warped_forest_vegetation}) that chain lives in the
 * placed feature; for one that is placed directly as a {@link Feature}, as {@code nylium_bonemeal} is,
 * vanilla wraps the same chain in an inline {@link PlacedFeature} inside an {@link OverlayFeature}. Since
 * this configurator produces a {@link Feature} that has to work in both roles, it always builds the
 * {@link OverlayFeature} form. The two are equivalent - {@code PlacedFeature#place} runs the same
 * modifier chain either way.
 * <p>
 * Mapping of the old configuration onto the new pieces, in placement order:
 * <ul>
 *   <li>{@link BlockPredicateFilter} on {@code #minecraft:nylium} at offset {@code (0,-1,0)} - 26.1
 *       checked the single block below the feature origin, once, before its loop.</li>
 *   <li>{@link CountPlacement} of {@code spreadWidth * spreadWidth} - the iteration count 26.1 used.</li>
 *   <li>{@link OffsetPlacement#ofTriangle(int, int)} of {@code spreadWidth - 1} / {@code spreadHeight - 1}.
 *       26.1 sampled {@code random.nextInt(n) - random.nextInt(n)}; a symmetric zero-plateau trapezoid over
 *       ±(n-1) is not merely the same shape, {@code TrapezoidInt#sample} short-circuits that exact case to
 *       {@code nextInt(max+1) - nextInt(max+1)}, so the two are the same expression.</li>
 *   <li>{@link BlockPredicateFilter} on {@code #minecraft:air} - 26.1 required an empty target block.</li>
 *   <li>{@link SimpleBlockFeature} itself calls {@code BlockState#canSurvive} before placing, which is
 *       what 26.1 did and what {@code RandomNeighborSpreadFeature} does not do. Without it plants are
 *       placed where they cannot live and vanish on the next block update.</li>
 * </ul>
 */
public class NetherForrestVegetationImpl extends FeatureConfiguratorImpl implements de.ambertation.wover.feature.api.configured.configurators.NetherForrestVegetation {
    private WeightedList.Builder<BlockState> blocks;
    private WeightedStateProvider stateProvider;
    private int spreadWidth = 8;
    private int spreadHeight = 4;

    NetherForrestVegetationImpl(
            @Nullable BootstrapContext<Feature> ctx,
            @Nullable ResourceKey<Feature> key
    ) {
        super(ctx, key);
    }


    @Override
    public NetherForrestVegetation spreadWidth(int width) {
        spreadWidth = width;
        return this;
    }

    @Override
    public NetherForrestVegetation spreadHeight(int height) {
        spreadHeight = height;
        return this;
    }

    @Override
    public NetherForrestVegetation addAllStates(Block block, int weight) {
        Set<BlockState> states = BlockHelper.getPossibleStates(block);
        states.forEach(s -> add(block.defaultBlockState(), Math.max(1, weight / states.size())));
        return this;
    }

    @Override
    public NetherForrestVegetation addAllStatesFor(IntegerProperty prop, Block block, int weight) {
        Collection<Integer> values = prop.getPossibleValues();
        values.forEach(s -> add(block.defaultBlockState().setValue(prop, s), Math.max(1, weight / values.size())));
        return this;
    }

    @Override
    public NetherForrestVegetation add(Block block, int weight) {
        return add(block.defaultBlockState(), weight);
    }

    @Override
    public NetherForrestVegetation add(BlockState state, int weight) {
        if (stateProvider != null) {
            throw new IllegalStateException("You can not add new state once a WeightedStateProvider was built. (" + state + ", " + weight + ")");
        }
        if (blocks == null) {
            blocks = WeightedList.builder();
        }
        blocks.add(state, weight);
        return this;
    }

    @Override
    public NetherForrestVegetation provider(WeightedStateProvider provider) {
        if (blocks != null) {
            throwStateError(
                    "You can not set a WeightedStateProvider after states were added manually.");
        }
        stateProvider = provider;
        return this;
    }

    /**
     * 26.1 required the block directly below the feature origin to be nylium. A tag-based
     * {@link BlockPredicate} needs no bound registry, unlike the {@code HolderSet} that
     * {@code RandomNeighborSpreadFeature} wanted, so this works unchanged during data generation.
     */
    private static final BlockPredicate ON_NYLIUM =
            BlockPredicate.matchesTag(Direction.DOWN.getUnitVec3i(), BlockTags.NYLIUM);

    @Override
    protected @NotNull Feature createFeature() {
        if (stateProvider == null && blocks == null) {
            throwStateError("A nether forrest vegetation feature needs at least one BlockState");
        }
        if (stateProvider == null) stateProvider = new WeightedStateProvider(blocks.build());

        final PlacedFeature spread = new PlacedFeature(
                Holder.direct(new SimpleBlockFeature(stateProvider)),
                List.of(
                        BlockPredicateFilter.forPredicate(ON_NYLIUM),
                        CountPlacement.of(spreadWidth * spreadWidth),
                        OffsetPlacement.ofTriangle(spreadWidth - 1, spreadHeight - 1),
                        BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)
                )
        );
        return new OverlayFeature(HolderSet.direct(Holder.direct(spread)));
    }

    public static class Key extends FeatureKey<NetherForrestVegetation> {
        public Key(Identifier id) {
            super(id);
        }

        @Override
        public NetherForrestVegetation bootstrap(@NotNull BootstrapContext<Feature> ctx) {
            return new NetherForrestVegetationImpl(ctx, key);
        }
    }

    public static class KeyBonemeal extends FeatureKey<NetherForrestVegetation> {
        public KeyBonemeal(Identifier id) {
            super(id);
        }

        @Override
        public NetherForrestVegetation bootstrap(@NotNull BootstrapContext<Feature> ctx) {
            return new NetherForrestVegetationImpl(ctx, key).spreadHeight(1).spreadWidth(3);
        }
    }
}
