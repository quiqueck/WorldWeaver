package de.ambertation.wover.feature.api.configured;

import de.ambertation.wover.events.api.Event;
import de.ambertation.wover.events.api.types.OnBootstrapRegistry;
import de.ambertation.wover.feature.api.configured.configurators.*;
import de.ambertation.wover.feature.api.placed.PlacedFeatureKey;
import de.ambertation.wover.feature.impl.configured.*;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Allows you to create a {@link ConfiguredFeatureKey} for {@link ConfiguredFeature}s. A {@link ConfiguredFeatureKey}
 * is (in general) a wrapper around the {@link ResourceKey} for a {@link ConfiguredFeature}.
 * <p>
 * {@link ConfiguredFeatureKey} can also be used to bootstrap a {@link ConfiguredFeature}. Configured Features
 * should be bootstrapped in the data generator whenever possible. However, if you need to bootstrap a
 * {@link ConfiguredFeature} in code, you can use the {@link #BOOTSTRAP_CONFIGURED_FEATURES} Event.
 */
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
     * in {@link PlacedFeatureKey#place(net.minecraft.data.worldgen.BootstrapContext, Holder)}
     * <p>
     * However, you should not normally need to use this, as you can create inline configurations using
     * {@link PlacedFeatureKey#inlineConfiguration(net.minecraft.data.worldgen.BootstrapContext)}.
     * <p>
     */
    public final static InlineBuilder INLINE_BUILDER = new InlineBuilderImpl();

    /**
     * Creates a Key for a simple block-state providing feature.
     *
     * @param id the id of the {@link ConfiguredFeature}
     * @return the new key
     * @see ForSimpleBlock
     */
    public static ConfiguredFeatureKey<ForSimpleBlock> simple(Identifier id) {
        return new ForSimpleBlockImpl.Key(id);
    }

    /**
     * Places given features in a random patch.
     *
     * @param id the id of the {@link ConfiguredFeature}
     * @return the new key
     * @see RandomPatch
     * deprecated: Vanilla removed the {@code random_patch} configured-feature type in Minecraft 26.1.
     * Use {@link de.ambertation.wover.feature.api.placed.FeaturePlacementBuilder#scatter(int, int, int, net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate)
     * FeaturePlacementBuilder.scatter(tries, xzSpread, ySpread, filter)} on a {@code simple_block} placed
     * feature instead ({@code CountPlacement.of(tries)} + {@code RandomOffsetPlacement.of(xz, y)} + an optional
     * {@code BlockPredicateFilter} + {@code BiomeFilter}). The {@code wover:random_patch} compat shim keeps this
     * functional until removal. See {@link RandomPatch} for details.
     */
    //TODO: @Deprecated(since = "26.1.0", forRemoval = true)
    public static ConfiguredFeatureKey<RandomPatch> randomPatch(Identifier id) {
        return new RandomPatchImpl.Key(id);
    }

    /**
     * Creates a new ore feature
     *
     * @param id the id of the {@link ConfiguredFeature}
     * @return the new key
     * @see AsOre
     */
    public static ConfiguredFeatureKey<AsOre> ore(Identifier id) {
        return new AsOreImpl.Key(id);
    }

    /**
     * Creates a new pillar feature.
     * <p>
     * Pillars use a state provider to pillar up a certain height. The vanilla gem uses this Feature to
     * generate Basalt Pillars
     *
     * @param id the id of the {@link ConfiguredFeature}
     * @return the new key
     * @see AsPillar
     */
    public static ConfiguredFeatureKey<AsPillar> pillar(Identifier id) {
        return new AsPillarImpl.Key(id);
    }

    /**
     * Creates a new sequence feature.
     * <p>
     * Sequences are a list of features that are placed in order.
     *
     * @param id the id of the {@link ConfiguredFeature}
     * @return the new key
     * @see AsSequence
     * @see de.ambertation.wover.feature.api.features.SequenceFeature
     */
    public static ConfiguredFeatureKey<AsSequence> sequence(Identifier id) {
        return new AsSequenceImpl.Key(id);
    }

    /**
     * Creates a new block column feature.
     * <p>
     * Creates multiple blocks ontop of each other. The vanilla game uses this feature for
     * example to generate Dripleaf, Cacti or Sugar Cane.
     *
     * @param id the id of the {@link ConfiguredFeature}
     * @return the new key
     * @see AsBlockColumn
     */
    public static ConfiguredFeatureKey<AsBlockColumn> blockColumn(Identifier id) {
        return new AsBlockColumnImpl.Key(id);
    }

    /**
     * Places prebuilt structures in the world.
     * <p>
     * Structures a randomly selected from the registered variants
     *
     * @param id the id of the {@link ConfiguredFeature}
     * @return the new key
     * @see WithTemplates
     * @see de.ambertation.wover.feature.api.features.TemplateFeature
     */
    public static ConfiguredFeatureKey<WithTemplates> templates(Identifier id) {
        return new WithTemplatesImpl.Key(id);
    }

    /**
     * Places blocks similar to vegetation in the nether
     *
     * @param id the id of the {@link ConfiguredFeature}
     * @return the new key
     * @see NetherForrestVegetation
     */
    public static ConfiguredFeatureKey<NetherForrestVegetation> netherForrestVegetation(Identifier id) {
        return new NetherForrestVegetationImpl.Key(id);
    }

    /**
     * Creates a custom feature
     *
     * @param id      the id of the {@link ConfiguredFeature}
     * @param feature the feature to use
     * @param <F>     the feature type
     * @param <FC>    the feature configuration type
     * @return the new key
     * @see WithConfiguration
     */
    public static <F extends Feature<FC>, FC extends FeatureConfiguration> ConfiguredFeatureKey<WithConfiguration<F, FC>> configuration(
            Identifier id,
            F feature
    ) {
        return new WithConfigurationImpl.Key<>(id, feature);
    }

    /**
     * Places Blocks with a FACING-Property
     *
     * @param id the id of the {@link ConfiguredFeature}
     * @return the new key
     */
    public static ConfiguredFeatureKey<FacingBlock> facingBlock(Identifier id) {
        return new FacingBlockImpl.Key(id);
    }

    /**
     * Places blocks (by weight) randomly in a patch
     * <p>
     * This is a simplified version of {@link #randomPatch(Identifier)}, as it will
     * only place blocks, not features.
     *
     * @param id the id of the {@link ConfiguredFeature}
     * @return the new key
     * @see WeightedBlockPatch
     * @deprecated Backed by the {@code random_patch} type removed by vanilla in Minecraft 26.1. Use
     * {@link de.ambertation.wover.feature.api.placed.FeaturePlacementBuilder#scatter(int, int, int, net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate)
     * FeaturePlacementBuilder.scatter(...)} on a {@code simple_block} placed feature instead (see
     * {@link WeightedBlockPatch}). The {@code wover:random_patch} compat shim keeps this functional until
     * removal.
     */
    @Deprecated(since = "26.1.0", forRemoval = true)
    public static ConfiguredFeatureKey<WeightedBlockPatch> randomBlockPatch(Identifier id) {
        return new WeightedBlockPatchImpl.Key(id);
    }

    /**
     * Places blocks (by weight) with the default bonemeal distribution. You can use this to
     * generate features when bone-mealing ground.
     *
     * @param id the id of the {@link ConfiguredFeature}
     * @return the new key
     * @see WeightedBlockPatch
     * deprecated: Backed by the {@code random_patch} type removed by vanilla in Minecraft 26.1. Use
     * {@link de.ambertation.wover.feature.api.placed.FeaturePlacementBuilder#scatter(int, int, int, net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate)
     * FeaturePlacementBuilder.scatter(...)} on a {@code simple_block} placed feature instead (see
     * {@link WeightedBlockPatch}). The {@code wover:random_patch} compat shim keeps this functional until
     * removal.
     */
    //@Deprecated(since = "26.1.0", forRemoval = true)
    public static ConfiguredFeatureKey<WeightedBlockPatch> bonemeal(Identifier id) {
        return new WeightedBlockPatchImpl.KeyBonemeal(id);
    }

    /**
     * Places blocks (by weight) with the default bonemeal distribution used in a nether forrest.
     * You can use this to generate features by bone-mealing ground in the nether.
     *
     * @param id the id of the {@link ConfiguredFeature}
     * @return the new key
     * @see WeightedBlockPatch
     */
    public static ConfiguredFeatureKey<NetherForrestVegetation> bonemealNetherForrest(Identifier id) {
        return new NetherForrestVegetationImpl.KeyBonemeal(id);
    }

    /**
     * Places a random block (by weight)
     *
     * @param id the id of the {@link ConfiguredFeature}
     * @return the new key
     * @see WeightedBlock
     */
    public static ConfiguredFeatureKey<WeightedBlock> randomBlock(Identifier id) {
        return new WeightedBlockImpl.Key(id);
    }

    /**
     * Places a random feature (by weight)
     *
     * @param id the id of the {@link ConfiguredFeature}
     * @return the new key
     * @see AsRandomSelect
     */
    public static ConfiguredFeatureKey<AsRandomSelect> randomFeature(Identifier id) {
        return new AsRandomSelectImpl.Key(id);
    }

    /**
     * Places a random feature (by weight). You can define a custom placement rule for all
     * (or select) features. Features are identified by a numeric value
     *
     * @param id the id of the {@link ConfiguredFeature}
     * @return the new key
     * @see AsMultiPlaceRandomSelect
     */
    public static ConfiguredFeatureKey<AsMultiPlaceRandomSelect> multiPlaceRandomFeature(Identifier id) {
        return new AsMultiPlaceRandomSelectImpl.Key(id);
    }

    /**
     * Interface for methods that return a Configured Feature Builder for a certain type.
     */
    public interface InlineBuilder {
        /**
         * Creates a new ore feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#ore(Identifier)
         */
        AsOre ore();
        /**
         * Creates a new pillar feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#pillar(Identifier)
         */
        AsPillar pillar();
        /**
         * Creates a new sequence feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#sequence(Identifier)
         */
        AsSequence sequence();
        /**
         * Creates a new block column feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#blockColumn(Identifier)
         */
        AsBlockColumn blockColumn();
        /**
         * Creates a new template feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#templates(Identifier)
         */
        WithTemplates templates();
        /**
         * Creates a new nether forrest vegetation feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#netherForrestVegetation(Identifier)
         */
        NetherForrestVegetation netherForrestVegetation();
        /**
         * Creates a new custom feature.
         *
         * @param feature the feature to use
         * @param <F>     the feature type
         * @param <FC>    the feature configuration type
         * @return the new builder
         * @see ConfiguredFeatureManager#configuration(Identifier, Feature)
         */
        <F extends Feature<FC>, FC extends FeatureConfiguration> WithConfiguration<F, FC> withFeature(F feature);

        /**
         * @param feature
         * @param <F>
         * @param <FC>
         * @return
         * @deprecated Use {@link #withFeature(Feature)} instead
         */
        @Deprecated(forRemoval = true)
        default <F extends Feature<FC>, FC extends FeatureConfiguration> WithConfiguration<F, FC> configuration(F feature) {
            return withFeature(feature);
        }
        /**
         * Creates a new oriented block feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#facingBlock(Identifier)
         */
        FacingBlock facingBlock();
        /**
         * Creates a new random block patch feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#randomBlockPatch(Identifier)
         * deprecated: Backed by the {@code random_patch} type removed by vanilla in Minecraft 26.1; use
         * {@link de.ambertation.wover.feature.api.placed.FeaturePlacementBuilder#scatter(int, int, int, net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate)
         * FeaturePlacementBuilder.scatter(...)} on a {@code simple_block} placed feature instead (see
         * {@link WeightedBlockPatch}).
         */
        //TODO: @Deprecated(since = "26.1.0", forRemoval = true)
        WeightedBlockPatch randomBlockPatch();
        /**
         * Creates a new bonemeal patch feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#bonemeal(Identifier)
         * deprecated: Backed by the {@code random_patch} type removed by vanilla in Minecraft 26.1; use
         * {@link de.ambertation.wover.feature.api.placed.FeaturePlacementBuilder#scatter(int, int, int, net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate)
         * FeaturePlacementBuilder.scatter(...)} on a {@code simple_block} placed feature instead (see
         * {@link WeightedBlockPatch}).
         */
        //TODO: @Deprecated(since = "26.1.0", forRemoval = true)
        WeightedBlockPatch bonemealPatch();
        /**
         * Creates a randomized block feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#randomBlock(Identifier)
         */
        WeightedBlock randomBlock();
        /**
         * Creates a randomized feature feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#randomFeature(Identifier)
         */
        AsRandomSelect randomFeature();
        /**
         * Creates a randomized feature feature with custom placement modificators.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#multiPlaceRandomFeature(Identifier)
         */
        AsMultiPlaceRandomSelect multiPlaceRandomFeature();
        /**
         * Creates a new simple block feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#simple(Identifier)
         */
        ForSimpleBlock simple();
        /**
         * Creates a new random patch feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#randomPatch(Identifier)
         * deprecated: Vanilla removed the {@code random_patch} type in Minecraft 26.1; use
         * {@link de.ambertation.wover.feature.api.placed.FeaturePlacementBuilder#scatter(int, int, int, net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate)
         * FeaturePlacementBuilder.scatter(...)} on a {@code simple_block} placed feature instead (see
         * {@link RandomPatch}).
         */
        //TODO: @Deprecated(since = "26.1.0", forRemoval = true)
        RandomPatch randomPatch();
    }

    /**
     * Gets the {@link Holder} for a {@link ConfiguredFeature} from a {@link HolderGetter}.
     *
     * @param getter the getter to get the holder from. You can get this getter from a
     *               {@link net.minecraft.data.worldgen.BootstrapContext} {@code ctx} by
     *               calling {@code ctx.lookup(Registries.CONFIGURED_FEATURE)}
     * @param key    the key to get the holder for
     * @return the holder, or null if the holder is not present
     */
    @Nullable
    public static Holder<ConfiguredFeature<?, ?>> getHolder(
            @Nullable HolderGetter<ConfiguredFeature<?, ?>> getter,
            @NotNull ResourceKey<ConfiguredFeature<?, ?>> key
    ) {
        return FeatureConfiguratorImpl.getHolder(getter, key);
    }

    /**
     * Gets the {@link Holder} for a {@link ConfiguredFeature} from a {@link BootstrapContext}.
     *
     * @param context the context to get registry containing the holder. When you need to
     *                get multiple holders at a time, you might want to use
     *                {@link #getHolder(HolderGetter, ResourceKey)} instead, as it will
     *                be slightly faster.
     * @param key     the key to get the holder for
     * @return the holder, or null if the holder is not present
     */
    @Nullable
    public static Holder<ConfiguredFeature<?, ?>> getHolder(
            @Nullable BootstrapContext<?> context,
            @NotNull ResourceKey<ConfiguredFeature<?, ?>> key
    ) {
        return getHolder(context.lookup(Registries.CONFIGURED_FEATURE), key);
    }


    private ConfiguredFeatureManager() {
    }
}
