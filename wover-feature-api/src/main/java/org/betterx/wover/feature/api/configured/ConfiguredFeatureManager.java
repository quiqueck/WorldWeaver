package org.betterx.wover.feature.api.configured;

import org.betterx.wover.events.api.Event;
import org.betterx.wover.events.api.types.OnBootstrapRegistry;
import org.betterx.wover.feature.api.configured.builders.FeatureConfigurator;
import org.betterx.wover.feature.api.configured.builders.ForSimpleBlock;
import org.betterx.wover.feature.api.configured.builders.RandomPatch;
import org.betterx.wover.feature.api.placed.PlacedFeatureKey;
import org.betterx.wover.feature.impl.configured.FeatureConfiguratorImpl;
import org.betterx.wover.feature.impl.configured.ForSimpleBlockImpl;
import org.betterx.wover.feature.impl.configured.InlineBuilderImpl;
import org.betterx.wover.feature.impl.configured.RandomPatchImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

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

    public interface InlineBuilder {
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
