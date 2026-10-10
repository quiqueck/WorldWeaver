package de.ambertation.wover.feature.api.features;

import de.ambertation.wover.block.api.BlockHelper;
import de.ambertation.wover.block.api.BlockProperties;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import org.jetbrains.annotations.NotNull;

/**
 * Creates a pillar of blocks (<b>{@code wover:pillar}</b>).
 * <p>
 * The pillar is created by placing blocks in a line in a given direction.
 * Here is a simple Java example that creates a Pillar (please note that this Block
 * can not actually be used, as it does not implement the correct size property):
 * <pre class="java"> FeatureKey&lt;AsPillar> TEST_KEY;
 *
 * TEST_KEY = FeatureContentManager.pillar(
 *     modCore.id("test")
 * );
 *
 * TEST_KEY.bootstrap(context)
 *         .allowedPlacement(BlockPredicate.ONLY_IN_AIR_OR_WATER_PREDICATE)
 *         .maxHeight(UniformInt.of(4, 8))
 *         .minHeight(3)
 *         .direction(Direction.UP)
 *         .transformer(PillarFeature.KnownTransformers.SIZE_INCREASE)
 *         .blockState(Blocks.LAPIS_BLOCK)
 *         .register();
 *  </pre>
 * The same feature can be created using JSON. Note that 26.3 collapsed {@code ConfiguredFeature} into
 * {@link Feature}, so the former {@code PillarFeatureConfig} is now part of this class and its fields sit at
 * the top level of the feature JSON instead of inside a {@code config} object:
 * <pre class="json"> {
 *   "type": "wover:pillar",
 *   "allowed_placement": {
 *     "type": "minecraft:matching_blocks",
 *     "blocks": [
 *       "minecraft:air",
 *       "minecraft:water"
 *     ]
 *   },
 *   "direction": "up",
 *   "max_height": {
 *     "type": "minecraft:uniform",
 *     "max_inclusive": 8,
 *     "min_inclusive": 4
 *   },
 *   "min_height": 3,
 *   "state": {
 *     "type": "minecraft:simple_state_provider",
 *     "state": {
 *       "Name": "minecraft:lapis_block"
 *     }
 *   },
 *   "transform": "size_increase"
 * }</pre>
 *
 * @see de.ambertation.wover.feature.api.Features#PILLAR
 */
public class PillarFeature implements Feature {
    /**
     * The state provider for the pillar blocks.
     */
    @FunctionalInterface
    public interface StateTransform {
        /**
         * Determin the BlockState for a given position.
         *
         * @param height     The height of the position within the Pillar
         * @param maxHeight  The maximum height of the pillar
         * @param inputState The input state
         * @param pos        The position in the world
         * @param rnd        The random source
         * @return The BlockState that should get placed
         */
        BlockState apply(int height, int maxHeight, BlockState inputState, BlockPos pos, RandomSource rnd);
    }

    /**
     * A predicate that checks the block above the pillar and determines if
     * the pillar can be placed at the given position.
     */
    @FunctionalInterface
    public interface PlacePredicate {
        /**
         * The default Predicate that always returns true.
         */
        PlacePredicate ALLWAYS = (min, max, start, above, level, allow, rnd) -> true;

        /**
         * Determines if a block should be placed.
         *
         * @param minHeight        The minimum height of the pillar
         * @param maxHeight        The maximum height of the pillar
         * @param startPos         The start position of the pillar
         * @param aboveTop         The position above the block that is going to be placed
         * @param level            The world
         * @param allowedPlacement The predicate that determines if a block is placeable
         * @param rnd              The random source
         * @return true if the block should be placed
         */
        boolean at(
                int minHeight,
                int maxHeight,
                BlockPos startPos,
                BlockPos aboveTop,
                WorldGenLevel level,
                BlockPredicate allowedPlacement,
                RandomSource rnd
        );
    }

    /**
     * The state provider for the pillar blocks.
     */
    public enum KnownTransformers implements StringRepresentable {
        /**
         * Decreases the size of the pillar towards the top. Only valid for Blocks providing {@link BlockProperties#SIZE}. The size is
         * set to 0 at the tip of the pillar and increases towards the bottom until it reaches 7.
         * <p>
         * Calculation: {@code Math.max(0, Math.min(7, maxHeight - height))}<br>
         * Type: {@code size_decrease}
         */
        SIZE_DECREASE(
                "size_decrease",
                (height, maxHeight, state, pos, rnd) -> state
                        .setValue(BlockProperties.SIZE, Math.max(0, Math.min(7, maxHeight - height)))
        ),

        /**
         * Increases the size of the pillar towards the top. Only valid for Blocks providing {@link BlockProperties#SIZE}. The size is
         * set to 0 at the bottom of the pillar and increases towards the top until it reaches 7.
         * <p>
         * Calculation: {@code Math.max(0, Math.min(7, height))}<br>
         * Type: {@code size_increase}
         */
        SIZE_INCREASE(
                "size_increase",
                (height, maxHeight, state, pos, rnd) -> state
                        .setValue(BlockProperties.SIZE, Math.max(0, Math.min(7, height)))
        ),
        /**
         * Sets the {@link BlockProperties#BOTTOM} property for the top of the Pillar.
         * The property is set to {@code true} at the top of the pillar and {@code false} for every other Block.
         * <p>
         * Calculation: {@code height == maxHeight}<br>
         * Type: {@code bottom_grow}
         */
        BOTTOM_GROW(
                "bottom_grow",
                (height, maxHeight, state, pos, rnd) -> state
                        .setValue(BlockProperties.BOTTOM, height == maxHeight)
        ),
        /**
         * Sets the {@link BlockProperties#BOTTOM} property for the bottom of the Pillar.
         * The property is set to {@code true} at the bottom of the pillar and {@code false} for every other Block.
         * <p>
         * Calculation: {@code height == 0}<br>
         * Type: {@code bottom}
         */
        BOTTOM(
                "bottom",
                (height, maxHeight, state, pos, rnd) -> state
                        .setValue(BlockProperties.BOTTOM, height == 0)
        ),
        /**
         * Chooses the correct {@link BlockProperties#TRIPLE_SHAPE} depending on the height.
         * The property is set to {@link BlockProperties.TripleShape#BOTTOM} at the bottom of the pillar,
         * {@link BlockProperties.TripleShape#TOP} at the top of the pillar and
         * {@link BlockProperties.TripleShape#MIDDLE} for every other Block.
         * <p>
         * This transform will also use a placement filter to determine if there is sufficient height
         * (see {@link KnownTransformers#canPlace}). In this test the block above the top of the pillar
         * is tested with the configured {@link BlockPredicate}. If the predicate returns false,
         * the pillar will not be placed.
         * <p>
         * Condition: {@code !allow.test(level, aboveTop)} (see {@link PlacePredicate})<br>
         * Type: {@code triple_shape_fill}
         */
        TRIPLE_SHAPE_FILL(
                "triple_shape_fill",
                (height, maxHeight, state, pos, rnd) -> {
                    if (height == 0)
                        return state.setValue(BlockProperties.TRIPLE_SHAPE, BlockProperties.TripleShape.BOTTOM);
                    if (height == maxHeight)
                        return state.setValue(BlockProperties.TRIPLE_SHAPE, BlockProperties.TripleShape.TOP);
                    return state.setValue(BlockProperties.TRIPLE_SHAPE, BlockProperties.TripleShape.MIDDLE);
                },
                (minHeight, maxHeight, startPos, aboveTop, level, allow, rnd) -> !allow.test(level, aboveTop)
        );


        /**
         * The codec for the {@link KnownTransformers} enum.
         */
        public static final EnumCodec<KnownTransformers> CODEC = StringRepresentable
                .fromEnum(KnownTransformers::values);


        /**
         * The name of the transformer.
         */
        public final String name;
        /**
         * The state transform function. This function determines the {@link BlockState} of the pillar
         * depending on the height in the pillar.
         */
        public final StateTransform stateTransform;
        /**
         * The place predicate. This predicate is evaluated one block above the tip of the
         * pillar (the block with maxHeight+1). If the predicate returns false, the pillar
         * will not be placed.
         */
        public final PlacePredicate canPlace;

        private KnownTransformers(String name, StateTransform stateTransform) {
            this(name, stateTransform, PlacePredicate.ALLWAYS);
        }

        private KnownTransformers(String name, StateTransform stateTransform, PlacePredicate canPlace) {
            this.name = name;
            this.stateTransform = stateTransform;
            this.canPlace = canPlace;
        }

        /**
         * Returns the name of the transformer.
         *
         * @return the name of the transformer.
         */
        @Override
        public String toString() {
            return this.name;
        }

        /**
         * Returns the name of the transformer.
         *
         * @return the name of the transformer.
         */
        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }

    /**
     * The codec for this feature. The field names are the ones the former {@code PillarFeatureConfig} used,
     * so existing data decodes unchanged apart from no longer being wrapped in a {@code config} object.
     */
    public static final MapCodec<PillarFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                    IntProviders.CODEC.fieldOf("min_height").forGetter(o -> o.minHeight),
                    IntProviders.CODEC.fieldOf("max_height").forGetter(o -> o.maxHeight),
                    Direction.CODEC.fieldOf("direction").orElse(Direction.UP).forGetter(o -> o.direction),
                    BlockPredicate.CODEC.fieldOf("allowed_placement").forGetter(o -> o.allowedPlacement),
                    BlockStateProvider.DIRECT_CODEC.fieldOf("state").forGetter(o -> o.stateProvider),
                    KnownTransformers.CODEC.fieldOf("transform").forGetter(o -> o.transformer)
            )
            .apply(instance, PillarFeature::new));

    /**
     * The maximum height of the pillar. The actual maximum height is calculated by
     * checking every possible height in the range [minHeight, maxHeight] and
     * checking if {@link #allowedPlacement} predicate returns true. The first non-true
     * height is the maximum height.
     */
    public final IntProvider maxHeight;
    /**
     * The minimum height of the pillar.
     */
    public final IntProvider minHeight;
    /**
     * The state provider. This provider determines the {@link BlockState} for a block in the
     * pillar before the #transformer is applied.
     */
    public final BlockStateProvider stateProvider;

    /**
     * The {@link BlockState} transformer. This transformer determines the {@link BlockState} for a block in the
     * pillar depending on the pillars height
     */
    public final KnownTransformers transformer;
    /**
     * The growing direction of the pillar.
     */
    public final Direction direction;

    /**
     * The placement predicate. This predicate determines if a block is allowed to be placed.
     */
    public final BlockPredicate allowedPlacement;

    /**
     * Constructs a new {@link PillarFeature} with the given parameters.
     *
     * @param minHeight        the minimum height of the pillar.
     * @param maxHeight        the maximum height of the pillar.
     * @param direction        the growing direction of the pillar.
     * @param allowedPlacement the placement predicate. This predicate determines if a block is allowed to be placed.
     * @param stateProvider    the state provider. This provider determines the {@link BlockState} for a block in the
     *                         pillar before the #transformer is applied.
     * @param transformer      the {@link BlockState} transformer. This transformer determines the {@link BlockState} for a block in the
     *                         pillar depending on the pillars height
     */
    public PillarFeature(
            IntProvider minHeight,
            IntProvider maxHeight,
            Direction direction,
            BlockPredicate allowedPlacement,
            BlockStateProvider stateProvider,
            KnownTransformers transformer
    ) {
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.stateProvider = stateProvider;
        this.transformer = transformer;
        this.direction = direction;
        this.allowedPlacement = allowedPlacement;
    }

    /**
     * The codec that serializes this feature.
     *
     * @return {@link #CODEC}
     */
    @Override
    public MapCodec<PillarFeature> codec() {
        return CODEC;
    }

    /**
     * Returns the BlockState for the given height.
     *
     * @param currentHeight height in the pillar where the BlockState is requested for.
     * @param maxHeight     the maximum height of the pillar
     * @param pos           the position the state is requested for
     * @param rnd           the random source
     * @param level         the level
     * @return the BlockState for the given height.
     */
    public BlockState transform(
            int currentHeight,
            int maxHeight,
            BlockPos pos,
            RandomSource rnd,
            WorldGenLevel level
    ) {
        BlockState state = stateProvider.getState(level, rnd, pos);
        return transformer.stateTransform.apply(currentHeight, maxHeight, state, pos, rnd);
    }

    /**
     * Places a pillar of blocks.
     *
     * @param level     the level to place in
     * @param generator the chunk generator
     * @param rnd       the random source
     * @param origin    the position to place at
     * @return {@code true} if the pillar was placed, {@code false} otherwise
     */
    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource rnd, BlockPos origin) {
        int height;
        int maxHeight = this.maxHeight.sample(rnd);
        int minHeight = this.minHeight.sample(rnd);
        BlockPos.MutableBlockPos posnow = origin.mutable();
        posnow.move(direction);

        for (height = 1; height < maxHeight; ++height) {
            if (!allowedPlacement.test(level, posnow)) {
                maxHeight = height;
                break;
            }
            posnow.move(direction);
        }
        if (maxHeight < minHeight) return false;

        if (!transformer.canPlace.at(
                minHeight,
                maxHeight,
                origin,
                posnow,
                level,
                allowedPlacement,
                rnd
        )) {
            return false;
        }
        posnow = origin.mutable();
        for (height = 0; height < maxHeight; ++height) {
            BlockState state = transform(height, maxHeight - 1, posnow, rnd, level);
            level.setBlock(posnow, state, BlockHelper.SET_SILENT);
            posnow.move(direction);
        }

        return true;
    }
}
