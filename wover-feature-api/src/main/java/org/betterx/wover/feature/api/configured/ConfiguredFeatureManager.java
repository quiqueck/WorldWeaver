package org.betterx.wover.feature.api.configured;

import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.feature.api.configured.configurators.*;
import org.betterx.wover.feature.api.placed.PlacedFeatureKey;
import org.betterx.wover.feature.impl.configured.*;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
     * in {@link PlacedFeatureKey#place(net.minecraft.data.worldgen.BootstapContext, Holder)}
     * <p>
     * However, you should not normally need to use this, as you can create inline configurations using
     * {@link PlacedFeatureKey#inlineConfiguration(net.minecraft.data.worldgen.BootstapContext)}.
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
    public static ConfiguredFeatureKey<ForSimpleBlock> simple(ResourceLocation id) {
        return new ForSimpleBlockImpl.Key(id);
    }

    /**
     * Places given features in a random patch.
     *
     * @param id the id of the {@link ConfiguredFeature}
     * @return the new key
     * @see RandomPatch
     */
    public static ConfiguredFeatureKey<RandomPatch> randomPatch(ResourceLocation id) {
        return new RandomPatchImpl.Key(id);
    }

    /**
     * Creates a new ore feature
     *
     * @param id the id of the {@link ConfiguredFeature}
     * @return the new key
     * @see AsOre
     */
    public static ConfiguredFeatureKey<AsOre> ore(ResourceLocation id) {
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
    public static ConfiguredFeatureKey<AsPillar> pillar(ResourceLocation id) {
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
     * @see org.betterx.wover.feature.api.features.SequenceFeature
     */
    public static ConfiguredFeatureKey<AsSequence> sequence(ResourceLocation id) {
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
    public static ConfiguredFeatureKey<AsBlockColumn> blockColumn(ResourceLocation id) {
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
     * @see org.betterx.wover.feature.api.features.TemplateFeature
     */
    public static ConfiguredFeatureKey<WithTemplates> templates(ResourceLocation id) {
        return new WithTemplatesImpl.Key(id);
    }

    /**
     * Places blocks similar to vegetation in the nether
     *
     * @param id the id of the {@link ConfiguredFeature}
     * @return the new key
     * @see NetherForrestVegetation
     */
    public static ConfiguredFeatureKey<NetherForrestVegetation> netherForrestVegetation(ResourceLocation id) {
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
            ResourceLocation id,
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
    public static ConfiguredFeatureKey<FacingBlock> facingBlock(ResourceLocation id) {
        return new FacingBlockImpl.Key(id);
    }

    /**
     * Places blocks (by weight) randomly in a patch
     * <p>
     * This is a simplified version of {@link #randomPatch(ResourceLocation)}, as it will
     * only place blocks, not features.
     *
     * @param id the id of the {@link ConfiguredFeature}
     * @return the new key
     * @see WeightedBlockPatch
     */
    public static ConfiguredFeatureKey<WeightedBlockPatch> randomBlockPatch(ResourceLocation id) {
        return new WeightedBlockPatchImpl.Key(id);
    }

    /**
     * Places blocks (by weight) with the default bonemeal distribution. You can use this to
     * generate features that are used when applying bonemeal
     *
     * @param id the id of the {@link ConfiguredFeature}
     * @return the new key
     * @see WeightedBlockPatch
     */
    public static ConfiguredFeatureKey<WeightedBlockPatch> bonemealPatch(ResourceLocation id) {
        return new WeightedBlockPatchImpl.KeyBonemeal(id);
    }

    /**
     * Places a random block (by weight)
     *
     * @param id the id of the {@link ConfiguredFeature}
     * @return the new key
     * @see WeightedBlock
     */
    public static ConfiguredFeatureKey<WeightedBlock> randomBlock(ResourceLocation id) {
        return new WeightedBlockImpl.Key(id);
    }

    /**
     * Places a random feature (by weight)
     *
     * @param id the id of the {@link ConfiguredFeature}
     * @return the new key
     * @see AsRandomSelect
     */
    public static ConfiguredFeatureKey<AsRandomSelect> randomFeature(ResourceLocation id) {
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
    public static ConfiguredFeatureKey<AsMultiPlaceRandomSelect> multiPlaceRandomFeature(ResourceLocation id) {
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
         * @see ConfiguredFeatureManager#ore(ResourceLocation)
         */
        AsOre ore();
        /**
         * Creates a new pillar feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#pillar(ResourceLocation)
         */
        AsPillar pillar();
        /**
         * Creates a new sequence feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#sequence(ResourceLocation)
         */
        AsSequence sequence();
        /**
         * Creates a new block column feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#blockColumn(ResourceLocation)
         */
        AsBlockColumn blockColumn();
        /**
         * Creates a new template feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#templates(ResourceLocation)
         */
        WithTemplates templates();
        /**
         * Creates a new nether forrest vegetation feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#netherForrestVegetation(ResourceLocation)
         */
        NetherForrestVegetation netherForrestVegetation();
        /**
         * Creates a new custom feature.
         *
         * @param feature the feature to use
         * @param <F>     the feature type
         * @param <FC>    the feature configuration type
         * @return the new builder
         * @see ConfiguredFeatureManager#configuration(ResourceLocation, Feature)
         */
        <F extends Feature<FC>, FC extends FeatureConfiguration> WithConfiguration<F, FC> configuration(F feature);
        /**
         * Creates a new oriented block feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#facingBlock(ResourceLocation)
         */
        FacingBlock facingBlock();
        /**
         * Creates a new random block patch feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#randomBlockPatch(ResourceLocation)
         */
        WeightedBlockPatch randomBlockPatch();
        /**
         * Creates a new bonemeal patch feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#bonemealPatch(ResourceLocation)
         */
        WeightedBlockPatch bonemealPatch();
        /**
         * Creates a randomized block feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#randomBlock(ResourceLocation)
         */
        WeightedBlock randomBlock();
        /**
         * Creates a randomized feature feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#randomFeature(ResourceLocation)
         */
        AsRandomSelect randomFeature();
        /**
         * Creates a randomized feature feature with custom placement modificators.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#multiPlaceRandomFeature(ResourceLocation)
         */
        AsMultiPlaceRandomSelect multiPlaceRandomFeature();
        /**
         * Creates a new simple block feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#simple(ResourceLocation)
         */
        ForSimpleBlock simple();
        /**
         * Creates a new random patch feature.
         *
         * @return the new builder
         * @see ConfiguredFeatureManager#randomPatch(ResourceLocation)
         */
        RandomPatch randomPatch();
    }

    /**
     * Gets the {@link Holder} for a {@link ConfiguredFeature} from a {@link HolderGetter}.
     *
     * @param getter the getter to get the holder from. You can get this getter from a
     *               {@link net.minecraft.data.worldgen.BootstapContext} {@code ctx} by
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
     * Gets the {@link Holder} for a {@link ConfiguredFeature} from a {@link BootstapContext}.
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
            @Nullable BootstapContext<?> context,
            @NotNull ResourceKey<ConfiguredFeature<?, ?>> key
    ) {
        return getHolder(context.lookup(Registries.CONFIGURED_FEATURE), key);
    }

    private ConfiguredFeatureManager() {
    }
}
